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

import au.com.grieve.bcf.api.ArgData;
import au.com.grieve.bcf.api.Parser;
import au.com.grieve.bcf.api.ParserContext;
import au.com.grieve.bcf.api.ParserResult;
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
        gui.getBukkitCommandManager().registerParser("economy.type", new Parser() {
            @Override
            public ParserResult resolve(ArgData data, List<String> args, ParserContext context) {
                ParserResult result = new ParserResult(data);

                if (args.size() == 0) {
                    return result;
                }

                String arg = args.remove(0);
                result.getArgs().add(arg);

//                result.getCompletions().addAll(gui.getLocalConfig().getResolver("menu").getKeys().stream()
//                        .filter(s -> s.startsWith(arg))
//                        .limit(20)
//                        .collect(Collectors.toList()));

//                q

                return result;
            }
        });

        // Economy Types
        VaultEconomy.register();

        // Commands
        gui.getBukkitCommandManager().registerCommand(new EconomyCommands());

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
