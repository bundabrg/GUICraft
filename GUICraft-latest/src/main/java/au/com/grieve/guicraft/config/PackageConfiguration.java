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
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Provides a Package Configuration Class. This will package up multiple ConfigurationSections under a namespace
 */
public abstract class PackageConfiguration extends PackageSection implements Configuration {
    //
    // Relevant for the Root Package
    //

    // PackageConfiguration Options
    private PackageConfigurationOptions options;

    // Packages
    @Getter
    private Map<String, Configuration> configurations;


    //
    // Relevant for All
    //
    private PackageConfiguration parent;
    private String namespace;
    private ConfigurationSection section;


    //
    // Constructors
    //



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
    public ConfigurationSection getParent() {
        return null;
    }

    @Override
    public Object get(String s) {
        return null;
    }

    public PackageConfiguration getLocalRoot() {
        return (PackageConfiguration) super.getRoot();
    }

    @Override
    public Object get(String path, Object def) {
        Validate.notNull(path, "Path cannot be null");
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
        if (!configurations.containsKey(filePath)) {
            return def;
        }

        return configurations.get(filePath).get(location.getPath());
    }

    @Override
    public void set(String s, Object o) {

    }

    @Override
    public ConfigurationSection createSection(String s) {
        return null;
    }

    @Override
    public ConfigurationSection createSection(String s, Map<?, ?> map) {
        return null;
    }

    @Override
    public String getString(String s) {
        return null;
    }

    @Override
    public String getString(String s, String s1) {
        return null;
    }

    @Override
    public boolean isString(String s) {
        return false;
    }

    @Override
    public int getInt(String s) {
        return 0;
    }

    @Override
    public int getInt(String s, int i) {
        return 0;
    }

    @Override
    public boolean isInt(String s) {
        return false;
    }

    @Override
    public boolean getBoolean(String s) {
        return false;
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

        for (Map.Entry<String, PackageConfiguration> entry : configurations.entrySet()) {
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

    @Override
    public Map<String, Object> getValues(boolean b) {
        return null;
    }

    @Override
    public boolean contains(String s) {
        return false;
    }

    @Override
    public boolean contains(String s, boolean b) {
        return false;
    }

    @Override
    public boolean isSet(String s) {
        return false;
    }

    @Override
    public String getCurrentPath() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    /**
     * Return the location of the current directory
     */
    public Location getLocation() {
        String path = getCurrentPath();

        return new Location(namespace + (path.length() > 0?getRoot().options().pathSeparator() + path:""));
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

}
