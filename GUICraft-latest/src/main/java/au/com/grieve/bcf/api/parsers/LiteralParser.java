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

package au.com.grieve.bcf.api.parsers;

import au.com.grieve.bcf.api.CommandManager;
import au.com.grieve.bcf.api.Parser;
import au.com.grieve.bcf.api.ParserContext;
import au.com.grieve.bcf.api.ParserNode;
import au.com.grieve.bcf.api.exceptions.ParserNoResultException;
import au.com.grieve.bcf.api.exceptions.ParserOutOfArgumentsException;

import java.util.ArrayList;
import java.util.List;

/**
 * Literal is provided as follows:
 * string1[|string2][|*]
 * <p>
 * Returns a String Type
 * Consumes 1 argument
 * If * is provided then it will accept any input
 * Will use the first matching alias as an alternative for partials
 */
public class LiteralParser extends Parser {


    public LiteralParser(CommandManager manager, ParserNode node, List<String> args, ParserContext context) throws ParserOutOfArgumentsException {
        super(manager, node, args, context);
    }

    @Override
    protected List<String> completions() {
        List<String> result = new ArrayList<>();

        String arg = args.get(0);
        for (String alias : node.getData().getName().split("\\|")) {
            if (alias.equals("*")) {
                result.add(arg);
                return result;
            }

            if (alias.startsWith(arg)) {
                result.add(alias);
                return result;
            }
        }

        return result;
    }

    @Override
    protected Object result() throws ParserOutOfArgumentsException {
        if (args.size() == 0) {
            throw new ParserOutOfArgumentsException();
        }

        String arg = args.get(0);
        for (String alias : node.getData().getName().split("\\|")) {
            if (alias.equals("*")) {
                return arg;
            }

            if (alias.equals(arg)) {
                return arg;
            }
        }

        return new ParserNoResultException();
    }


}
