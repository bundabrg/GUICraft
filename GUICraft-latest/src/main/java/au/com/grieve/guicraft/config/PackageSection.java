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
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Proxy Class to a ConfigurationSection provided by another class that is part of a
 * PackageConfiguration
 */
public class PackageSection extends MemorySection {
    private ConfigurationSection proxy;
    private PackageConfiguration root;
    private String namespace;


    protected PackageSection() {
        super();
        if (!(this instanceof PackageConfiguration)) {
            throw new IllegalStateException("Cannot construct a root PackageSection when not a PackageConfiguration");
        }
    }

    /**
     * Create a PackageSection that is a child of the PackageConfiguration
     */
    protected PackageSection(PackageConfiguration root, String namespace, ConfigurationSection proxy) {
        this.proxy = proxy;
        this.root = root;
        this.namespace = namespace;
    }

    protected PackageSection(ConfigurationSection parent, String path) {
        super(parent, path);
    }

    /**
     * Retrieve the proxy configurationsection for this location
     */
    ConfigurationSection getProxy() {
        if (proxy != null) {
            return proxy;
        }

        return getParent().getPr
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return getProxy().getKeys(deep);
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        return getProxy().getValues(deep).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x -> {
                    if (x.getValue() instanceof ConfigurationSection) {
                        return new PackageSection(this, x.getKey(), (ConfigurationSection) x.getValue());
                    }
                    return x.getValue();
                }));

    }

    @Override
    public Object get(String path) {
        return get(path, null);
    }

    @Override
    public Object get(String path, Object def) {
        //@TODO
        System.err.println("Get: " + path);
        return null;
    }

    @Override
    public void set(String path, Object value) {
        //@TODO
    }

    @Override
    public ConfigurationSection createSection(String path) {
        //@TODO
        return section.createSection(path);
    }

    @Override
    public ConfigurationSection createSection(String path, Map<?, ?> map) {
        //@TODO
        return section.createSection(path, map);
    }

    @Override
    public String getString(String path) {
        Object def = getDefault(path);
        return getString(path, def != null ? def.toString() : null);
    }

    @Override
    public String getString(String path, String def) {
        return section.getString(path, def);
    }

    @Override
    public boolean isString(String path) {
        return section.isString(path);
    }

    @Override
    public int getInt(String path) {
        return section.getInt(path);
    }

    @Override
    public int getInt(String path, int def) {
        return section.getInt(path, def);
    }

    @Override
    public boolean isInt(String path) {
        return section.isInt(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return section.getBoolean(path);
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return false;
    }

    @Override
    public boolean isBoolean(String s) {
        return false;
    }

    @Override
    public double getDouble(String s) {
        return 0;
    }

    @Override
    public double getDouble(String s, double v) {
        return 0;
    }

    @Override
    public boolean isDouble(String s) {
        return false;
    }

    @Override
    public long getLong(String s) {
        return 0;
    }

    @Override
    public long getLong(String s, long l) {
        return 0;
    }

    @Override
    public boolean isLong(String s) {
        return false;
    }

    @Override
    public List<?> getList(String s) {
        return null;
    }

    @Override
    public List<?> getList(String s, List<?> list) {
        return null;
    }

    @Override
    public boolean isList(String s) {
        return false;
    }

    @Override
    public List<String> getStringList(String s) {
        return null;
    }

    @Override
    public List<Integer> getIntegerList(String s) {
        return null;
    }

    @Override
    public List<Boolean> getBooleanList(String s) {
        return null;
    }

    @Override
    public List<Double> getDoubleList(String s) {
        return null;
    }

    @Override
    public List<Float> getFloatList(String s) {
        return null;
    }

    @Override
    public List<Long> getLongList(String s) {
        return null;
    }

    @Override
    public List<Byte> getByteList(String s) {
        return null;
    }

    @Override
    public List<Character> getCharacterList(String s) {
        return null;
    }

    @Override
    public List<Short> getShortList(String s) {
        return null;
    }

    @Override
    public List<Map<?, ?>> getMapList(String s) {
        return null;
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String s, Class<T> aClass) {
        return null;
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String s, Class<T> aClass, T t) {
        return null;
    }

    @Override
    public Vector getVector(String s) {
        return null;
    }

    @Override
    public Vector getVector(String s, Vector vector) {
        return null;
    }

    @Override
    public boolean isVector(String s) {
        return false;
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String s) {
        return null;
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public boolean isOfflinePlayer(String s) {
        return false;
    }

    @Override
    public ItemStack getItemStack(String s) {
        return null;
    }

    @Override
    public ItemStack getItemStack(String s, ItemStack itemStack) {
        return null;
    }

    @Override
    public boolean isItemStack(String s) {
        return false;
    }

    @Override
    public Color getColor(String s) {
        return null;
    }

    @Override
    public Color getColor(String s, Color color) {
        return null;
    }

    @Override
    public boolean isColor(String s) {
        return false;
    }

    @Override
    public ConfigurationSection getConfigurationSection(String s) {
        return null;
    }

    @Override
    public boolean isConfigurationSection(String s) {
        return false;
    }

    @Override
    public ConfigurationSection getDefaultSection() {
        return null;
    }

    @Override
    public void addDefault(String s, Object o) {

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
