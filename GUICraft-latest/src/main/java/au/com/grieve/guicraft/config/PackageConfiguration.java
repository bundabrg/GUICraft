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
import java.util.Map;
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
    private Map<String, PackageConfiguration> packages;
    private PackageConfiguration root;
    private String path;


    //
    // Constructors
    //

    /**
     * Create an empty {@link PackageConfiguration} with no default values
     */
    public PackageConfiguration() {
        super();
        packages = new HashMap<>();
        path = "";
    }

    /**
     * Create a new ConfigurationPackage
     */
    public PackageConfiguration(PackageConfiguration root, Configuration config, String path) {
        super();
        copySection(config, this);
        this.root = root;
        this.path = path;
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
        load(file, "default");
    }

    /**
     * Load config from a file or directory. Their paths will be appended to the provided path
     */
    public void load(Path file, String path) throws IOException {
        // Make sure we are root
        if (getRoot() != this) {
            throw new IOException("Can only load on root object");
        }

        Validate.notNull(path, "Path cannot be null");
        Validate.notEmpty(path, "Path cannot be empty");
        Validate.notNull(file, "File cannot be null");

        // For directories we walk over it and recurse
        if (Files.isDirectory(file)) {
            Files.walk(file)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".yml"))
                    .forEach(p -> {
                        try {
                            String relativePath = file.relativize(p).toString();
                            load(p, path + "/" + relativePath.substring(0, relativePath.length() - 4));
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
        String realPath = path.replaceFirst("^/*", "");
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

    @Override
    public Object get(String path, Object def) {
        Location location = new Location(path);

        if (location.getFilePath() == null) {
            return translate(super.get(location.toString(), def));
        }

        // If not root we pass to root
        if (getRoot() != this) {
            return getRoot().get(location.toAbsolute().toString(), def);
        }

        // As root attempt to load
        String filePath = location.getFilePath().replaceFirst("^/*", "");
        if (!packages.containsKey(filePath)) {
            filePath = "default/" + filePath;
            if (!packages.containsKey(filePath)) {
                return def;
            }
        }

        return packages.get(filePath).get(location.getConfigPath());
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

    public ConfigurationSection getConfigurationSection(String path) {
        Object val = get(path, null);

        if (val == null) {
            val = get(path, getDefault(path));
            return (val instanceof ConfigurationSection) ? createSection(path) : null;
        }

        if (val instanceof ConfigurationSection) {
            return (ConfigurationSection) val;
        }

        // Check if its a reference variable
        if (val instanceof String) {
            Matcher matcher = Pattern.compile("\\$(?:([^{\\s]+)|(?:\\{)([^\\}]*)(?:}))").matcher((String) val);
            if (matcher.find()) {
                return (ConfigurationSection) getRoot().get(matcher.group(1) != null?matcher.group(1):matcher.group(2));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        Configuration root = getRoot();
        return new StringBuilder()
                .append(getClass().getSimpleName())
                .append("[path='")
                .append(new Location(path, getCurrentPath()).toString())
                .append("', root='")
                .append(root == null ? null : root.getClass().getSimpleName())
                .append("']")
                .toString();
    }

    public class Location {
        @Getter
        private String filePath;

        @Getter
        private String configPath;

        /**
         * Create a new location based on a raw string
         */
        public Location(String raw) {
            char fileSeparator = getRoot().options().fileSeparator();
            char pathSeparator = getRoot().options().pathSeparator();

            // /default/bob.test.123
            // default/bob.test.123
            // config
            // /bob

            int fileSeparatorLocation = raw.lastIndexOf(fileSeparator);

            if (fileSeparatorLocation == -1) {
                filePath = null;
                configPath =  raw;
            } else {
                int pathSeparatorLocation = raw.substring(fileSeparatorLocation+1).indexOf(pathSeparator);
                if (pathSeparatorLocation == -1) {
                    filePath = raw;
                    configPath = null;
                } else {
                    filePath = raw.substring(0, fileSeparatorLocation + 1 + pathSeparatorLocation);
                    configPath = raw.substring(fileSeparatorLocation + 1 + pathSeparatorLocation + 1);
                }
            }
        }

        public Location(String filePath, String configPath) {
            this.filePath = filePath;
            this.configPath = configPath;
        }

        public Location toAbsolute() {
            // No filePath? Return existing path
            if (filePath == null) {
                return new Location(path, configPath);
            }

            String fullPath;
            char fileSeparator = getRoot().options().fileSeparator();

            if (filePath.startsWith(String.valueOf(fileSeparator))) {
                fullPath = filePath;
            } else {
                fullPath = fileSeparator + path.substring(0, path.lastIndexOf(fileSeparator)) + fileSeparator + filePath;
            }

            Deque<String> stack = new ArrayDeque<>();
            for (String part : fullPath.split(String.valueOf(fileSeparator))) {
                if (part.equals("..")) {
                    if (!stack.isEmpty()) {
                        stack.pop();
                    }
                    continue;
                }
                stack.push(part);
            }

            // Generate path
            StringJoiner result = new StringJoiner(String.valueOf(fileSeparator));
            stack.descendingIterator().forEachRemaining(result::add);
            return new Location(result.toString(), configPath);
        }

        public String toString() {

            return filePath == null?configPath:(filePath + getRoot().options().pathSeparator() + configPath);
        }
    }


}
