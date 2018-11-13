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

import au.com.grieve.bcf.exceptions.ParserException;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArgumentParser {
    private final CommandManager manager;
    @Getter
    private TreeNode<ArgData> data = new TreeNode<>();

    public ArgumentParser(CommandManager manager) {
        this.manager = manager;
    }

    public ArgumentParser(CommandManager manager, String path) {
        this.manager = manager;
        if (path != null) {
            createNode(path);
        }
    }


    public String walkTree() {
        StringBuilder result = new StringBuilder();

        for (TreeNode<ArgData> n : data.children) {
            result.append(walkTree(n, 0));
        }
        return result.toString();
    }

    private String walkTree(TreeNode<ArgData> node, int depth) {
        StringBuilder result = new StringBuilder();

        char[] repeat = new char[depth];
        Arrays.fill(repeat, ' ');
        String pad = new String(repeat);

        // Our name
        result.append(pad).append(node.data.arg).append(" - ").append(node.data.parameters).append("\n");

        for (TreeNode<ArgData> n : node.children) {
            result.append(walkTree(n, depth + 1));
        }
        return result.toString();
    }

    /**
     * Create node(s) designated by path including any missing parent nodes
     */
    public List<TreeNode<ArgData>> createNode(String path) {
        List<TreeNode<ArgData>> currentNodes = Collections.singletonList(data);

        StringReader reader = new StringReader(path);
        for (List<ArgData> argDataList : ArgData.parse(reader)) {
            List<TreeNode<ArgData>> newCurrent = new ArrayList<>();
            for (TreeNode<ArgData> node : currentNodes) {
                for (ArgData argData : argDataList) {
                    newCurrent.add(node.children.stream()
                            .filter(c -> c.data.arg.equals(argData.arg))
                            .findFirst()
                            .orElseGet(() -> node.addChild(argData)));
                }
            }
            currentNodes = newCurrent;
        }

        if (currentNodes.size() > 0 && currentNodes.get(0) == data) {
            return new ArrayList<>();
        }

        return currentNodes;
    }

//    public List<Object> getData(CommandSender sender, String[] args) {
//        return getData(sender, Arrays.asList(args), data);
//    }
//
//    private List<Object> getData(CommandSender, List<String> args, TreeNode<ArgData> node) {
//
//    }

    public boolean resolve(String[] args, ParserContext context) {
        return resolve(data, Arrays.asList(args), context);
    }

    /**
     * Return the longest chain of arguments
     */
    boolean resolve(TreeNode<ArgData> node, List<String> args, ParserContext context) {
        // If we have a default argument then add it now if needed
        if (args.size() == 0 && node.data.getParameters().containsKey("default")) {
            args.addAll(Arrays.asList(node.data.getParameters().get("default").split(" ")));
        }

        if (!node.isRoot() && node.data != null && node.data.arg != null) {
            Parser parser = manager.getParser(node.data.arg);

            if (parser == null) {
                return false;
            }

            try {
                boolean result = parser.resolve(args, context, node.data);
                if (!result) {
                    if
                    return false;
                }

                // If its not optional then end of the line
                if (context.getResults()
                parseResult.getResult() == null && !parseResult.getParameters().getOrDefault("required", "true").equals("false"))
                {
                    return result;
                }

                result.add(parseResult);
            } catch (ParserException e) {
                e.printStackTrace();
            }
        }

        // Recurse and keep first best
        for (TreeNode<ArgData> n : node.children) {
            ParserContext currentContext;
            try {
                currentContext = (ParserContext) context.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                break;
            }

            List<ParserResult> checkResult = resolve(new ArrayList<>(args), currentContext, n);
            if (currentContext.getResults().size() > context.getResults().size()) {
                context = currentContext;
            }
        }

        result.addAll(bestResult);

        return result;
    }


    public List<String> complete(CommandSender sender, String[] args) {
        return complete(sender, Arrays.asList(args), data);
    }

    /**
     * Return the list of completions for the last argument of the deepest chain
     */
    List<String> complete(CommandSender sender, List<String> args, TreeNode<ArgData> node) {
        List<String> result = new ArrayList<>();

        if (!node.isRoot() && node.data != null && node.data.arg != null) {
            Parser parser = manager.getParser(node.data.arg);

            if (parser == null) {
                return result;
            }

            try {
                ParserResult parseResult = parser.resolve(sender, args, node.data);

                if (parseResult == null) {
                    return result;
                }

                // If no arguments left, store the completions
                if (args.size() == 0) {
                    result.addAll(parseResult.getCompletions());
                    return result;
                }

                // If its not optional and failed to resolve then end of the line
                if (parseResult.getResult() == null && !parseResult.getParameters().getOrDefault("required", "true").equals("false")) {
                    return result;
                }

            } catch (ParserException e) {
                e.printStackTrace();
            }
        }

        // Recurse and merge
        for (TreeNode<ArgData> n : node.children) {
            result.addAll(complete(sender, new ArrayList<>(args), n));
        }

        return result;
    }

}
