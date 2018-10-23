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

package au.com.grieve.guicraft.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ItemUtils {

    /**
     * Returns true if the inventory can store the items
     */
    public static boolean canStore(Inventory inventory, ItemStack... items) {
        ItemStack[] invRef = inventory.getStorageContents();
        System.err.println("Size: " + inventory.getSize());
        Inventory inv = Bukkit.createInventory(null, inventory.getSize(), "canStore");
        inv.setContents(invRef);

        for (ItemStack item : items) {
            if (item != null) {
                HashMap<Integer, ItemStack> extra = inv.addItem(item);
                if (!extra.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
