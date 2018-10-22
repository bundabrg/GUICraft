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

package au.com.grieve.guicraft.item.types;

import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.config.PackageConfiguration;
import au.com.grieve.guicraft.config.PackageResolver;
import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.item.ItemException;
import au.com.grieve.guicraft.item.ItemType;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * A BukkitItem uses the built in bukkit serialization format. This should in theory support any item that does not
 * have external data.
 */
public class BukkitItemType implements ItemType {

    @Getter
    private ConfigurationSection config;

    public BukkitItemType(ConfigurationSection config) {
        this.config = config;

    }

    public static void saveItem(String path, ItemStack item) throws ItemException {
        PackageConfiguration config = GUICraft.getInstance().getLocalConfig();
        PackageResolver resolver = config.getResolver("item");

        ConfigurationSection section = config.createSection(resolver.getPath(path));

        section.set("data", item.serialize());
        section.set("type", "bukkit");
        config.save();

    }

    @Override
    public ItemStack toItemStack() throws GUICraftException {
        if (!config.getString("type", "").equals("bukkit")) {
            throw new ItemException("Not a Bukkit Item Type: " + config.getName());
        }
        if (!config.contains("data") || !config.isConfigurationSection("data")) {
            throw new ItemException("Invalid Bukkit Item Definition: " + config.getName());
        }

        return ItemStack.deserialize(config.getConfigurationSection("data").getValues(false));
    }

}
