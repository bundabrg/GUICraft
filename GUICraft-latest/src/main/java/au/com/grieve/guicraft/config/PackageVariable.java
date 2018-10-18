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
import java.util.Map;
import java.util.Set;

public class PackageVariable {

    private PackageConfiguration config;

    public PackageVariable(PackageConfiguration config) {
        this.config = config;
    }

    public Resolver getResolver(String file) {
        return new Resolver(file, null);
    }

    public Resolver getResolver(String file, String path) {
        return new Resolver(file, path);
    }


    /**
     * Resolver for a Variable
     */
    public class Resolver {
        @Getter
        private String file;
        @Getter
        private String path;

        public Resolver(String file, String path) {
            Validate.notNull(file);
            this.file = file;
            this.path = path;
        }

        /**
         * Return all the keys for all packages that match this resolver
         */
        public Set<String> getKeys() {
            Set<String> output = new LinkedHashSet<>();
            for (Map.Entry<String, PackageConfiguration> entry : config.getConfigurations().entrySet()) {
                PackageConfiguration.Location location = entry.getValue().getLocation();

                // Check if filename matches
                if (!location.getFile().equals(file)) {
                    continue;
                }

                ConfigurationSection section = entry.getValue();

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
            for (Map.Entry<String, PackageConfiguration> entry : config.getConfigurations().entrySet()) {
                PackageConfiguration.Location location = entry.getValue().getLocation();

                // Check if filename matches
                if (!location.getFile().equals(file)) {
                    continue;
                }

                ConfigurationSection section = entry.getValue();

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
                key = key.substring(loc+1);
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



//    public class Variable {
//        private final Resolver resolver;
//
//        @Getter
//        private final String path;
//
//        public Variable(Resolver resolver, String path) {
//            this.resolver = resolver;
//            this.path = path;
//        }
//
//    }

//    /**
//     * A variable.
//     *
//     * A variables format is:
//     *      [dir.dir.]key
//     */
//    public class Variable {
//
//        @Getter
//        private Resolver resolver;
//        @Getter
//        private String directory;
//        @Getter
//        private String key;
//
//        public Variable(String file, String path, String variablePath) {
//            initVariable(new Resolver(file, path), variablePath);
//        }
//        public Variable(Resolver resolver, String variablePath) {
//            initVariable(resolver, variablePath);
//        }
//
//        private void initVariable(Resolver resolver, String variablePath) {
//            this.resolver = resolver;
//            int loc = variablePath.lastIndexOf('.');
//
//            if (loc == -1) {
//                key = variablePath;
//                directory = null;
//            } else {
//                key = variablePath.substring(loc+1);
//                directory = variablePath.substring(0,loc);
//            }
//        }
//
//        public String toString() {
//            return (directory == null?"":directory + '.') + key;
//        }
//
////        public PackageConfiguration.Location toLocation() {
////            PackageConfigurationOptions options = getRoot().options();
////
////            return new Location(path.replace(options.variableSeparator(), options.fileSeparator())
////                    + options.fileSeparator()
////                    + location.replace(options.variableSeparator(), options.pathSeparator())
////                    + options.pathSeparator()
////                    + key
////                    );
////        }
//
//        public String toPath() {
//            PackageConfigurationOptions options = config.options();
//            String resolverString = resolver.toString();
//            StringBuilder result = new StringBuilder();
//
//            if (directory != null) {
//                result.append(directory.replace('.', options.fileSeparator()));
//                if (resolverString != null) {
//                    result.append(options.fileSeparator());
//                }
//            }
//
//            if (resolverString != null) {
//                result.append(resolverString).append(options.pathSeparator());
//            }
//            result.append(key);
//
//            return result.toString();
//        }
//
//
//    }
}
