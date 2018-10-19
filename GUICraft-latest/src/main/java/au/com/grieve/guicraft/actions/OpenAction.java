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
import au.com.grieve.guicraft.MenuType;
import au.com.grieve.guicraft.config.PackageResolver;
import au.com.grieve.guicraft.exceptions.ActionException;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.contexts.OnlinePlayer;
import lombok.Getter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Opens a new menu based upon a config file
 */
public class OpenAction implements GUIAction {

    @Getter
    private Map<String, MenuType> types;

    public OpenAction() {
        // Register a Command to manually execute this action
        GUICraft.getInstance().getCommandManager().registerCommand(new Command());
    }

    @Override
    public void execute(Player player, String[] args) throws ActionException {
        if (args.length < 1) {
            throw new ActionException("Not enough arguments");
        }

        // Get Config
        PackageResolver resolver = GUICraft.getInstance().getLocalConfig().getResolver("menu");
        ConfigurationSection section =  GUICraft.getInstance().getLocalConfig().getConfigurationSection(resolver.getPath(args[0]));

        if (section == null) {
            throw new ActionException("Invalid menu: " + args[0]);
        }

        if (!section.contains("type")) {
            throw new ActionException("Missing type in menu: " + args[0]);
        }


        MenuType menuType = GUICraft.getInstance().getMenuTypes().get(section.getString("type"));

        if (menuType == null) {
            throw new ActionException("Invalid type '" + section.getString("type") + "' in menu: " + args[0]);
        }

        // Pass to type
        menuType.open(player, section);

    }


    @CommandAlias("%guicraft")
    @Subcommand("%action")
    public class Command extends BaseCommand {

        @Subcommand("open|o")
        @Description("Execute open action")
        @CommandCompletion("@players @config:file=menu")
        public void onActionOpen(CommandSender sender, OnlinePlayer player, String config) {
            try {
                OpenAction.this.execute(player.player, new String[]{config});
            } catch (ActionException e) {
                sender.spigot().sendMessage(new ComponentBuilder("Invalid Action: ").append(e.getMessage()).create());
            }
        }
    }

}
