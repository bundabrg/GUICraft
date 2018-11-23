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

import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.config.PackageConfiguration;
import au.com.grieve.guicraft.config.PackageSection;
import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.item.commands.BukkitCommands;
import au.com.grieve.guicraft.item.parsers.ItemConfigParser;
import au.com.grieve.guicraft.item.parsers.ItemPackageParser;
import au.com.grieve.guicraft.item.types.BukkitItemType;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
        gui.getBukkitCommandManager().registerParser("item.config", ItemConfigParser.class);
        gui.getBukkitCommandManager().registerParser("item.package", ItemPackageParser.class);

        // Item Types
        registerItemType("bukkit", BukkitItemType.class);

        // Commands
        // Register a Command to manually execute this type
        GUICraft.getInstance().getBukkitCommandManager().registerCommand(new BukkitCommands());
    }

    public static void init() {
        if (instance == null) {
            instance = new Item();
        }

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

}
