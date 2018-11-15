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

import au.com.grieve.bcf.annotations.Arg;
import au.com.grieve.bcf.annotations.Command;
import au.com.grieve.bcf.api.ArgData;
import au.com.grieve.bcf.api.ArgumentParser;
import au.com.grieve.bcf.api.BaseCommand;
import au.com.grieve.bcf.api.CommandManager;
import au.com.grieve.bcf.api.TreeNode;
import au.com.grieve.bcf.parsers.PlayerParser;
import au.com.grieve.bcf.utils.ReflectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BukkitCommandManager extends CommandManager {

    private final JavaPlugin plugin;
    private final CommandMap commandMap;

    public BukkitCommandManager(JavaPlugin plugin) {
        super();
        this.plugin = plugin;
        this.commandMap = hookCommandMap();

        // Register Default Parsers
        registerParser("player", new PlayerParser());
    }

    private CommandMap hookCommandMap() {
        CommandMap commandMap;
        Server server = Bukkit.getServer();
        Method getCommandMap;
        try {
            getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);
            commandMap = (CommandMap) getCommandMap.invoke(server);
            Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            throw new RuntimeException("Cannot Hook CommandMap", e);
        }

        return commandMap;
    }

    public void registerCommand(BaseCommand cmd) {
        // Lookup all parents
        List<Class<?>> parents = Stream
                .concat(
                        Stream.of(cmd.getClass()),
                        Stream.of(ReflectUtils.getAllSuperClasses(cmd.getClass())))
                .filter(BaseCommand.class::isAssignableFrom)
                .filter(c -> c != BaseCommand.class)
                .collect(Collectors.toList());

        // Get Full Parent Arg
        Collections.reverse(parents);
        String parentArg = parents.stream()
                .map(c -> c.getAnnotation(Arg.class))
                .filter(Objects::nonNull)
                .map(Arg::value)
                .collect(Collectors.joining(" "));

        // Get Root Command, if any
        String rootCommand = null;

        if (parents.size() > 0) {
            Class<?> rootClass = parents.get(0);
            Command commandAnnotation = rootClass.getAnnotation(Command.class);
            if (commandAnnotation != null) {
                rootCommand = commandAnnotation.value();
            }
        }

        // Get Parser, if any
        ArgumentParser parser = null;
        if (rootCommand != null && commands.containsKey(rootCommand)) {
            parser = commands.get(rootCommand).getParser();
        }

        // No parser? Create it
        if (parser == null) {
            parser = createParser();
            if (rootCommand != null) {
                String[] aliases = rootCommand.split("\\|");
                if (aliases.length == 0) {
                    aliases = new String[]{cmd.getClass().getSimpleName().toLowerCase()};
                }
                BukkitRootCommand command = new BukkitRootCommand(parser, aliases[0]);
                command.setAliases(Arrays.asList(aliases));
                commandMap.register(aliases[0], plugin.getName().toLowerCase(), command);

                commands.put(rootCommand, command);
            }
        }

        // Add each method to parser
        for (Method m : cmd.getClass().getDeclaredMethods()) {
            Arg argAnnotation = m.getAnnotation(Arg.class);


            if (argAnnotation != null && argAnnotation.value().trim().length() > 0) {
                for (TreeNode<ArgData> t : parser.createNode(parentArg + " " + argAnnotation.value().trim())) {
                    t.data.setMethod(m);
                    t.data.setCommand(cmd);
                }
            }
        }

        // Debug
        System.err.println("\n" + parser.walkTree());
    }

}
