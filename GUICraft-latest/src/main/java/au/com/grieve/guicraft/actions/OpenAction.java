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

package au.com.grieve.guicraft.actions;

import au.com.grieve.guicraft.GUIAction;
import au.com.grieve.guicraft.GUICraft;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.contexts.OnlinePlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Opens a new menu based upon a config file
 */
public class OpenAction implements GUIAction {

    public OpenAction() {
        GUICraft.getInstance().getCommandManager().registerCommand(new Command());
    }

    public static List<String> tabCompletion() {
        return Arrays.asList("foo", "bar", "baz");
    }

    @CommandAlias("%guicraft")
    @Subcommand("%action")
    public static class Command extends BaseCommand {

        @Subcommand("open|o")
        @Description("Execute open action")
        @CommandCompletion("@players test1|test2")
        public void onActionOpen(CommandSender sender, OnlinePlayer player, String config) {
            sender.spigot().sendMessage(new ComponentBuilder("Open Action").create());
        }
    }


}
