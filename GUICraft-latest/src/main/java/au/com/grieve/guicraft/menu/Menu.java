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

package au.com.grieve.guicraft.menu;

import au.com.grieve.bcf.ArgData;
import au.com.grieve.bcf.Parser;
import au.com.grieve.bcf.TreeNode;
import au.com.grieve.bcf.ValidArgument;
import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.config.PackageConfiguration;
import au.com.grieve.guicraft.config.PackageResolver;
import au.com.grieve.guicraft.config.PackageSection;
import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.menu.commands.MenuCommands;
import au.com.grieve.guicraft.menu.types.InventoryMenu;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Menu {

    @Getter
    private static Menu instance;

    @Getter
    private Map<String, Class<? extends MenuType>> menuTypes = new HashMap<>();

    public Menu() {
        GUICraft gui = GUICraft.getInstance();

        // Command Replacements
//        gui.getCommandManager().getCommandReplacements().addReplacement("menu", "menu|m");

        // Tab Completions
        gui.getCommandManager().registerParser("menu.config", new Parser() {
            @Override
            public ValidArgument isValid(CommandSender sender, List<String> args, TreeNode<ArgData> node) {
                String arg = args.remove(0);

                boolean constrain = node.data.getParameters().getOrDefault("constrain", "false").equalsIgnoreCase("true");

                if (args.size() == 0 || constrain) {
                    List<String> result = gui.getLocalConfig().getResolver("menu").getKeys().stream()
                            .filter(s -> s.startsWith(arg))
                            .collect(Collectors.toList());

                    if (result.size() == 0) {
                        return ValidArgument.INVALID();
                    }

                    if (args.size() == 0) {
                        return ValidArgument.PARTIAL(result);
                    }
                }

                return ValidArgument.VALID();
            }
        });

        gui.getCommandManager().registerParser("menu.package", new Parser() {
            @Override
            public ValidArgument isValid(CommandSender sender, List<String> args, TreeNode<ArgData> node) {
                String arg = args.remove(0);

                boolean constrain = node.data.getParameters().getOrDefault("constrain", "false").equalsIgnoreCase("true");

                if (args.size() == 0 || constrain) {
                    List<String> result = gui.getLocalConfig().getResolver("menu").getPackages().stream()
                            .filter(s -> s.startsWith(arg))
                            .collect(Collectors.toList());

                    if (result.size() == 0) {
                        return ValidArgument.INVALID();
                    }

                    if (args.size() == 0) {
                        return ValidArgument.PARTIAL(result);
                    }
                }

                return ValidArgument.VALID();
            }
        });

        // Menu Types
        registerMenuType("inventory", InventoryMenu.class);

        // Commands
        gui.getCommandManager().registerCommand(new MenuCommands());

    }

    /**
     * Register a MenuType
     */
    public void registerMenuType(String name, Class<? extends MenuType> type) {
        menuTypes.put(name, type);
    }

    /**
     * Unregister MenuType
     */
    public void unregisterMenuType(String name) {
        menuTypes.remove(name);
    }

    /**
     * Lookup an ItemType by variable
     */
    public MenuType resolveMenuType(String path) throws GUICraftException {
        PackageConfiguration config = GUICraft.getInstance().getLocalConfig();

        PackageSection section = config.getConfigurationSection(config.getResolver("menu").getPath(path));

        if (section == null || !section.contains("type") || !menuTypes.containsKey(section.getString("type"))) {
            throw new MenuException("Unable to resolve MenuType: " + path);
        }

        try {
            return menuTypes.get(section.getString("type")).getConstructor(ConfigurationSection.class)
                    .newInstance(section);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new MenuException("Failed to resolve MenuType: " + path, e);
        }
    }

    public static Menu getInstance() {
        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new Menu();
        }
    }

}
