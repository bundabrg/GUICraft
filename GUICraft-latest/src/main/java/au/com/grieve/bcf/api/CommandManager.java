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

import au.com.grieve.bcf.api.parsers.DoubleParser;
import au.com.grieve.bcf.api.parsers.IntegerParser;
import au.com.grieve.bcf.api.parsers.LiteralParser;
import au.com.grieve.bcf.api.parsers.StringParser;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.parsers.put(name, parser);
    }

    public void unregisterParser(String name) {
        this.parsers.remove(name);
    }

    /**
     * Return a list of Parsers for the specified arguments
     *
     * A context is passed to provide implementation specific data as well as useful data needed by parsers
     */
    public List<Parser> resolve(ParserNode node, List<String> args, ParserContext context) {
        List<Parser> result = new ArrayList<>();

        for (ParserNode child : node.getChildren()) {
            ParserNodeData data = child.getData();

            // Check validity
            if (data == null) {
                continue;
            }

            // Make a copy of the context
            ParserContext childContext;
            try {
                childContext = (ParserContext) context.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                break;
            }

            Class<? extends Parser> parserClass = parsers.getOrDefault("@" + data.getName(), LiteralParser.class);

            Parser parser;
            try {
                parser = parserClass.getConstructor(CommandManager.class, ParserNode.class, List.class, ParserContext.class)
                        .newInstance(this, child, args, childContext);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            // Valid parser we recurse into
            if (parser.isValid()) {
                List<Parser> childResult = resolve(child, new ArrayList<>(args), childContext);

                if ((childResult.size() + 1) > result.size()) {
                    result = new ArrayList<>();
                    result.add(parser);
                    result.addAll(childResult);
                }
            }
        }

        return result;
    }

}
