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

package au.com.grieve.guicraft.item.commands;

import au.com.grieve.guicraft.item.ItemException;
import au.com.grieve.guicraft.item.types.BukkitItemType;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


@CommandAlias("%guicraft")
@Subcommand("%item")
public class BukkitCommands extends BaseCommand {

    @Subcommand("%itemsave")
    @Description("Save item in hand as a bukkit item")
    @CommandCompletion("@item.package bukkit")
    public void onSave(CommandSender sender, String path, String type) {
        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sender.spigot().sendMessage(new ComponentBuilder("Must hold item to save").color(ChatColor.RED).create());
            return;
        }

        try {
            BukkitItemType.saveItem(path, item);
        } catch (ItemException e) {
            sender.spigot().sendMessage(new ComponentBuilder("Failed to save item: ").append(e.getMessage()).create());
            return;
        }

        sender.spigot().sendMessage(new ComponentBuilder("Item saved").create());
    }
}