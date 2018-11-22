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

package au.com.grieve.guicraft.menu.parsers;

import au.com.grieve.bcf.api.CommandManager;
import au.com.grieve.bcf.api.Parser;
import au.com.grieve.bcf.api.ParserContext;
import au.com.grieve.bcf.api.ParserNode;
import au.com.grieve.bcf.api.SingleParser;
import au.com.grieve.bcf.api.exceptions.ParserInvalidResultException;
import au.com.grieve.bcf.api.exceptions.ParserRequiredArgumentException;
import au.com.grieve.guicraft.GUICraft;

import java.util.List;
import java.util.stream.Collectors;

public class MenuConfigParser extends SingleParser {


    public MenuConfigParser(CommandManager manager, ParserNode node, ParserContext context) {
        super(manager, node, context);
    }

    @Override
    protected Object result() throws ParserInvalidResultException {
        return GUICraft.getInstance().getLocalConfig().getResolver("menu").getKeys().stream()
                .filter(s -> s.equals(getInput()))
                .findFirst()
                .orElseThrow(ParserInvalidResultException::new);
    }

    @Override
    protected List<String> complete() {
        return GUICraft.getInstance().getLocalConfig().getResolver("menu").getKeys().stream()
                .filter(s -> s.startsWith(getInput()))
                .limit(20)
                .collect(Collectors.toList());
    }
}
