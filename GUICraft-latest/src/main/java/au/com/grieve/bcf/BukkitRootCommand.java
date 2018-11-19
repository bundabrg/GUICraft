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

package au.com.grieve.bcf;

import au.com.grieve.bcf.api.CommandManager;
import au.com.grieve.bcf.api.Parser;
import au.com.grieve.bcf.api.ParserNode;
import au.com.grieve.bcf.api.RootCommand;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BukkitRootCommand extends Command implements RootCommand {
    @Getter
    private ParserNode node = new ParserNode();

    @Getter
    private CommandManager manager;

    protected BukkitRootCommand(CommandManager manager, String name) {
        super(name);
        this.manager = manager;
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        BukkitParserContext context = new BukkitParserContext(manager, sender);

        List<Parser> result = manager.resolve(node, args, context);

//        System.err.println("Result: " + String.join(",", result.stream()
//                .flatMap(r -> r.getResults().stream()
//                        .map(q -> q == null ? "null" : q.getClass().getName()))
//                .collect(Collectors.toList())));
//        if (result.size() > 0) {
//            Method method = result.get(result.size() - 1).getData().getMethod();
//            BaseCommand cmd = result.get(result.size() - 1).getData().getCommand();
//
//            if (method != null && cmd != null) {
//                // Generate list of parameters
//                List<Object> objs = Stream.concat(
//                        Stream.of(sender),
//                        result.stream()
//                                .filter(r -> !r.getParameters().getOrDefault("suppress", "false").equals("true"))
//                                .flatMap(r -> r.getResults().stream())
//                )
//                        .limit(method.getParameterCount()).collect(Collectors.toList());
//
//                // Fill out extra parameters with null
//                while (objs.size() < method.getParameterCount()) {
//                    objs.add(null);
//                }
//
//                System.err.println("O: " + Stream.of(objs.toArray())
//                        .map(p -> {
//                            if (p == null) {
//                                return "null ";
//                            } else {
//                                return p.getClass().getName() + " ";
//                            }
//                        }).collect(Collectors.joining()));
//                try {
//                    System.err.println(method + " - " + String.join(",", Arrays.stream(method.getParameterTypes()).map(p -> p.getName()).collect(Collectors.toList())));
//                    method.invoke(cmd, objs.toArray());
//                } catch (IllegalAccessException | InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        BukkitParserContext context = new BukkitParserContext(manager, sender);
        List<Parser> result = manager.resolve(node, args, context);
        return new ArrayList<>();
    }
}
