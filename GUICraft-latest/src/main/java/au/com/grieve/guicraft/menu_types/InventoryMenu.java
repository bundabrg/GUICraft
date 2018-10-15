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

package au.com.grieve.guicraft.menu_types;

import au.com.grieve.guicraft.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryMenu implements MenuType {
    @Override
    public void open(Player player, ConfigurationSection section) {
        String title = section.getString("title", "Menu");
        int rows = Math.max(1, section.getInt("rows", 5));

        Inventory inventory = Bukkit.createInventory(player, 9 * rows, title);
//        for(int i =0; i < size; i++) {
//            shopInventory.setItem(i, items.get(i));
//        }
        player.openInventory(inventory);
    }
}
