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

import au.com.grieve.bcf.api.CommandManager;
import au.com.grieve.bcf.api.ParserContext;
import lombok.Data;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@Data
public class BukkitParserContext extends ParserContext {

    // Bukkit Command Sender
    @Getter
    private CommandSender sender;

    public BukkitParserContext(CommandManager manager, CommandSender sender) {
        super(manager);

        this.sender = sender;
    }
}
