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
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class Parser {

    /**
     * Return a list of resolved objects and their text names based upon the arguments provided.
     */
    public abstract ParseResult resolve(CommandSender sender, List<String> args, ArgData data) throws ParserException;

//    public abstract List<String> complete(CommandSender sender, List<String> args, ArgData data)  throws ParserException;

}
