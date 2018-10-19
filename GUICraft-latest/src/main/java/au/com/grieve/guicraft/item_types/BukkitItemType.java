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

package au.com.grieve.guicraft.item_types;

import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.ItemType;
import au.com.grieve.guicraft.config.PackageConfiguration;
import au.com.grieve.guicraft.config.PackageResolver;
import au.com.grieve.guicraft.exceptions.ItemException;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A BukkitItem uses the built in bukkit serialization format. This should in theory support any item that does not
 * have external data.
 */
public class BukkitItemType implements ItemType {

    public BukkitItemType() {
        // Register a Command to manually execute this type
        GUICraft.getInstance().getCommandManager().registerCommand(new Command());
    }

    private void saveItem(String path, ItemStack item) throws ItemException {
        PackageConfiguration config = GUICraft.getInstance().getLocalConfig();
        PackageResolver resolver = config.getResolver("item");

        ConfigurationSection section = config.createSection(resolver.getPath(path));

        section.set("data", item.serialize());
        section.set("type", "bukkit");
        config.save();

    }

    @CommandAlias("%guicraft")
    @Subcommand("%item")
    public class Command extends BaseCommand {

        @Subcommand("%save bukkit")
        @Description("Save item in hand as a bukkit item")
        @CommandCompletion("@package:file=item")
        public void onActionOpen(CommandSender sender, String path) {
            ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                sender.spigot().sendMessage(new ComponentBuilder("Must hold item to save").color(ChatColor.RED).create());
                return;
            }

            try {
                saveItem(path, item);
            } catch (ItemException e) {
                sender.spigot().sendMessage(new ComponentBuilder("Failed to save item: ").append(e.getMessage()).create());
                return;
            }

            sender.spigot().sendMessage(new ComponentBuilder("Item saved").create());
        }
    }

}
