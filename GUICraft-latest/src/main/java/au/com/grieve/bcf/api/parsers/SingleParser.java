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

import au.com.grieve.bcf.api.CommandManager;
import au.com.grieve.bcf.api.Parser;
import au.com.grieve.bcf.api.ParserContext;
import au.com.grieve.bcf.api.ParserNode;
import au.com.grieve.bcf.api.exceptions.ParserInvalidResultException;
import au.com.grieve.bcf.api.exceptions.ParserRequiredArgumentException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Supports a single argument parser
 */
public abstract class SingleParser extends Parser {
    @Getter
    private String input;

    public SingleParser(CommandManager manager, ParserNode node, ParserContext context) {
        super(manager, node, context);
    }

    public String parse(String input) throws ParserRequiredArgumentException {
        if (input == null) {
            Map<String, String> parameters = node.getData().getParameters();

            // Check if a default is provided or if its not required
            if (!parameters.containsKey("default") && parameters.getOrDefault("required", "true").equals("true")) {
                throw new ParserRequiredArgumentException();
            }

            this.input = parameters.getOrDefault("default", null);
            return null;
        }

        String[] result = input.split(" ", 2);

        this.input = result[0];
        return result.length>1?result[1]:null;
    }

    public List<String> getCompletions() {
        if (input == null) {
            return new ArrayList<>();
        }

        return super.getCompletions();
    }

    public Object getResult() throws ParserInvalidResultException {
        if (input == null) {
            return null;
        }

        return super.getResult();
    }

}
