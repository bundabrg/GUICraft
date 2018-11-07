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

package au.com.grieve.guicraft.economy;

import au.com.grieve.bcf.ArgData;
import au.com.grieve.bcf.Parser;
import au.com.grieve.bcf.TreeNode;
import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.economy.commands.EconomyCommands;
import au.com.grieve.guicraft.economy.types.VaultEconomy;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Economy {
    @Getter
    private static Economy instance;

    @Getter
    private Map<String, Class<? extends EconomyType>> economyTypes = new HashMap<>();

    public Economy() {
        instance = this;

        GUICraft gui = GUICraft.getInstance();

        // Command Replacements
//        gui.getCommandManager().getCommandReplacements().addReplacement("economy", "economy|e");

        // Tab Completions
        gui.getCommandManager().registerParser("economy.type", new Parser() {
            @Override
            public void resolve(CommandSender sender, List<String> args, TreeNode<ArgData> node, List<String> alternatives, List<Object> result) {
                if (args.size() == 0) {
                    return;
                }

                String arg = args.remove(0);
            }
        });

        // Economy Types
        VaultEconomy.register();

        // Commands
        gui.getCommandManager().registerCommand(new EconomyCommands());

    }

    /**
     * Register a MenuType
     */
    public void registerEconomyType(String name, Class<? extends EconomyType> type) {
        economyTypes.put(name, type);
    }

    /**
     * Unregister MenuType
     */
    public void unregisterEconomyType(String name) {
        economyTypes.remove(name);
    }


    public static void init() {
        if (instance == null) {
            new Economy();
        }
    }
}
