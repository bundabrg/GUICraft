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
import au.com.grieve.guicraft.config.PackageResolver;
import au.com.grieve.guicraft.config.PackageSection;
import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.item.types.BukkitItemType;
import au.com.grieve.guicraft.menu.actions.OpenAction;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Item {
    @Getter
    private static Item instance;

    @Getter
    private Map<String, ItemType> itemTypes = new HashMap<>();

    public Item() {
        instance = this;
        GUICraft gui = GUICraft.getInstance();

        // Command Replacements
        gui.getCommandManager().getCommandReplacements().addReplacement("item", "item|i");
        gui.getCommandManager().getCommandReplacements().addReplacement("itemsave", "save|s");

        // Tab Completions
        gui.getCommandManager().getCommandCompletions().registerAsyncCompletion("item.config", c -> {
            PackageResolver resolver = gui.getLocalConfig().getResolver("item");
            return new LinkedHashSet<>(resolver.getKeys());
        });
        gui.getCommandManager().getCommandCompletions().registerAsyncCompletion("item.package", c -> {
            PackageResolver resolver = gui.getLocalConfig().getResolver("item");
            return new LinkedHashSet<>(resolver.getPackages());
        });

        // Actions
//        gui.registerAction("open", new OpenAction());

        // Item Types
        registerItemType("bukkit", new BukkitItemType());
    }

    /**
     * Register a new ItemType
     */
    public void registerItemType(String name, ItemType type) {
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

        PackageSection section = config.getConfigurationSection(config.getResolver("item").getPath());

        if (section == null || !section.contains("type") || !itemTypes.containsKey(section.getString("type"))) {
            throw new ItemException("Unable to resolve ItemType: " + path);
        }

        return itemTypes.get(section.getString("type"));
    }

}
