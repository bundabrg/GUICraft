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

package au.com.grieve.bcf.parsers;

import au.com.grieve.bcf.ArgData;
import au.com.grieve.bcf.Parser;
import au.com.grieve.bcf.TreeNode;
import au.com.grieve.bcf.ValidArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Name of a player
 *
 * Parameters:
 *   filter:
 *     any - (default) Any player
 *     online - Only online players
 *   constrain:
 *     false - (default) accept any input
 *     true - Only accept a player by filter
 */
public class Player implements Parser {
    @Override
    public ValidArgument isValid(CommandSender sender, List<String> args, TreeNode<ArgData> node) {
        if (args.size() == 0) {
            return ValidArgument.INVALID();
        }

        String filter = node.data.getParameters().getOrDefault("filter", "any");
        boolean constrain = node.data.getParameters().getOrDefault("constrain", "false").equalsIgnoreCase("true");
        String arg = args.remove(0);

        if (args.size() == 0 || constrain) {
            List<String> players;
            switch(filter) {
                case "online":
                    players = Bukkit.getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .filter(s -> s.startsWith(arg))
                            .collect(Collectors.toList());
                    break;
                default:
                    players = Arrays.stream(Bukkit.getOfflinePlayers())
                            .map(OfflinePlayer::getName)
                            .filter(s -> s.startsWith(arg))
                            .collect(Collectors.toList());
            }

            if (players.size() == 0) {
                return ValidArgument.INVALID();
            }

            if (args.size() == 0) {
                return ValidArgument.PARTIAL(players);
            }
        }

        return ValidArgument.VALID();
    }
}
