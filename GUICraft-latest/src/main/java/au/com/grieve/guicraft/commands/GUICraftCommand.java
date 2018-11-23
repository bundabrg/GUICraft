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

import au.com.grieve.bcf.annotations.Arg;
import au.com.grieve.bcf.annotations.Command;
import au.com.grieve.bcf.api.BaseCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

@Command("guicraft|gc")
public class GUICraftCommand extends BaseCommand {

    @Arg("help|?")
    public void onHelp(CommandSender sender) {
        sender.spigot().sendMessage(new ComponentBuilder("=== [ GUICraft Help ] ===").color(ChatColor.AQUA).create());
        sender.spigot().sendMessage(new ComponentBuilder("/gc help <command>").color(ChatColor.DARK_AQUA)
                .append(" - Show help about command").color(ChatColor.GRAY).create());
        sender.spigot().sendMessage(new ComponentBuilder("/gc action <action>> [<parameters> ...]").color(ChatColor.DARK_AQUA)
                .append(" - Run a GUICraft action").color(ChatColor.GRAY).create());
    }
}
