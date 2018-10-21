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

package au.com.grieve.guicraft.menu.types;

import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.item.Item;
import au.com.grieve.guicraft.item.ItemException;
import au.com.grieve.guicraft.item.ItemType;
import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.menu.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryMenu implements MenuType {
    @Override
    public void open(Player player, ConfigurationSection section) throws GUICraftException {
        String title = section.getString("title", "Menu");
        int rows = Math.max(1, section.getInt("rows", 5));

        Inventory inventory = Bukkit.createInventory(player, 9 * rows, title);

        ConfigurationSection items = section.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                System.err.println("Key:" + key);
                if (!items.isConfigurationSection(key)) {
                    continue;
                }

                ConfigurationSection itemSection = items.getConfigurationSection(key);
                if (!itemSection.contains("type")) {
                    continue;
                }

                try {
                    ItemStack item = Item.getInstance().resolveItemType(itemSection.getString("type")).toItemStack(itemSection);
                    if (item == null) {
                        continue;
                    }

                    inventory.addItem(item);
                } catch (GUICraftException ignored) {
                }
            }
        }

        player.openInventory(inventory);
    }
}
