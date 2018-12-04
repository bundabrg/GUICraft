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

import au.com.grieve.bcf.api.exceptions.ParserInvalidResultException;
import au.com.grieve.bcf.api.exceptions.ParserRequiredArgumentException;
import au.com.grieve.bcf.api.parsers.DoubleParser;
import au.com.grieve.bcf.api.parsers.IntegerParser;
import au.com.grieve.bcf.api.parsers.LiteralParser;
import au.com.grieve.bcf.api.parsers.StringParser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CommandManager {

    protected Map<String, RootCommand> commands = new HashMap<>();
    protected Map<String, Class<? extends Parser>> parsers = new HashMap<>();

    public CommandManager() {
        // Register Default Parsers
        registerParser("string", StringParser.class);
        registerParser("int", IntegerParser.class);
        registerParser("double", DoubleParser.class);
    }

    public abstract void registerCommand(BaseCommand cmd);

    public void registerParser(String name, Class<? extends Parser> parser) {
        this.parsers.put("@" + name, parser);
    }

    public void unregisterParser(String name) {
        this.parsers.remove(name);
    }

    /**
     * Return a list of Parsers for the specified arguments
     *
     * A context is passed to provide implementation specific data as well as useful data needed by parsers
     */
//    public List<Parser> resolve(ParserNode node, String args, ParserContext context) {
//        List<Parser> result = new ArrayList<>();

//        for (ParserNode child : node.getChildren()) {
//            ParserNodeData data = child.getData();
//
//            // Check validity
//            if (data == null) {
//                continue;
//            }
//
//            // Make a copy of the context
//            ParserContext childContext;
//            try {
//                childContext = (ParserContext) context.clone();
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//                break;
//            }
//
//            List<Parser> childResult;
//
//            Class<? extends Parser> parserClass = parsers.getOrDefault("@" + data.getName(), LiteralParser.class);
//
//            Parser parser;
//            try {
//                parser = parserClass.getConstructor(CommandManager.class, ParserNode.class, String.class, ParserContext.class)
//                        .newInstance(this, child, args, childContext);
//            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
//                e.printStackTrace();
//                continue;
//            } catch (InvocationTargetException e) {
//                // If its missing a required argument, then we are done
//                if (e.getCause() instanceof ParserRequiredArgumentException) {
//                    continue;
//                }
//                e.printStackTrace();
//                continue;
//            }
//
//            // Make sure it returns a valid result
//            try {
//                parser.getResult();
//            } catch (ParserInvalidResultException e) {
//                continue;
//            }
//
//            // Valid parser we recurse into
//            List<Parser> r = resolve(child, parser.getUnused(), childContext);
//            childResult = Stream.concat(
//                    Collections.singletonList(parser).stream(),
//                    resolve(child, parser.getUnused(), childContext).stream())
//                    .collect(Collectors.toList());
//
//            if (childResult.size() > result.size()) {
//                result = childResult;
//            }
//        }

//        return result;
//    }
    public List<Parser> getResolve(ParserNode node, String args, ParserContext context) {
        List<Parser> result = new ArrayList<>();
        for (Parser p : resolve(node, args, context)) {
            if (!p.isParsed()) {
                try {
                    p.parse(null);
                } catch (ParserRequiredArgumentException e) {
                    break;
                }
            }

            try {
                p.getResult();
            } catch (ParserInvalidResultException e) {
                break;
            }

            result.add(p);
        }
        return result;
    }

    /**
     * Return parsers for a switch
     */
    public List<Parser> switchResolve(ParserNode node, String args, ParserContext context) {
        List<Parser> result = new ArrayList<>();

        while (args != null && args.startsWith("-")) {
            String[] argSplit = args.split(" ", 2);

            // Complete it if last argument
            if (argSplit.length < 2) {
                return new ArrayList<>();
            }

            args = argSplit[1];

            // Look for parser
            Parser switchParser = context.getSwitches().stream()
                    .flatMap(s -> Arrays.stream(s.getNode().getData().getParameters().get("switch").split("\\|"))
                            .filter(sw -> sw.equals(argSplit[0].substring(1)))
                            .limit(1)
                            .map(sw -> s)
                    )
                    .findFirst()
                    .orElse(null);

            if (switchParser == null) {
                return new ArrayList<>();
            }

            try {
                args = switchParser.parse(args);
            } catch (ParserRequiredArgumentException e) {
                return new ArrayList<>();
            }

            // Make sure its a valid result
            try {
                switchParser.getResult();
            } catch (ParserInvalidResultException e) {
                return new ArrayList<>();
            }

            context.getSwitches().remove(switchParser);
        }

        result.addAll(resolve(node, args, context));
        return result;
    }

    /**
     * Return a list of Parsers
     */
    public List<Parser> resolve(ParserNode node, String args, ParserContext context) {
        List<Parser> result = new ArrayList<>();

        if (node == null) {
            return result;
        }

        if (!node.isRoot() && node.getData() != null) {
            ParserNodeData data = node.getData();

            Class<? extends Parser> parserClass = parsers.getOrDefault(data.getName(), LiteralParser.class);

            Parser parser;
            try {
                parser = parserClass.getConstructor(CommandManager.class, ParserNode.class, ParserContext.class)
                        .newInstance(this, node, context);
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }

            // Take care of switches first in case this argument is also a switch
            if (data.getParameters().containsKey("switch")) {
                result.add(parser);
                context.getSwitches().add(parser);

            } else {
                if (args != null && args.startsWith("-")) {
                    result.addAll(switchResolve(node, args, context));
                    return result;
                }

                try {
                    args = parser.parse(args);
                } catch (ParserRequiredArgumentException e) {
                    return new ArrayList<>();
                }

                // Make sure its a valid result
                try {
                    parser.getResult();
                } catch (ParserInvalidResultException e) {
                    return new ArrayList<>();
                }

                result.add(parser);
                context.getParsers().add(parser);
            }
        }

        // Recurse into children
        if (node.getChildren().size() == 0) {
            if (args != null && args.startsWith("-")) {
                result.addAll(switchResolve(null, args, context));
                return result;
            }
        }

        List<Parser> best = new ArrayList<>();
        for (ParserNode child : node.getChildren()) {
            // Make a copy of the context
            ParserContext childContext;
            try {
                childContext = (ParserContext) context.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                break;
            }

            List<Parser> check = resolve(child, args, childContext);
            if (check.size() > best.size()) {
                best = check;
            }
        }
        result.addAll(best);

        return result;
    }

    /**
     * Return completions for a switch
     */
    public List<String> switchComplete(ParserNode node, String args, ParserContext context) {
        while (args != null && args.startsWith("-")) {
            String[] argSplit = args.split(" ", 2);

            // Complete it if last argument
            if (argSplit.length == 1) {
                return context.getSwitches().stream()
                        .flatMap(s -> Arrays.stream(s.getNode().getData().getParameters().get("switch").split("\\|"))
                                .filter(sw -> sw.startsWith(argSplit[0].substring(1)))
                                .limit(1)
                        )
                        .map(s -> "-" + s)
                        .limit(20)
                        .collect(Collectors.toList());
            }

            args = argSplit[1];

            // Look for parser
            Parser switchParser = context.getSwitches().stream()
                    .flatMap(s -> Arrays.stream(s.getNode().getData().getParameters().get("switch").split("\\|"))
                            .filter(sw -> sw.equals(argSplit[0].substring(1)))
                            .limit(1)
                            .map(sw -> s)
                    )
                    .findFirst()
                    .orElse(null);

            if (switchParser == null) {
                return new ArrayList<>();
            }

            try {
                args = switchParser.parse(args);
            } catch (ParserRequiredArgumentException e) {
                return new ArrayList<>();
            }

            if (args == null) {
                return switchParser.getCompletions();
            }

            context.getSwitches().remove(switchParser);
        }

        return complete(node, args, context);
    }

    public List<String> getComplete(ParserNode node, String args, ParserContext context) {
        return complete(node, args, context);
    }

    /**
     * Return a list completions
     */
    public List<String> complete(ParserNode node, String args, ParserContext context) {
        List<String> result = new ArrayList<>();

        if (node == null) {
            return result;
        }

        if (!node.isRoot() && node.getData() != null) {
            ParserNodeData data = node.getData();

            Class<? extends Parser> parserClass = parsers.getOrDefault(data.getName(), LiteralParser.class);

            Parser parser;
            try {
                parser = parserClass.getConstructor(CommandManager.class, ParserNode.class, ParserContext.class)
                        .newInstance(this, node, context);
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }

            // Take care of switches first in case this argument is also a switch
            if (data.getParameters().containsKey("switch")) {
                context.getSwitches().add(parser);

            } else {
                if (args != null && args.startsWith("-")) {
                    return switchComplete(node, args, context);
                }

                try {
                    args = parser.parse(args);
                } catch (ParserRequiredArgumentException e) {
                    return new ArrayList<>();
                }

                if (args != null) {
                    // Make sure its a valid result for non leaf nodes
                    try {
                        parser.getResult();
                    } catch (ParserInvalidResultException e) {
                        return new ArrayList<>();
                    }
                } else {
                    result.addAll(parser.getCompletions());
                }
            }
        }

        // Recurse into children
        if (node.getChildren().size() == 0) {
            if (args != null && args.startsWith("-")) {
                return switchComplete(null, args, context);
            }
        }

        for (ParserNode child : node.getChildren()) {
            // Make a copy of the context
            ParserContext childContext;
            try {
                childContext = (ParserContext) context.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                break;
            }

            result.addAll(complete(child, args, childContext));
        }

        return result;
    }
}
