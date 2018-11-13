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

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RootCommand extends Command {
    @Getter
    private ArgumentParser parser;

    protected RootCommand(ArgumentParser parser, String name) {
        super(name);
        this.parser = parser;
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        List<ParserResult> result = parser.resolve(sender, args);
        System.err.println("Result: " + result.stream().map(r -> r.getResult()).collect(Collectors.toList()));
        if (result.size() > 0) {
            Method method = result.get(result.size() - 1).getData().getMethod();
            BaseCommand cmd = result.get(result.size() - 1).getData().getCommand();

            if (method != null && cmd != null) {
                // Generate list of parameters
                List<Object> objs = Stream.concat(
                        Stream.of(sender),
                        result.stream()
                                .filter(r -> !r.getParameters().getOrDefault("suppress", "false").equals("true"))
                                .map(ParserResult::getResult)
                )
                        .limit(method.getParameterCount()).collect(Collectors.toList());

                // Fill out extra parameters with null
                while (objs.size() < method.getParameterCount()) {
                    System.err.println("Adding null" + null);
                    objs.add(null);
                }
                System.err.println("OO: " + objs);

                System.err.println("O: " + Stream.of(objs.toArray())
                        .map(p -> {
                            if (p == null) {
                                return "null ";
                            } else {
                                return p.getClass().getName() + " ";
                            }
                        }).collect(Collectors.joining()));
                try {
                    System.err.println(method + " - " + String.join(",", Arrays.stream(method.getParameterTypes()).map(p -> p.getName()).collect(Collectors.toList())));
                    method.invoke(cmd, objs.toArray());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return parser.complete(sender, args);
    }
}
