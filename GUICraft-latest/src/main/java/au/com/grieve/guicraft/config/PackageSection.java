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
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Proxy Class to a ConfigurationSection provided by another class that is part of a
 * PackageConfiguration
 */
public class PackageSection extends MemorySection {
    protected ConfigurationSection proxy;
    protected PackageRoot rootNode;
    protected PackageConfiguration root;

    @Getter
    protected String namespace;

    protected PackageSection() {
        super();
        if (!(this instanceof PackageConfiguration)) {
            throw new IllegalStateException("Cannot construct a root PackageSection when not a PackageConfiguration");
        }
    }

    protected PackageSection(PackageConfiguration root, ConfigurationSection proxy) {
        super(root, proxy.getCurrentPath());
        this.root = root;
        this.proxy = proxy;
    }

    protected PackageSection(PackageRoot rootNode, ConfigurationSection proxy) {
        super(rootNode, proxy.getCurrentPath());
        this.rootNode = rootNode;
        this.root = rootNode.getRoot();
        this.proxy = proxy;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        if (!deep) {
            return proxy.getKeys(false);
        }

        Set<String> result = new LinkedHashSet<>();
        for (String key : proxy.getKeys(false)) {
            Object val = proxy.get(key);
            if (val instanceof ConfigurationSection) {
                result.addAll(getConfigurationSection(key).getKeys(true));
            } else {
                result.add(key);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        return getKeys(deep).stream()
                .collect(Collectors.toMap(x -> x, x -> {
                    if (proxy.get(x) instanceof ConfigurationSection) {
                        return new PackageSection(rootNode, (ConfigurationSection) proxy.get(x));
                    }
                    return proxy.get(x);
                }));
    }

    @Override
    public Object get(String path) {
        return get(path, null);
    }

    @Override
    public Object get(String path, Object def) {
        Validate.notNull(path, "Path cannot be null");

        if (path.length() == 0) {
            return this;
        }

        Location location = getLocation().resolve(path);

        // If we are responsible for this package
        if (location.getFullFile().equals(getLocation().getFullFile())) {
            final char separator = root.options().pathSeparator();
            // i1 is the leading (higher) index
            // i2 is the trailing (lower) index
            int i1 = -1, i2;
            PackageSection section = this;
            while ((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
                section = section.getConfigurationSection(path.substring(i2, i1));
                if (section == null) {
                    return def;
                }
            }

            String key = path.substring(i2);
            if (section.proxy == proxy) {
                Object val = proxy.get(key, null);
                if (val instanceof String) {
                    Matcher matcher = Pattern.compile("\\$(?:([^{\\s]+)|(?:\\{)([^\\}]*)(?:}))").matcher((String) val);
                    if (matcher.find()) {
                        return rootNode.get(matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
                    }
                }

                if (val instanceof ConfigurationSection) {
                    val = new PackageSection(rootNode, (ConfigurationSection) val);
                }

                return translate(val == null ? def : val);
            }
            return section.get(key, def);
        }

        return getRoot().get(location.toString(), def);
    }

    @Override
    public PackageSection getConfigurationSection(String path) {
        return (PackageSection) super.getConfigurationSection(path);
    }

    public void set(String path, Object value) {
        Validate.notEmpty(path, "Cannot set to an empty path");

        Configuration root = getRoot();
        if (root == null) {
            throw new IllegalStateException("Cannot use section without a root");
        }

        final char separator = root.options().pathSeparator();
        // i1 is the leading (higher) index
        // i2 is the trailing (lower) index
        int i1 = -1, i2;
        ConfigurationSection section = this;
        while ((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
            String node = path.substring(i2, i1);
            ConfigurationSection subSection = section.getConfigurationSection(node);
            if (subSection == null) {
                if (value == null) {
                    // no need to create missing sub-sections if we want to remove the value:
                    return;
                }
                section = section.createSection(node);
            } else {
                section = subSection;
            }
        }

        String key = path.substring(i2);
        rootNode.setDirty();
        proxy.set(key, value);
    }

    @Override
    public ConfigurationSection createSection(String path) {
        return createSection(path, new HashMap<>());
    }

    @Override
    public ConfigurationSection createSection(String path, Map<?, ?> map) {
        rootNode.setDirty();
        return new PackageSection(rootNode, proxy.createSection(path, map));
    }

    @Override
    public PackageConfiguration getRoot() {
        return root;
    }

    /**
     * Run input through all the translators
     */
    private Object translate(Object input) {
        for (ConfigurationTranslator translator : getRoot().options().translators()) {
            input = translator.translate(this, input);
        }
        return input;
    }

    @Override
    public String getCurrentPath() {
        return rootNode.getNamespace() + (proxy.getCurrentPath().length() == 0 ? "" : getRoot().options().pathSeparator() + proxy.getCurrentPath());
    }

    /**
     * Return the location of the current directory
     */
    public Location getLocation() {
        return new Location(getCurrentPath());
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
         * Format:
         * - [/dir/dir/][file[:path.path]]
         * - path
         * - path.path
         * - /dir/file
         * - /dir/file.path
         * - ./file
         * - ./file.path
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
