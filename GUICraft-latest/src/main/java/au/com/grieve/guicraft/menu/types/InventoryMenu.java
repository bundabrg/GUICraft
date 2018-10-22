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
import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.item.Item;
import au.com.grieve.guicraft.item.ItemType;
import au.com.grieve.guicraft.menu.MenuType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryMenu implements MenuType, Listener {

    private Inventory inventory;

    @Getter
    private ConfigurationSection config;

    public InventoryMenu(ConfigurationSection config) {
        this.config = config;

        Bukkit.getServer().getPluginManager().registerEvents(this, GUICraft.getPlugin());
    }

    @Override
    public void open(Player player) {
        String title = config.getString("title", "Menu");
        int rows = Math.max(1, config.getInt("rows", 5));

        inventory = Bukkit.createInventory(player, 9 * rows, title);

        ConfigurationSection items = config.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                ConfigurationSection itemSection = items.getConfigurationSection(key);
                if (itemSection == null) {
                    continue;
                }

                try {
                    ItemType itemType = Item.getInstance().resolveItemType(itemSection.getString("item"));
                    if (itemType == null) {
                        continue;
                    }

                    inventory.addItem(itemType.toItemStack());
                } catch (GUICraftException ignored) {
                }
            }
        }

        player.openInventory(inventory);
    }

    // Handle clicks in Menu
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }

        // Ignore clicks outside of the menu
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(inventory)) {
            return;
        }

        Action[] actions = Action.parse(config.get("action"), "!gc menu");


        event.setCancelled(true);
    }

    // Handle Close
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }
        System.err.println("Close");
        HandlerList.unregisterAll(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.err.println("Finalized!");
    }
}
