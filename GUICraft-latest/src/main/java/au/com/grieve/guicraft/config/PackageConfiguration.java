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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Provides a Package Configuration Class. This will package up multiple ConfigurationSections under a namespace
 */
public class PackageConfiguration extends PackageSection implements Configuration {
    // PackageConfiguration Options
    private PackageConfigurationOptions options;

    // Packages
    @Getter
    private Map<String, PackageRoot> configurations = new HashMap<>();

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
    public Configuration getDefaults() {
        return null;
    }

    @Override
    public void setDefaults(Configuration configuration) {

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

    public void addPackage(String namespace, PackageRoot config) {
        configurations.put(namespace, config);
    }

    @Override
    public Object get(String path) {
        return get(path, null);
    }

    @Override
    public Object get(String path, Object def) {
        Validate.notNull(path, "Path cannot be null");
        Location location = getLocation().resolve(path);

        try {
            return getPackage(location.getFullFile()).get(location.getPath(), def);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    @Override
    public void set(String path, Object value) {
        Validate.notNull(path, "Path cannot be null");
        Location location = getLocation().resolve(path);

        getPackage(location.getFullFile()).set(location.getPath(), value);

    }

    /**
     * Get a package given a file path
     */
    private PackageRoot getPackage(String filePath) {
        filePath = filePath.replaceFirst("^" + options().fileSeparator() + "*", "");
        if (!configurations.containsKey(filePath)) {
            throw new IllegalArgumentException("Cannot find package: " + filePath);
        }

        return configurations.get(filePath);
    }

    @Override
    public ConfigurationSection createSection(String path) {
        return createSection(path, new HashMap<>());
    }

    @Override
    public ConfigurationSection createSection(String path, Map<?, ?> map) {
        Validate.notNull(path, "Path cannot be null");
        Location location = getLocation().resolve(path);

        if (location.getPath().equals("")) {
            throw new IllegalArgumentException("Cannot create a root section");
        }

        return getPackage(location.getFullFile()).createSection(location.getPath(), map);
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
        if (!deep) {
            return configurations.keySet();
        }

        return configurations.entrySet().stream()
                .flatMap(x -> x.getValue().getKeys(true).stream()
                        .map(y -> x.getKey() + options().pathSeparator() + y))
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        return getKeys(deep).stream()
                .collect(Collectors.toMap(x -> x, this::get));
    }

    @Override
    public String getCurrentPath() {
        return "/";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public PackageConfiguration getRoot() {
        return this;
    }

    public PackageResolver getResolver(String file) {
        return getResolver(file, null);
    }

    public PackageResolver getResolver(String file, String path) {
        return new PackageResolver(this, file, path);
    }

    /**
     * Save all packages
     */
    public void save() {
        configurations.forEach((key, value) -> value.save());
    }

    @Override
    public String toString() {
        Configuration root = getRoot();
        return getClass().getSimpleName() +
                "[directory='" +
                getLocation().toString() +
                "', root='" +
                (root == null ? null : root.getClass().getSimpleName()) +
                "']";
    }


}
