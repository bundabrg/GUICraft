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

package au.com.grieve.guicraft.economy.commands;

import au.com.grieve.bcf.annotations.Arg;
import au.com.grieve.bcf.annotations.Description;
import au.com.grieve.guicraft.commands.GUICraftCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@Arg("economy|e")
public class EconomyCommands extends GUICraftCommand {


    @Arg("buy|b @item.config @player.offline(switch=player|p, required=false, default=self) @double @int")
    @Description("Buy Item")
    public void onBuy(CommandSender sender, String itemPath, OfflinePlayer player, double cost, int qty) {
        System.err.println(sender + ", " + itemPath + ", " + player + ", " + cost + ", " + qty);
    }

//    @Subcommand("buy|b")
//    @Description("Buy using Vault")
//    @CommandCompletion("@item.config @nothing @nothing")
//    public void onBuy(CommandSender sender, String itemPath, double cost, int qty, @Optional OfflinePlayer player) {
//        Player executor;
//
//        if (player == null) {
//            if (!(sender instanceof Player)) {
//                sender.spigot().sendMessage(new ComponentBuilder("Execute command as player or specify their name on the end.").color(ChatColor.RED).create());
//                return;
//            }
//            executor = (Player) sender;
//        } else {
//            executor = player.getPlayer();
//        }
//
//        // Check that item is valid
//        ItemType itemType;
//        try {
//            itemType = Item.getInstance().resolveItemType(itemPath);
//        } catch (GUICraftException e) {
//            sender.spigot().sendMessage(new ComponentBuilder("Failed to resolve Item: ").append(e.getMessage()).color(ChatColor.RED).create());
//            return;
//        }
//
//        // Check if player has enough funds
//        double totalCost = cost * qty;
//        if (!Economy.getInstance().getEconomy().has(executor, totalCost)) {
//            sender.spigot().sendMessage(new ComponentBuilder("Not enough funds.").color(ChatColor.RED).create());
//            return;
//        }
//
//        // Check if the player has inventory space
//        ItemStack itemStack;
//
//        try {
//            itemStack = itemType.toItemStack();
//            itemStack.setAmount(qty);
//        } catch (GUICraftException e) {
//            sender.spigot().sendMessage(new ComponentBuilder("Failed to buy item: ").append(e.getMessage()).color(ChatColor.RED).create());
//            return;
//        }
//
//        if (!ItemUtils.canStore(executor.getInventory(), itemStack)) {
//            sender.spigot().sendMessage(new ComponentBuilder("Not enough space in inventory.").color(ChatColor.RED).create());
//            return;
//        }
//
//        // Done
//        Economy.getInstance().getEconomy().withdrawPlayer(executor, totalCost);
//        executor.getInventory().addItem(itemStack);
//
//        sender.spigot().sendMessage(
//                new ComponentBuilder("Bought ")
//                        .append(itemStack.getItemMeta().getDisplayName())
//                        .append(" for ")
//                        .append(Economy.getInstance().getEconomy().format(totalCost))
//                        .append(".")
//                        .create());
//    }

}
