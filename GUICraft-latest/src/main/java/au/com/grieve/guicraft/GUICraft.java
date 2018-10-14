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

package au.com.grieve.guicraft;

import au.com.grieve.guicraft.config.PackageConfiguration;
import au.com.grieve.multi_version_plugin.VersionPlugin;
import org.bukkit.configuration.ConfigurationSection;

public class GUICraft extends VersionPlugin {

    @Override
    public void onEnable() {
        PackageConfiguration config = PackageConfiguration.loadConfiguration(getDataFolder());
//        System.err.println("test1:");System.err.println(config.getString("test1"));
//        System.err.println("test2.test1: ");System.err.println(config.getString("test2.test1"));
//        System.err.println("config:test1: ");System.err.println(config.getString("config:test1"));
//        System.err.println("config:test2.test1: ");System.err.println(config.getString("config:test2.test1"));
//        System.err.println("config2:test1: ");System.err.println(config.getString("config2:test1"));
//        System.err.println("config2:test2.test1: ");System.err.println(config.getString("config2:test2.test1"));
//        System.err.println("dir/config:test1: ");System.err.println(config.getString("dir/config:test1"));
//        System.err.println("dir/config:test2.test: ");System.err.println(config.getString("dir/config:test2.test1"));
//
        System.err.println("config:test3: ");System.err.println(config.getString("config:test3"));
        System.err.println("config:test4.test1: ");System.err.println(config.getString("config:test4.test1"));
//        System.err.println("config:test4.test1.test2: ");System.err.println(config.getString("config:test4.test1.test2"));

//        ConfigurationSection section = config.getConfigurationSection("dir/config:");
//        section = section.getConfigurationSection("../config2:");
//        System.err.println("test1: " + section.get("test1"));
//        System.err.println("config2:test1: " + section.get("config2:test1"));
//        System.err.println("dir/yarn:test2.test1: " + section.get("dir/yarn:test2.test1"));
//        System.err.println("../config2:test2.test1: " + section.get("../config2:test2.test1"));
    }
}
