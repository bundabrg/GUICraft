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

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RootCommand extends Command {
    @Getter
    private ArgumentParser parser;

    protected RootCommand(ArgumentParser parser, String name) {
        super(name);
        this.parser = parser;
    }

    @Override
    public boolean execute(CommandSender commandSender, String alias, String[] args) {
         List<Object> result = parser.getData(sender, args);
        // Reflect into method
        System.err.println("Result: " + result);

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return parser.getAlternatives(sender, args);
    }
}
