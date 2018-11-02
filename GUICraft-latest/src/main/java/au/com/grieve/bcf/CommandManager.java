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
import au.com.grieve.bcf.utils.ReflectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandManager {

    private final JavaPlugin plugin;
    private final CommandMap commandMap;
    private List<TreeNode<ArgData>> args = new ArrayList<>();
    private Map<String, RootCommand> commands = new HashMap<>();

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commandMap = hookCommandMap();


    }

    private CommandMap hookCommandMap() {
        CommandMap commandMap = null;
        Server server = Bukkit.getServer();
        Method getCommandMap = null;
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

        // Get Root Node, if any
        TreeNode<ArgData> rootNode = null;
        if (rootCommand != null && commands.containsKey(rootCommand)) {
            rootNode = commands.get(rootCommand).getArgData();
        }

        // No root node, create it
        if (rootNode == null) {
            rootNode = new TreeNode<>();
            args.add(rootNode);
            System.err.println("RootCommand: " + rootCommand);
            if (rootCommand != null) {
                String[] aliases = rootCommand.split("\\|");
                if (aliases.length == 0) {
                    aliases = new String[]{cmd.getClass().getSimpleName().toLowerCase()};
                }
                RootCommand command = new RootCommand(rootNode, aliases[0]);
                command.setAliases(Arrays.asList(aliases));
                System.err.println("Registering: " + aliases[0] + " - " + plugin.getName().toLowerCase() + " - " + command);
                commandMap.register(aliases[0], plugin.getName().toLowerCase(), command);

                commands.put(rootCommand, command);
            }
        }

        List<TreeNode<ArgData>> currentNodes = new ArrayList<>();
        currentNodes.add(rootNode);

        // Build parent args
        StringReader reader = new StringReader(parentArg);
        currentNodes = addToArgs(reader, currentNodes);

        // Add each method to args tree
        for (Method m : cmd.getClass().getDeclaredMethods()) {
            Arg argAnnotation = m.getAnnotation(Arg.class);

            if (argAnnotation != null && argAnnotation.value().trim().length() > 0) {
                reader = new StringReader(argAnnotation.value().trim());
                List<TreeNode<ArgData>> methodNodes = addToArgs(reader, new ArrayList<>(currentNodes));
            }
        }

        // Debug
        for( TreeNode<ArgData> r : args) {
            for ( TreeNode<ArgData> t : r) {
                if (t.isLeaf() && !t.isRoot()) {
                    TreeNode<ArgData> current = t;
                    List<ArgData> a = new ArrayList<>();
                    while (t != null) {
                        a.add(t.data);
                        t = t.parent;
                    }



                    System.err.println("--> " + String.join(",", a.stream()
                            .filter(Objects::nonNull)
                            .map(l -> l.arg)
                            .collect(Collectors.toList())));
                }
            }
        }
    }

    private List<TreeNode<ArgData>> addToArgs(Reader reader, List<TreeNode<ArgData>> currentNodes) {

        for (List<ArgData> argDataList : ArgData.parse(reader)) {
            List<TreeNode<ArgData>> newCurrent = new ArrayList<>();
            for (TreeNode<ArgData> node : currentNodes) {
                for (ArgData argData : argDataList) {
                    if (!node.contains(argData)) {
                        newCurrent.add(node.addChild(argData));
                    }
                }
            }
            currentNodes = newCurrent;
        }
        return currentNodes;
    }

}
