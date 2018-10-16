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

package au.com.grieve.guicraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.contexts.OnlinePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("%guicraft")
public class GUICraftCommand extends BaseCommand {

    @HelpCommand
    @Subcommand("help")
    public void onHelp(CommandSender sender) {
        sender.spigot().sendMessage(new ComponentBuilder("=== [ GUICraft Help ] ===").color(ChatColor.AQUA).create());
        sender.spigot().sendMessage(new ComponentBuilder("/gc help <command>").color(ChatColor.DARK_AQUA)
                .append(" - Show help about command").color(ChatColor.GRAY).create());
        sender.spigot().sendMessage(new ComponentBuilder("/gc action <action>> [<parameters> ...]").color(ChatColor.DARK_AQUA)
                .append(" - Run a GUICraft action").color(ChatColor.GRAY).create());
    }

    @Subcommand("item|i")
    @Description("Save item to config file")
    @CommandCompletion("@nothing @package:file=item")
    public void onItem(CommandSender sender, String name, @Optional String pkg) {


    }


    @Subcommand("%action")
    public class ActionSubCommand extends BaseCommand {

        @Description("Execute an action")
        @Default
        public void onAction(CommandSender sender) {
            sender.spigot().sendMessage(new ComponentBuilder("Invalid Action").create());
        }

    }
}
