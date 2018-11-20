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

package au.com.grieve.bcf.api;

import au.com.grieve.bcf.api.exceptions.ParserNoResultException;
import au.com.grieve.bcf.api.exceptions.ParserOutOfArgumentsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Parser {
    // Data
    protected CommandManager manager;
    protected List<String> args;
    protected ParserNode node;
    protected ParserContext context;

    // Cache
    protected List<String> completions;
    protected Object result;

    protected Parser(CommandManager manager, ParserNode node, ParserContext context) {
        this.manager = manager;
        this.node = node;
        this.context = context;
    }

    public Parser(CommandManager manager, ParserNode node, List<String> args, ParserContext context) throws ParserOutOfArgumentsException {
        this(manager, node, context);
        this.args = arguments(args);
    }

    public boolean isValid() {
        return false;
    }

    public List<String> getCompletions() throws ParserOutOfArgumentsException {
        if (completions == null) {
            completions = completions();
        }
        return completions;
    }

    public Object getResult() throws ParserNoResultException, ParserOutOfArgumentsException {
        if (result == null) {
            result = result();
        }
        return result;
    }

    // default methods

    /**
     * Save arguments.
     *
     * By default just a single argument is used
     */
    protected List<String> arguments(List<String> args) throws ParserOutOfArgumentsException {
        if (args.size() == 0) {
            throw new ParserOutOfArgumentsException();
        }

        return new ArrayList<>(Collections.singletonList(args.remove(0)));
    }

    // abstract methods
    protected abstract List<String> completions();
    protected abstract Object result() throws ParserNoResultException;


}
