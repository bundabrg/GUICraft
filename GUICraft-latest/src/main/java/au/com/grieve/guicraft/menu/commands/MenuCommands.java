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

package au.com.grieve.guicraft.menu.commands;

import au.com.grieve.bcf.ParseResult;
import au.com.grieve.bcf.annotations.Arg;
import au.com.grieve.bcf.annotations.Description;
import au.com.grieve.guicraft.commands.GUICraftCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


@Arg("menu|m")
public class MenuCommands extends GUICraftCommand {

    @Arg("open|o @menu.config @player(switch=player|p, required=false, default=self, filter=online) @menu.type.proxy(required=false)")
    @Description("Open Menu")
    public void onOpen(CommandSender sender, String path, Player player, List<ParseResult> data) {
        System.err.println("onOpen: " + sender + ", " + path + ", " + player + "," + data);
        return;
//        // Resolve Menu
//        try {
//            MenuType menuType = Menu.getInstance().resolveMenuType(path);
//
//            if (menuType == null) {
//                throw new MenuException("Invalid menu: " + path);
//            }
//
//            Player opener;
//
//            if (player == null) {
//                if (!(sender instanceof Player)) {
//                    throw new MenuException("Execute command as player or specify their name on the end");
//                }
//                opener = (Player) sender;
//            } else {
//                opener = player.player;
//            }
//
//
//            menuType.open(opener);
//
//        } catch (GUICraftException e) {
//            sender.spigot().sendMessage(new ComponentBuilder("Error: ").append(e.getMessage()).color(ChatColor.RED).create());
//        }
    }

    @Arg("close|c @players(switch=player|p, required=false, default=self, filter=any)")
    @Description("Close Menu")
    public void onClose(CommandSender sender, Player player) {
        System.err.println("onOpen: " + sender + ", " + player);
        return;
//        // Resolve Menu
//        try {
//            Player closer;
//
//            if (player == null) {
//                if (!(sender instanceof Player)) {
//                    throw new MenuException("Execute command as player or specify their name on the end");
//                }
//                closer = (Player) sender;
//            } else {
//                closer = player.player;
//            }
//
//            closer.closeInventory();
//
//        } catch (GUICraftException e) {
//            sender.spigot().sendMessage(new ComponentBuilder("Error: ").append(e.getMessage()).color(ChatColor.RED).create());
//        }
    }
}