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

package au.com.grieve.guicraft.item;

import au.com.grieve.bcf.ArgData;
import au.com.grieve.bcf.Parser;
import au.com.grieve.bcf.ParserResult;
import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.config.PackageConfiguration;
import au.com.grieve.guicraft.config.PackageSection;
import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.item.commands.BukkitCommands;
import au.com.grieve.guicraft.item.types.BukkitItemType;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Item {
    @Getter
    private static Item instance;

    @Getter
    private Map<String, Class<? extends ItemType>> itemTypes = new HashMap<>();

    protected Item() {
        GUICraft gui = GUICraft.getInstance();

        // Command Replacements
//        gui.getCommandManager().getCommandReplacements().addReplacement("item", "item|i");
//        gui.getCommandManager().getCommandReplacements().addReplacement("itemsave", "save|s");

        // Tab Completions
        gui.getCommandManager().registerParser("item.config", new Parser() {
            @Override
            public ParserResult resolve(CommandSender sender, List<String> args, ArgData data) {
                ParserResult result = new ParserResult(data);

                if (args.size() == 0) {
                    return result;
                }

                String arg = args.remove(0);
                result.getArgs().add(arg);

                result.getCompletions().addAll(gui.getLocalConfig().getResolver("item").getKeys().stream()
                        .filter(s -> s.startsWith(arg))
                        .limit(20)
                        .collect(Collectors.toList()));

                result.setResult(gui.getLocalConfig().getResolver("item").getKeys().stream()
                        .filter(s -> s.equals(arg))
                        .findFirst()
                        .orElse(null));

                return result;
            }
        });

        gui.getCommandManager().registerParser("item.package", new Parser() {
            @Override
            public ParserResult resolve(CommandSender sender, List<String> args, ArgData data) {
                ParserResult result = new ParserResult(data);

                if (args.size() == 0) {
                    return result;
                }

                String arg = args.remove(0);
                result.getArgs().add(arg);

                result.getCompletions().addAll(gui.getLocalConfig().getResolver("item").getPackages().stream()
                        .filter(s -> s.startsWith(arg))
                        .limit(20)
                        .collect(Collectors.toList()));

                int index = arg.lastIndexOf('.');
                String pkg = index == -1 ? arg : arg.substring(0, index);

                result.setResult(gui.getLocalConfig().getResolver("item").getPackages().stream()
                        .filter(s -> s.equals(pkg) && index > arg.length())
                        .findFirst()
                        .map(s -> arg)
                        .orElse(null));

                return result;
            }
        });


        // Actions
//        gui.registerAction("open", new OpenAction());

        // Item Types
        registerItemType("bukkit", BukkitItemType.class);

        // Commands
        // Register a Command to manually execute this type
        GUICraft.getInstance().getCommandManager().registerCommand(new BukkitCommands());
    }

    /**
     * Register a new ItemType
     */
    public void registerItemType(String name, Class<? extends ItemType> type) {
        itemTypes.put(name, type);
    }

    /**
     * Unregister an Action
     */
    public void unregisterItemType(String name) {
        itemTypes.remove(name);
    }


    /**
     * Lookup an ItemType by variable
     */
    public ItemType resolveItemType(String path) throws GUICraftException {
        PackageConfiguration config = GUICraft.getInstance().getLocalConfig();

        PackageSection section = config.getConfigurationSection(config.getResolver("item").getPath(path));

        if (section == null || !section.contains("type") || !itemTypes.containsKey(section.getString("type"))) {
            throw new ItemException("Unable to resolve ItemType: " + path);
        }

        try {
            return itemTypes.get(section.getString("type")).getConstructor(ConfigurationSection.class)
                    .newInstance(section);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new ItemException("Unable to resolve ItemType: " + path, e);
        }
    }

    public static void init() {
        if (instance == null) {
            instance = new Item();
        }

    }

}
