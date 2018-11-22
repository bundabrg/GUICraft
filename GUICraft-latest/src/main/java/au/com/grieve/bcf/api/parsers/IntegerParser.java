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
import au.com.grieve.bcf.api.BaseParser;
import au.com.grieve.bcf.api.ParserContext;
import au.com.grieve.bcf.api.ParserResult;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerBaseParser extends BaseParser {
    @Override
    public ParserResult resolve(ArgData data, List<String> args, ParserContext context) {
        ParserResult result = new ParserResult(data);

        if (args.size() == 0) {
            return result;
        }

        String arg = args.remove(0);
        Integer argInt;
        try {
            argInt = Integer.valueOf(arg);
        } catch (NumberFormatException e) {
            if (arg.length() != 0) {
                return result;
            }
            argInt = null;
        }

        result.getArgs().add(arg);

        // Completions use range parameters
        if (result.getParameters().containsKey("max")) {
            int max = Integer.valueOf(result.getParameters().get("max"));
            int min = Integer.valueOf(result.getParameters().getOrDefault("min", "0"));
            result.getCompletions().addAll(IntStream.rangeClosed(min, max)
                    .mapToObj(String::valueOf)
                    .filter(s -> s.startsWith(arg))
                    .limit(20)
                    .collect(Collectors.toList()));

            if (argInt != null && (argInt >= min && argInt <= max)) {
                result.getResults().add(argInt);
            }
        } else {
            if (argInt != null) {
                result.getResults().add(argInt);
            }
        }

        return result;
    }
}
