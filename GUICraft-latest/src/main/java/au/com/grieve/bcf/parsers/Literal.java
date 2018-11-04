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

package au.com.grieve.bcf.parsers;

import au.com.grieve.bcf.ArgData;
import au.com.grieve.bcf.Parser;
import au.com.grieve.bcf.TreeNode;
import au.com.grieve.bcf.ValidArgument;
import org.bukkit.command.CommandSender;

import java.util.Collections;
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
public class Literal implements Parser {
    @Override
    public ValidArgument isValid(CommandSender sender, List<String> args, TreeNode<ArgData> node) {
        if (args.size() == 0) {
            return ValidArgument.INVALID();
        }

        for (String alias : node.data.getArg().split("\\|")) {
            // Handle Wildcard
            if (alias.equals("*")) {
                args.remove(0);
                return ValidArgument.VALID();
            }

            if (!alias.startsWith(args.get(0))) {
                continue;
            }

            String arg = args.remove(0);

            // If its the last argument then return as a partial
            if (args.size() == 0) {
                return ValidArgument.PARTIAL(Collections.singletonList(alias));
            }

            return ValidArgument.VALID();
        }

        return ValidArgument.INVALID();
    }
}
