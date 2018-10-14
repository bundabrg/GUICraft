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

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;

import java.util.ArrayList;
import java.util.List;

public class PackageConfigurationOptions extends ConfigurationOptions {
    private char fileSeparator = ':';
//    private char filePathSeparator = '/';
    private List<ConfigurationTranslator> translators = new ArrayList<>();
    private String defaultPath = "config";

    protected PackageConfigurationOptions(Configuration configuration) {
        super(configuration);
    }

    @Override
    public PackageConfiguration configuration() {
        return (PackageConfiguration) super.configuration();
    }

    public char fileSeparator() {
        return fileSeparator;
    }

    public PackageConfigurationOptions fileSeparator(char value) {
        this.fileSeparator = value;
        return this;
    }

//    public char filePathSeparator() {
//        return filePathSeparator;
//    }
//
//    public PackageConfiguration filePathSeparator(char value) {
//        this.filePathSeparator = value;
//        return this;
//    }

    public PackageConfigurationOptions registerTranslator(ConfigurationTranslator translator) {
        translators.add(translator);
        return this;
    }

    public PackageConfigurationOptions unregisterTranslator(ConfigurationTranslator translator) {
        translators.remove(translator);
        return this;
    }

    public List<ConfigurationTranslator> translators() {
        return this.translators;
    }

    public String defaultPath() {
        return defaultPath;
    }

    public PackageConfigurationOptions defaultPath(String path) {
        this.defaultPath = path;
        return this;
    }

}
