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

import au.com.grieve.guicraft.exceptions.GUICraftException;
import au.com.grieve.guicraft.menu.Menu;
import au.com.grieve.guicraft.menu.MenuException;
import au.com.grieve.guicraft.menu.MenuType;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.contexts.OnlinePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%guicraft")
@Subcommand("%menu")
public class MenuCommands extends BaseCommand {

    @Subcommand("open|o")
    @Description("Execute open command")
    @CommandCompletion("@menu.config @players")
    public void onOpen(CommandSender sender, String path, @Optional OnlinePlayer player) {
        // Resolve Menu
        try {
            MenuType menuType = Menu.getInstance().resolveMenuType(path);

            if (menuType == null) {
                throw new MenuException("Invalid menu: " + path);
            }

            Player opener;

            if (player == null) {
                if (!(sender instanceof Player)) {
                    throw new MenuException("Execute command as player or specify their name on the end");
                }
                opener = (Player) sender;
            } else {
                opener = player.player;
            }


            menuType.open(opener);

        } catch (GUICraftException e) {
            sender.spigot().sendMessage(new ComponentBuilder("Error: ").append(e.getMessage()).color(ChatColor.RED).create());
        }
    }
}