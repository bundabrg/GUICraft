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

import au.com.grieve.guicraft.commands.GUICraftCommand;
import au.com.grieve.guicraft.config.PackageConfiguration;
import au.com.grieve.guicraft.config.PackageResolver;
import au.com.grieve.guicraft.config.YamlPackage;
import au.com.grieve.guicraft.item.Item;
import au.com.grieve.guicraft.menu.Menu;
import au.com.grieve.guicraft.vault.Vault;
import au.com.grieve.multi_version_plugin.VersionPlugin;
import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;

import java.io.IOException;
import java.util.LinkedHashSet;

public class GUICraft extends VersionPlugin {

    @Getter
    private static GUICraft instance;
    @Getter
    PackageConfiguration localConfig;
    // Variables
    @Getter
    private BukkitCommandManager commandManager;


    public GUICraft() {
        instance = this;
    }


    @Override
    public void onEnable() {

        // Setup Config
        initConfig();

        // Setup Command Manager
        initCommandManager();

        // Register Components
        initComponents();

        // Register all Commands
        registerCommands();

//        System.err.println("gc/config.test1: " + localConfig.get("gc/config.test1"));
//        System.err.println("gc/config.test2.test1: " + localConfig.get("gc/config.test2.test1"));
//        System.err.println("gc/config2.test1: " + localConfig.get("gc/config2.test1"));
//        System.err.println("gc/config2.test2.test1: " + localConfig.get("gc/config2.test2.test1"));
//        System.err.println("/gc/config2.test1: " + localConfig.get("/gc/config2.test1"));
//        System.err.println("/gc/config2.test2.test1: " + localConfig.get("/gc/config2.test2.test1"));
//        System.err.println("gc/dir/config.test1: " + localConfig.get("gc/dir/config.test1"));
//        System.err.println("gc/dir/config.test2.test1: " + localConfig.get("gc/dir/config.test2.test1"));
//        System.err.println("/gc/dir/config.test2.test1: " + localConfig.get("/gc/dir/config.test2.test1"));
//
//        ConfigurationSection section = localConfig.getConfigurationSection("/gc/dir/config");
//        System.err.println("../config.test1: " + section.get("../config.test1"));
//        System.err.println("gc/config.test3: " + localConfig.get("gc/config.test3"));
//        System.err.println("gc/config.test4.test1: " + localConfig.get("gc/config.test4.test1"));


    }

    private void initConfig() {
        localConfig = new PackageConfiguration();

        try {
            YamlPackage.loadConfiguration(localConfig, "gc", getDataFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCommandManager() {
        commandManager = new BukkitCommandManager(getPlugin());
        commandManager.enableUnstableAPI("help");

        // Replacements
        commandManager.getCommandReplacements().addReplacement("guicraft", "guicraft|gui|gc");

        // Tab Completions
        commandManager.getCommandCompletions().registerAsyncCompletion("config", c -> {
            String file = c.getConfig("file");
            String path = c.getConfig("path");

            PackageResolver resolver = localConfig.getResolver(file, path);
            return new LinkedHashSet<>(resolver.getKeys());
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("package", c -> {
            String file = c.getConfig("file");
            String append = c.getConfig("append");

            PackageResolver resolver = localConfig.getResolver(file);
            return new LinkedHashSet<>(resolver.getPackages());
        });
    }

    private void initComponents() {
        Item.init();
        Menu.init();
        Vault.init();
    }

    private void registerCommands() {
        commandManager.registerCommand(new GUICraftCommand());
    }

}
