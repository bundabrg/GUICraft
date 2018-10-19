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
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Resolver for a Variable
 */
public class PackageResolver {
    @Getter
    private String file;
    @Getter
    private String path;
    private PackageConfiguration config;

    public PackageResolver(PackageConfiguration config, String file, String path) {
        Validate.notNull(file);
        Validate.notNull(config);

        this.config = config;
        this.file = file;
        this.path = path;
    }

    /**
     * Return all the keys for all packages that match this resolver
     */
    public Set<String> getKeys() {
        Set<String> output = new LinkedHashSet<>();
        for (String namespace : config.getKeys(false)) {
            PackageConfiguration.Location location = config.getConfigurationSection(namespace).getLocation();

            // Check if filename matches
            if (!location.getFile().equals(file)) {
                continue;
            }

            ConfigurationSection section = config.getConfigurationSection(namespace);

            // Check if path exists
            if (path != null) {
                section = section.getConfigurationSection(path);
                if (section == null) {
                    continue;
                }
            }

            String variableDir = location.getDirectory().replace(config.options().fileSeparator(), '.');
            if (variableDir.length() > 0) {
                variableDir += ".";
            }

            // Get all Keys
            for (String key : section.getKeys(false)) {
                output.add(variableDir + key.replace(config.options().pathSeparator(), '.'));
            }
        }
        return output;
    }

    /**
     * Return a list of packages that match our spec
     */
    public Set<String> getPackages() {
        Set<String> output = new LinkedHashSet<>();
        for (String namespace : config.getKeys(false)) {
            PackageConfiguration.Location location = config.getConfigurationSection(namespace).getLocation();

            // Check if filename matches
            if (!location.getFile().equals(file)) {
                continue;
            }

            ConfigurationSection section = config.getConfigurationSection(namespace);

            // Check if path exists
            if (path != null) {
                if (section.getConfigurationSection(path) == null) {
                    continue;
                }
            }

            String dir = location.getDirectory().replace(config.options().fileSeparator(), '.');
            if (dir.length() == 0) {
                continue;
            }

            output.add(dir + ".");
        }
        return output;
    }

    /**
     * Return the fully qualified configuration path given path
     */
    public String getPath(String key) {


        StringBuilder result = new StringBuilder();


        int loc = key.lastIndexOf('.');

        if (loc != -1) {
            result.append(key.substring(0, loc).replace('.', config.options().fileSeparator()));
            result.append(config.options().fileSeparator());
            key = key.substring(loc + 1);
        }

        result.append(file);

        if (path != null) {
            result.append(config.options().pathSeparator());
            result.append(path.replace('.', config.options().pathSeparator()));
        }

        result.append(config.options().pathSeparator());
        result.append(key);

        return result.toString();
    }
}
