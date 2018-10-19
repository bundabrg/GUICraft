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

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class YamlPackageRoot extends PackageRoot {

    private Path file;
    private boolean dirty = false;
    private Map<String, Path> packageFiles = new HashMap<>();

    /**
     * Create a PackageSection that is a child of the PackageConfiguration
     *
     * @param root
     * @param namespace
     * @param proxy
     */
    public YamlPackageRoot(PackageConfiguration root, String namespace, ConfigurationSection proxy) {
        super(root, namespace, proxy);
    }

    @Override
    public void set(String path, Object value) {
        super.set(path, value);
        dirty = true;
    }

    /**
     * Add a new package
     */
    public void addPackage(String namespace, Path file) {
        Validate.notNull(file, "File cannot be null");
        Validate.notNull(namespace, "Namespace cannot be null");


    }


    public static void loadConfiguration(PackageConfiguration config, String namespace, File file) throws IOException {
        loadConfiguration(config, namespace, file.toPath());
    }

    public static void loadConfiguration(PackageConfiguration config, String namespace, Path file) throws IOException {
        // If file is actually a directory we walk over it and find all YAML files
        if (Files.isDirectory(file)) {
            try {
                Files.walk(file)
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".yml"))
                        .forEach(p -> {
                            Path relativePath = file.relativize(p);
                            Path parent = relativePath.getParent();
                            String dir = parent == null ? "" : ("/" + parent.getFileName().toString());
                            try {
                                loadConfiguration(config, namespace + dir, p);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        YamlConfiguration c = YamlConfiguration.loadConfiguration(file.toFile());
        String fileName = file.getFileName().toString();
        config.addPackage(namespace + "/" + fileName.substring(0, fileName.length()-4), YamlPackageRoot.class, c);
    }
}
