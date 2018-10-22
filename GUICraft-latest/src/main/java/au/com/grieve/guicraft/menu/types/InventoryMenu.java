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

import au.com.grieve.guicraft.Action;
import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.item.Item;
import au.com.grieve.guicraft.item.ItemType;
import au.com.grieve.guicraft.menu.MenuType;
import lombok.Data;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryMenu implements MenuType, Listener {

    private Inventory inventory;
    private Map<Integer, InventorySlot> inventorySlotMap = new HashMap<>();

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

        player.closeInventory();

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

                    int slot = inventory.firstEmpty();
                    if (slot == -1) {
                        continue;
                    }

                    InventorySlot inventorySlot = new InventorySlot();
                    inventorySlot.itemType = itemType;
                    inventorySlot.configurationSection = itemSection;

                    inventorySlotMap.put(slot, inventorySlot);

                    inventory.setItem(slot, itemType.toItemStack());
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

        if (inventorySlotMap.containsKey(event.getRawSlot())) {
            List<String> matches = new ArrayList<>();
            switch(event.getClick()) {
                case LEFT:
                    matches.add("left");
                    break;
                case SHIFT_LEFT:
                    matches.add("shift_left");
                    matches.add("left");
                    break;
                case DOUBLE_CLICK:
                    matches.add("doubleclick");
                    break;

                case RIGHT:
                    matches.add("right");
                    break;
                case SHIFT_RIGHT:
                    matches.add("shift_right");
                    matches.add("right");
                    break;

                case MIDDLE:
                    matches.add("middle");
                    break;

                case DROP:
                    matches.add("drop");
                    break;
                case CONTROL_DROP:
                    matches.add("control_drop");
                    break;
            }

            new Action(inventorySlotMap.get(event.getRawSlot()).configurationSection.get("action")).execute(event.getWhoClicked(), "gc menu", matches.toArray(new String[0]));
        }

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

    @Data
    private class InventorySlot {
        ItemType itemType;
        ConfigurationSection configurationSection;
    }
}
