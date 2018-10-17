/*
 * GUICraft - The Ultimate GUI System
 * Copyright (C) 2018 bundabrg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package au.com.grieve.guicraft.config;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Provides a Package Configuration Class. This allows multiple YML files to be part of a greater structure
 * and to be able to reference each other through variables.
 */
public class PackageConfiguration extends MemorySection implements Configuration {
    //
    // Relevant for the Root Package
    //

    // PackageConfiguration Options
    private PackageConfigurationOptions options;

    // Packages
    @Getter
    private Map<String, PackageConfiguration> packages;
    private PackageConfiguration root;
    private String directory;


    //
    // Constructors
    //

    /**
     * Create an empty {@link PackageConfiguration} with no default values
     */
    public PackageConfiguration() {
        super();
        packages = new HashMap<>();
        directory = "";
    }

    /**
     * Create a new ConfigurationPackage
     */
    public PackageConfiguration(PackageConfiguration root, Configuration config, String directory) {
        super();
        copySection(config, this);
        this.root = root;
        this.directory = directory;
    }

    public static PackageConfiguration loadConfiguration(File path) {
        return loadConfiguration(path.toPath());
    }

    public static PackageConfiguration loadConfiguration(Path path) {
        PackageConfiguration config = new PackageConfiguration();

        try {
            config.load(path);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load Configuration from directory: " + path.toString(), e);
        }

        return config;
    }

    @Override
    public void addDefault(String s, Object o) {

    }

