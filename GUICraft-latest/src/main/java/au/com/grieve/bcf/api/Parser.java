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

import au.com.grieve.bcf.api.exceptions.ParserInvalidResultException;
import au.com.grieve.bcf.api.exceptions.ParserRequiredArgumentException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class Parser {
    // Data
    @Getter
    protected CommandManager manager;
    @Getter
    protected ParserNode node;
    @Getter
    protected ParserContext context;

    // Cache
    protected List<String> completions;
    protected Object result;

    public Parser(CommandManager manager, ParserNode node, ParserContext context) {
        this.manager = manager;
        this.node = node;
        this.context = context;
    }

    public List<String> getCompletions() {
        if (completions == null) {
            completions = complete();
        }
        return completions;
    }

    public Object getResult() throws ParserInvalidResultException {
        if (result == null) {
            result = result();
        }

        return result;
    }

    // default methods

    protected List<String> complete() {
        return new ArrayList<>();
    }

    // abstract methods
    protected abstract Object result() throws ParserInvalidResultException;

    /**
     * Take input and return the unused data
     */
    public String parse(String input) throws ParserRequiredArgumentException {
        return input;
    }


}
