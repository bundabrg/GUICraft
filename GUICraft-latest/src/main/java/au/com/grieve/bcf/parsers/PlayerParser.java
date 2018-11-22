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

import au.com.grieve.bcf.BukkitParserContext;
import au.com.grieve.bcf.api.ArgData;
import au.com.grieve.bcf.api.BaseParser;
import au.com.grieve.bcf.api.ParserContext;
import au.com.grieve.bcf.api.ParserResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Name of a player
 *
 * Parameters:
 *   mode:
 *     any - (default) Any player
 *     online - Only online players
 */
public class PlayerBaseParser extends BaseParser {
    @Override
    public ParserResult resolve(ArgData data, List<String> args, ParserContext context) {
        ParserResult result = new ParserResult(data);

        if (args.size() == 0) {
            return result;
        }

        String arg = args.remove(0);
        result.getArgs().add(arg);

        switch (data.getParameters().getOrDefault("mode", "any")) {
            case "online":
                result.getCompletions().addAll(Bukkit.getOnlinePlayers().stream()
                        .map(HumanEntity::getName)
                        .filter(s -> s.startsWith(arg))
                        .limit(20)
                        .collect(Collectors.toList()));

                if (arg.equals("@self")) {
                    result.getResults().add(((BukkitParserContext) context).getSender());
                } else {
                    result.getResults().addAll(Bukkit.getOnlinePlayers().stream()
                            .filter(p -> p.getName().equals(arg))
                            .limit(1)
                            .collect(Collectors.toList()));
                }
                break;
            default:
                result.getCompletions().addAll(Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(OfflinePlayer::getName)
                        .filter(s -> s.startsWith(arg))
                        .limit(20)
                        .collect(Collectors.toList()));

                if (arg.equals("@self")) {
                    result.getResults().add(Bukkit.getOfflinePlayer(((Player) ((BukkitParserContext) context).getSender()).getUniqueId()));
                } else {
                    result.getResults().addAll(Arrays.stream(Bukkit.getOfflinePlayers())
                            .filter(p -> p.getName().equals(arg))
                            .limit(1)
                            .collect(Collectors.toList()));
                }
        }

        return result;
    }
}
