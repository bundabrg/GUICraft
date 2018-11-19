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

import au.com.grieve.bcf.api.ArgData;
import au.com.grieve.bcf.api.Parser;
import au.com.grieve.bcf.api.ParserContext;
import au.com.grieve.bcf.api.ParserResult;
import au.com.grieve.bcf.api.exceptions.ParserException;

import java.util.Arrays;
import java.util.List;

/**
 * Handles switches in argument lines
 *
 * A switch looks like:
 *   -switchname {switch parameters}
 */
public class SwitchParser extends Parser {
    @Override
    public ParserResult resolve(ArgData data, List<String> args, ParserContext context) throws ParserException {
        ParserResult result = new ParserResult(data);

        if (args.size() == 0) {
            return result;
        }

        String switchArg = args.remove(0);

        for (ArgData argData : context.getSwitches()) {
            for (String s : argData.getParameters().get("switch").split("\\|")) {
                if (s.startsWith(switchArg.substring(1))) {
                    if (args.size() == 0) {
                        result.getCompletions().add("-" + s);
                        break;
                    }

                    if (s.equals(switchArg.substring(1))) {
                        Parser parser = context.getManager().getParser(argData.getArg());
                        if (parser == null) {
                            return result;
                        }

                        context.getSwitches().remove(argData);

                        // If we have a default argument then add it now if needed
                        if (args.size() == 0 && argData.getParameters().containsKey("default")) {
                            args.addAll(Arrays.asList(argData.getParameters().get("default").split(" ")));
                        }

                        return parser.resolve(argData, args, context);
                    }
                }
            }
        }
        return result;
    }
}
