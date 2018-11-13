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

import lombok.Data;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParserContext {
    // List of Results
    private List<ParserResult> results = new ArrayList<>();

    // Bukkit Command Sender
    private CommandSender sender;

    public Object clone() throws CloneNotSupportedException {
        ParserContext clone = (ParserContext) super.clone();

        // Clone Results
        clone.results = new ArrayList<>(results);

        return clone;
    }

}
