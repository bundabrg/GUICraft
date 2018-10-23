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

package au.com.grieve.guicraft.vault.commands;

import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.item.Item;
import au.com.grieve.guicraft.item.ItemType;
import au.com.grieve.guicraft.utils.ItemUtils;
import au.com.grieve.guicraft.vault.Vault;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("%guicraft")
@Subcommand("%vault")
public class VaultCommands extends BaseCommand {

    @Subcommand("buy|b")
    @Description("Buy using Vault")
    @CommandCompletion("@item.config @nothing @nothing")
    public void onBuy(CommandSender sender, String itemPath, double cost, int qty, @Optional OfflinePlayer player) {
        Player executor;

        if (player == null) {
            if (!(sender instanceof Player)) {
                sender.spigot().sendMessage(new ComponentBuilder("Execute command as player or specify their name on the end.").color(ChatColor.RED).create());
                return;
            }
            executor = (Player) sender;
        } else {
            executor = player.getPlayer();
        }

        // Check that item is valid
        ItemType itemType;
        try {
            itemType = Item.getInstance().resolveItemType(itemPath);
        } catch (GUICraftException e) {
            sender.spigot().sendMessage(new ComponentBuilder("Failed to resolve Item: ").append(e.getMessage()).color(ChatColor.RED).create());
            return;
        }

        // Check if player has enough funds
        double totalCost = cost * qty;
        if (!Vault.getInstance().getEconomy().has(executor, totalCost)) {
            sender.spigot().sendMessage(new ComponentBuilder("Not enough funds.").color(ChatColor.RED).create());
            return;
        }

        // Check if the player has inventory space
        ItemStack itemStack;

        try {
            itemStack = itemType.toItemStack();
            itemStack.setAmount(qty);
        } catch (GUICraftException e) {
            sender.spigot().sendMessage(new ComponentBuilder("Failed to buy item: ").append(e.getMessage()).color(ChatColor.RED).create());
            return;
        }

        if (!ItemUtils.canStore(executor.getInventory(), itemStack)) {
            sender.spigot().sendMessage(new ComponentBuilder("Not enough space in inventory.").color(ChatColor.RED).create());
            return;
        }

        // Done
        Vault.getInstance().getEconomy().withdrawPlayer(executor, totalCost);
        executor.getInventory().addItem(itemStack);

        sender.spigot().sendMessage(
                new ComponentBuilder("Bought ")
                        .append(itemStack.getItemMeta().getDisplayName())
                        .append(" for ")
                        .append(Vault.getInstance().getEconomy().format(totalCost))
                        .append(".")
                        .create());
    }

}
