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

import java.util.List;

public class DoubleParser extends Parser {
    @Override
    public ParserResult resolve(ArgData data, List<String> args, ParserContext context) {
        ParserResult result = new ParserResult(data);

        if (args.size() == 0) {
            return result;
        }

        String arg = args.remove(0);
        Double argDouble;

        try {
            argDouble = Double.valueOf(arg);
        } catch (NumberFormatException e) {
            if (arg.length() > 0) {
                return result;
            }
            argDouble = null;
        }


        result.getArgs().add(arg);
        if (argDouble != null) {
            result.getResults().add(argDouble);
        }

        return result;
    }
}
