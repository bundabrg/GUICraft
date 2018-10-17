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

import au.com.grieve.guicraft.actions.OpenAction;
import au.com.grieve.guicraft.commands.GUICraftCommand;
import au.com.grieve.guicraft.config.PackageConfiguration;
import au.com.grieve.guicraft.config.PackageVariable;
import au.com.grieve.guicraft.menu_types.InventoryMenu;
import au.com.grieve.multi_version_plugin.VersionPlugin;
import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GUICraft extends VersionPlugin {

    @Getter
    private static GUICraft instance;
    @Getter
    PackageConfiguration localConfig;
    @Getter
    PackageVariable packageVariable;
    // Variables
    @Getter
    private BukkitCommandManager commandManager;
    @Getter
    private Map<String, GUIAction> actions = new HashMap<>();
    @Getter
    private Map<String, MenuType> menuTypes = new HashMap<>();

    public GUICraft() {
        instance = this;
    }


    @Override
    public void onEnable() {

        // Setup Config
        initConfig();

        // Setup Command Manager
        initCommandManager();

        // Register all Commands
        registerCommands();

        // Register Actions
        registerActions();

        // Register MenuTypes
        registerMenuTypes();


//        System.err.println("default/config.test1: " + localConfig.get("default/config.test1"));
//        System.err.println("default/config.test2.test1: " + localConfig.get("default/config.test2.test1"));
//        System.err.println("default/config2.test1: " + localConfig.get("default/config2.test1"));
//        System.err.println("default/config2.test2.test1: " + localConfig.get("default/config2.test2.test1"));
//        System.err.println("/default/config2.test1: " + localConfig.get("/default/config2.test1"));
//        System.err.println("/default/config2.test2.test1: " + localConfig.get("/default/config2.test2.test1"));
//        System.err.println("default/dir/config.test1: " + localConfig.get("default/dir/config.test1"));
//        System.err.println("default/dir/config.test2.test1: " + localConfig.get("default/dir/config.test2.test1"));
//        System.err.println("/default/dir/config.test2.test1: " + localConfig.get("/default/dir/config.test2.test1"));
//
//        ConfigurationSection section = localConfig.getConfigurationSection("/default/dir/config");
//        System.err.println("../config.test1: " + section.get("../config.test1"));
//        System.err.println("default/config.test3: " + localConfig.get("default/config.test3"));
//        System.err.println("default/config.test4.test1: " + localConfig.get("default/config.test4.test1"));


    }

    private void initConfig() {
        localConfig = PackageConfiguration.loadConfiguration(getDataFolder());
        packageVariable = new PackageVariable(localConfig);
    }

    private void initCommandManager() {
        commandManager = new BukkitCommandManager(getPlugin());
        commandManager.enableUnstableAPI("help");

        // Replacements
        commandManager.getCommandReplacements().addReplacement("guicraft", "guicraft|gui|gc");
        commandManager.getCommandReplacements().addReplacement("action", "action|a");

        // Tab Completions
        commandManager.getCommandCompletions().registerAsyncCompletion("config", c -> {
            String file = c.getConfig("file");
            String path = c.getConfig("path");

            Set<String> output = new LinkedHashSet<>();
            for (PackageVariable.Variable variable : packageVariable.getVariables(file, path)) {
                System.err.println("Var: " + variable.toString() + " - " + variable.toPath());
                output.add(variable.toString());
            }
            return output;
        });
//        commandManager.getCommandCompletions().registerAsyncCompletion("package", c -> {
//            String match = c.getConfig("file");
//            String append = c.getConfig("append");
//            Set<String> output = new LinkedHashSet<>();
//            for (String name : localConfig.getPackages().keySet()) {
//                System.err.println(name);
//
//                PackageConfiguration.Location location = localConfig.new Location(name);
//                if (match != null && !location.file().equals(match)) {
//                    continue;
//                }
//                output.add(location.removeDefault().directory());
//            }
//            return output;
//
//        });

    }

    private void registerCommands() {
        commandManager.registerCommand(new GUICraftCommand());
    }

    private void registerActions() {
        registerAction("open", new OpenAction());
    }

    private void registerMenuTypes() {
        registerMenuType("inventory", new InventoryMenu());
    }

    /**
     * Register a new Action
     */
    public void registerAction(String name, GUIAction action) {
        actions.put(name, action);
    }

    /**
     * Unregister an Action
     */
    public void unregisterAction(String name) {
        actions.remove(name);
    }

    /**
     * Register a MenuType
     */
    public void registerMenuType(String name, MenuType type) {
        menuTypes.put(name, type);
    }

    /**
     * Unregister MenuType
     */
    public void unregisterMenuType(String name) {
        menuTypes.remove(name);
    }

}
