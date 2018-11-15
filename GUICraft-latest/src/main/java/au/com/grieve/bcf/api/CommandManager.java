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

import au.com.grieve.bcf.api.parsers.DoubleParser;
import au.com.grieve.bcf.api.parsers.IntegerParser;
import au.com.grieve.bcf.api.parsers.LiteralParser;
import au.com.grieve.bcf.api.parsers.StringParser;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandManager {

    protected Map<String, RootCommand> commands = new HashMap<>();
    protected Map<String, Parser> parsers = new HashMap<>();
    protected Parser literalParser = new LiteralParser();
    protected Parser defaultParser = new StringParser();

    public CommandManager() {
        // Register Default Parsers
        registerParser("string", new StringParser());
        registerParser("int", new IntegerParser());
        registerParser("double", new DoubleParser());
    }

    public abstract void registerCommand(BaseCommand cmd);

    public ArgumentParser createParser() {
        return createParser(null);
    }

    public ArgumentParser createParser(String path) {
        return new ArgumentParser(this, path);
    }

    public void registerParser(String name, Parser parser) {
        this.parsers.put("@" + name, parser);
    }

    public void unregisterParser(String name) {
        this.parsers.remove("@" + name);
    }

    public Parser getParser(String name) {
        if (name.startsWith("@")) {
            return parsers.getOrDefault(name, defaultParser);
        } else {
            return literalParser;
        }
    }

}
