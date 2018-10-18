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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * Provides a Package Configuration Class. This will package up multiple ConfigurationSections under a namespace
 */
public class PackageConfiguration extends PackageSection implements Configuration {
    // PackageConfiguration Options
    private PackageConfigurationOptions options;

    // Packages
    @Getter
    private Map<String, PackageSection> configurations = new HashMap<>();

    @Override
    public void addDefault(String s, Object o) {

    }

    @Override
    public void addDefaults(Map<String, Object> map) {

    }

    @Override
    public void addDefaults(Configuration configuration) {

    }

    @Override
    public void setDefaults(Configuration configuration) {

    }

    @Override
    public Configuration getDefaults() {
        return null;
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

    @Override
    public ConfigurationSection getParent() {
        return null;
    }

    public void addPackage(String namespace, Configuration config) {
        configurations.put(namespace, new PackageSection(this, namespace, config));
    }

    @Override
    public Object get(String path) {
        return get(path, null);
    }

    @Override
    public Object get(String path, Object def) {
        Validate.notNull(path, "Path cannot be null");
        Location location = getLocation().resolve(path);

        String filePath = location.getFullFile().replaceFirst("^" + options().fileSeparator() +"*", "");
        if (!configurations.containsKey(filePath)) {
            return def;
        }

        return configurations.get(filePath).get(location.getPath());
    }

    @Override
    public void set(String path, Object value) {

    }

    @Override
    public ConfigurationSection createSection(String path) {
        System.err.println("RootCreateSection: " + path);
        return null;
    }

    @Override
    public ConfigurationSection createSection(String path, Map<?, ?> map) {
        System.err.println("RootCreateSection: " + path);
        return null;
    }

//    @Override
//    public Set<String> getKeys(boolean deep) {
//        if (getRoot() != this) {
//            return super.getKeys(deep);
//        }
//
//        Set<String> result = new LinkedHashSet<>();
//        char pathSeparator = options().pathSeparator();
//
//        for (Map.Entry<String, PackageConfiguration> entry : configurations.entrySet()) {
//            String path = entry.getKey();
//
//            result.add(path);
//            if (deep) {
//                for (String item : entry.getValue().getKeys(true)) {
//                    result.add(path + pathSeparator + item);
//                }
//            }
//        }
//
//        return result;
//    }

    @Override
    public Set<String> getKeys(boolean deep) {
        System.err.println("RootGetKeys");
        return new LinkedHashSet<>();
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        System.err.println("RootGetValues");
        return new HashMap<>();
    }

    public PackageSection getConfigurationSection(String path) {
        return (PackageSection) super.getConfigurationSection(path);
    }

    @Override
    public String getCurrentPath() {
        return "/";
    }

    @Override
    public String getName() {
        System.err.println("RootGetName");
        return null;
    }

    @Override
    public PackageConfiguration getRoot() {
        return this;
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
