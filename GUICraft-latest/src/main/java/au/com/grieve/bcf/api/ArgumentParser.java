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

package au.com.grieve.bcf.api;

import au.com.grieve.bcf.api.exceptions.ParserException;
import lombok.Getter;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArgumentParser {
    @Getter
    private TreeNode<ArgData> data = new TreeNode<>();


    public ArgumentParser() {
        this(null);
    }


    public ArgumentParser(String path) {
        if (path != null) {
            createNode(path);
        }
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
                            .filter(c -> c.data.getArg().equals(argData.getArg()))
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

    public List<ParserResult> resolve(String[] args, ParserContext context) {
        return resolve(data, Arrays.asList(args), context);
    }

    /**
     * Return the longest chain of arguments
     */
    List<ParserResult> resolve(TreeNode<ArgData> node, List<String> args, ParserContext context) {
        List<ParserResult> results = new ArrayList<>();

        if (!node.isRoot() && node.data != null && node.data.getArg() != null) {
            // Handle switches
            if (node.data.getParameters().containsKey("switch")) {
                context.getSwitches().add(node.data);
            } else {
                boolean isSwitch = false;
                Parser parser;
                if (args.size() > 0 && args.get(0).length() > 0 && args.get(0).startsWith("-")) {
                    parser = context.getManager().getSwitchParser();
                    isSwitch = true;
                } else {
                    parser = context.getManager().getParser(node.data.arg);
                }

                if (parser == null) {
                    return results;
                }

                try {
                    // If we have a default argument then add it now if needed
                    if (args.size() == 0 && node.data.getParameters().containsKey("default")) {
                        args.addAll(Arrays.asList(node.data.getParameters().get("default").split(" ")));
                    }

                    ParserResult result = parser.resolve(node.data, args, context);
                    if (result == null) {
                        return results;
                    }

                    // If its not optional then end of the line
                    if (result.getResults().size() == 0 && !result.getParameters().getOrDefault("required", "true").equals("false")) {
                        return results;
                    }

                    results.add(result);
                } catch (ParserException e) {
                    e.printStackTrace();
                }

                if (isSwitch) {
                    return resolve(node, args, context);
                }
            }
        }

        // Recurse and keep first best
        List<ParserResult> bestResult = new ArrayList<>();
        for (TreeNode<ArgData> n : node.children) {
            ParserContext currentContext;
            try {
                currentContext = (ParserContext) context.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                break;
            }

            List<ParserResult> checkResult = resolve(n, new ArrayList<>(args), currentContext);
            if (checkResult.size() > bestResult.size()) {
                bestResult = checkResult;
            }
        }

        results.addAll(bestResult);

        return results;
    }


    public List<String> complete(String[] args, ParserContext context) {
        return complete(data, Arrays.asList(args), context);
    }

    /**
     * Return the list of completions for the last argument of the deepest chain
     */
    List<String> complete(TreeNode<ArgData> node, List<String> args, ParserContext context) {
        List<String> results = new ArrayList<>();

        if (!node.isRoot() && node.data != null && node.data.arg != null) {
            // Handle switches
            if (node.data.getParameters().containsKey("switch")) {
                context.getSwitches().add(node.data);
            } else {
                boolean isSwitch = false;
                Parser parser;
                if (args.size() > 0 && args.get(0).length() > 0 && args.get(0).startsWith("-")) {
                    parser = context.getManager().getSwitchParser();
                    isSwitch = true;
                } else {
                    parser = context.getManager().getParser(node.data.arg);
                }

                if (parser == null) {
                    return results;
                }

                try {
                    ParserResult result = parser.resolve(node.data, args, context);

                    if (result == null) {
                        return results;
                    }

                    // If no arguments left, store the completions
                    if (args.size() == 0) {
                        results.addAll(result.getCompletions());
                        return results;
                    }

                    // If its not optional and failed to resolve then end of the line
                    if (result.getResults().size() == 0 && !result.getParameters().getOrDefault("required", "true").equals("false")) {
                        return results;
                    }

                } catch (ParserException e) {
                    e.printStackTrace();
                }

                if (isSwitch) {
                    return complete(node, args, context);
                }
            }
        }

        // Recurse and merge
        for (TreeNode<ArgData> n : node.children) {
            ParserContext currentContext;
            try {
                currentContext = (ParserContext) context.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                break;
            }
            results.addAll(complete(n, new ArrayList<>(args), currentContext));
        }

        return results;
    }

}
