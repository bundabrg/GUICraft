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
import au.com.grieve.bcf.api.exceptions.ParserNoResultException;
import au.com.grieve.bcf.api.exceptions.ParserRequiredArgumentException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Parser {
    // Data
    @Getter
    protected CommandManager manager;
    @Getter
    protected ParserNode node;
    @Getter
    protected ParserContext context;

    @Getter
    private String input;
    @Getter
    private String unused;

    // Cache
    protected List<String> completions;
    protected Object result;

    public Parser(CommandManager manager, ParserNode node, String args, ParserContext context) throws ParserRequiredArgumentException {
        this.manager = manager;
        this.node = node;
        this.context = context;

        String[] data = arguments(args);
        input = data[0];
        unused = data[1];
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

    /**
     * Return tuple containing used and unused input
     *
     * Defaults to removing a single argument
     */
    public String[] arguments(String args) throws ParserRequiredArgumentException {
        String[] result = args.split(" ", 2);

        if (result.length == 0) {
            Map<String, String> parameters = node.getData().getParameters();

            // Check if a default is provided or if its not required
            if (!parameters.containsKey("default") && parameters.getOrDefault("required", "true").equals("true")) {
                throw new ParserRequiredArgumentException();
            }

            return new String[] {parameters.getOrDefault("default", ""), ""};
        }

        return new String[] {result[0], result.length>1?result[1]:null};
    }

    protected List<String> complete() {
        return new ArrayList<>();
    }

    // abstract methods
    protected abstract Object result() throws ParserInvalidResultException;


}