    @Override
    public void addDefaults(Map<String, Object> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDefaults(Configuration configuration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Configuration getDefaults() {
        return null;
    }

    @Override
    public void setDefaults(Configuration configuration) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the {@link PackageConfigurationOptions} for this Package
     */
    @Override
    public PackageConfigurationOptions options() {
        if (options == null) {
            options = new PackageConfigurationOptions(this);
        }
        return options;
    }

    public void load(Path file) throws IOException {
        load(file, options().defaultPath());
    }

    /**
     * Load config from a file or directory. Their paths will be appended to the provided directory
     */
    public void load(Path file, String directory) throws IOException {
        // Make sure we are root
        if (getRoot() != this) {
            throw new IOException("Can only load on root object");
        }

        Validate.notNull(directory, "Directory cannot be null");
        Validate.notEmpty(directory, "Directory cannot be empty");
        Validate.notNull(file, "File cannot be null");

        // For directories we walk over it and recurse
        if (Files.isDirectory(file)) {
            Files.walk(file)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".yml"))
                    .forEach(p -> {
                        try {
                            String relativePath = file.relativize(p).toString();
                            load(p, directory + "/" + relativePath.substring(0, relativePath.length() - 4));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            return;
        }

        if (!Files.isRegularFile(file)) {
            throw new IOException("Not a file: " + file.toString());
        }

        // Load config
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file.toFile());
        String realPath = directory.replaceFirst("^/*", "");
        packages.put(realPath, new PackageConfiguration(this, config, realPath));
    }

    protected void copySection(ConfigurationSection input, ConfigurationSection section) {
        for (String key : input.getKeys(false)) {
            Object value = input.get(key);

            if (value instanceof ConfigurationSection) {
                copySection((ConfigurationSection) value, section.createSection(key));
            } else {
                section.set(key, value);
            }
        }
    }

    @Override
    public PackageConfiguration getRoot() {
        if (root != null) {
            return root;
        } else {
            return (PackageConfiguration) super.getRoot();
        }
    }

    public PackageConfiguration getLocalRoot() {
        return (PackageConfiguration) super.getRoot();
    }

    @Override
    public Object get(String path, Object def) {
        Location location = getLocation().resolve(path);

        // If we are responsible for this package
        if (location.getFullFile().equals(getLocation().getFullFile())) {
            Object val = super.get(location.getPath(), null);
            if (val instanceof String) {
                Matcher matcher = Pattern.compile("\\$(?:([^{\\s]+)|(?:\\{)([^\\}]*)(?:}))").matcher((String) val);
                if (matcher.find()) {
                    return getLocalRoot().get(matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
                }
            }

            return translate(val==null?def:val);
        }

        // If not root we pass to root
        if (getRoot() != this) {
            return getRoot().get(location.toString(), def);
        }

        // As root attempt to load
        String filePath = location.getFullFile().replaceFirst("^/*", "");
        if (!packages.containsKey(filePath)) {
            return def;
        }

        return packages.get(filePath).get(location.getPath());
    }

    /**
     * Run input through all the translators
     */
    private Object translate(Object input) {
        for (ConfigurationTranslator translator : options().translators()) {
            input = translator.translate(this, input);
        }
        return input;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        if (getRoot() != this) {
            return super.getKeys(deep);
        }

        Set<String> result = new LinkedHashSet<>();
        char pathSeparator = options().pathSeparator();

        for (Map.Entry<String, PackageConfiguration> entry : packages.entrySet()) {
            String path = entry.getKey();

            result.add(path);
            if (deep) {
                for (String item : entry.getValue().getKeys(true)) {
                    result.add(path + pathSeparator + item);
                }
            }
        }

        return result;
    }

    /**
     * Return the location of the current directory
     */
    public Location getLocation() {
        String path = getCurrentPath();

        return new Location(directory + (path.length() > 0?getRoot().options().pathSeparator() + path:""));
    }




    @Override
    public String toString() {
        Configuration root = getRoot();
        return new StringBuilder()
                .append(getClass().getSimpleName())
                .append("[directory='")
                .append(getLocation().toString())
                .append("', root='")
                .append(root == null ? null : root.getClass().getSimpleName())
                .append("']")
                .toString();
    }

    public class Location {

        @Getter
        private final String directory;

        @Getter
        private final String file;

        @Getter
        private final String path;

        /**
         * Create a new location based on a raw string
         */
        public Location(String raw) {
            String[] data = splitRaw(raw);
            directory = data[0];
            file = data[1];
            path = data[2];
        }

        private Location(String directory, String file, String path) {
            this.directory = directory;
            this.file = file;
            this.path = path;
        }

        public Location resolve(String raw) {
            String[] data = splitRaw(raw);
            PackageConfigurationOptions options = getRoot().options();

            if (data[0].length() > 0) {
                // Absolute directory?
                if (data[0].startsWith(String.valueOf(options.fileSeparator()))) {
                    return new Location(data[0], data[1], data[2]);
                }

                // Get Relative directory
                String resolveDirectory = directory + options.fileSeparator() + data[0];

                Deque<String> stack = new ArrayDeque<>();
                for (String part : resolveDirectory.split(String.valueOf(options.fileSeparator()))) {
                    if (part.equals("..")) {
                        if (!stack.isEmpty()) {
                            stack.pop();
                        }
                        continue;
                    }
                    stack.push(part);
                }

                // Generate directory
                StringJoiner result = new StringJoiner(String.valueOf(options.fileSeparator()));
                stack.descendingIterator().forEachRemaining(result::add);
                return new Location(result.toString(), data[1], data[2]);
            }

            if (data[1].length() > 0) {
                return new Location(directory, data[1], data[2]);
            }

            return new Location(directory, file, data[2]);
        }

        /**
         * Take a location string and split it into its components
         *  Format:
         *    - [/dir/dir/][file[:path.path]]
         *    - path
         *    - path.path
         *    - /dir/file
         *    - /dir/file.path
         *    - ./file
         *    - ./file.path
         */
        private String[] splitRaw(String raw) {
            Validate.notNull(raw);
            char fileSeparator = getRoot().options().fileSeparator();
            char pathSeparator = getRoot().options().pathSeparator();
            String resolveDirectory;
            String resolveFile;
            String resolvePath;

            int fileSeparatorLocation = raw.lastIndexOf(fileSeparator);

            if (fileSeparatorLocation == -1) {
                resolveDirectory = "";
                resolveFile = "";
                resolvePath = raw;
            } else {
                resolveDirectory = raw.substring(0, fileSeparatorLocation);
                raw = raw.substring(fileSeparatorLocation + 1);

                int pathSeparatorLocation = raw.indexOf(pathSeparator);

                if (pathSeparatorLocation == -1) {
                    resolveFile = raw;
                    resolvePath = "";
                } else {
                    resolveFile = raw.substring(0, pathSeparatorLocation);
                    resolvePath = raw.substring(pathSeparatorLocation + 1);
                }
            }

            return new String[]{resolveDirectory, resolveFile, resolvePath};
        }

        public String toString() {
            PackageConfigurationOptions options = getRoot().options();
            StringBuilder result = new StringBuilder();

            result.append(getFullFile());

            if (path.length() > 0) {
                if (result.length() > 0) {
                    result.append(options.pathSeparator());
                }
                result.append(path);
            }

            return result.toString();
        }

        public String getFullFile() {
            PackageConfigurationOptions options = getRoot().options();
            StringBuilder result = new StringBuilder();

            if (directory.length() > 0) {
                result.append(directory);
            }

            if (file.length() > 0) {
                if (directory.length() > 0) {
                    result.append(options.fileSeparator());
                }
                result.append(file);
            }

            return result.toString();
        }
    }


}
