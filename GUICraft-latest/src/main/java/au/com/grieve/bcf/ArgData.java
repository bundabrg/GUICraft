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

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ArgData {
    String arg;
    Map<String, String> parameters = new HashMap<>();

    // Method to Call
    Method method;

    ArgData(String arg) {
        this.arg = arg;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ArgData)) {
            return false;
        }

        ArgData argData = (ArgData) obj;
        return argData.arg.equals(arg);
    }

    public static class Parser {
        private Reader reader;

        enum State {
            NAME,
            PARAM_KEY,
            PARAM_VALUE,
            PARAM_VALUE_QUOTE,
            PARAM_VALUE_QUOTE_END,
            PARAM_END
        }

        public Parser(Reader reader) {
            this.reader = reader;
        }

        public List<List<ArgData>> parse() {
            List<List<ArgData>> result = new ArrayList<>();

            List<ArgData> nextData = parseNext();
            while (nextData.size() > 0) {
                result.add(nextData);
                nextData = parseNext();
            }

            return result;
        }

        private List<ArgData> parseNext() {
            List<ArgData> result = new ArrayList<>();
            State state = State.NAME;
            StringBuilder name = new StringBuilder();
            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();

            Map<String, String> parameters = new HashMap<>();


            int i;
            char quote = ' ';

            do {
                try {
                    i = reader.read();
                } catch (IOException e) {
                    break;
                }

                if (i < 0) {
                    break;
                }

                char c = (char) i;

                switch(state) {
                    case NAME:
                        switch (" (,".indexOf(c)) {
                            case 0:
                                if (name.length() > 0) {
                                    result.add(new ArgData(name.toString()));
                                    return result;
                                }
                                break;
                            case 1:
                                if (name.length() > 0) {
                                    result.add(new ArgData(name.toString()));
                                }

                                state = State.PARAM_KEY;
                                parameters = new HashMap<>();
                                key = new StringBuilder();
                                break;
                            case 2:
                                result.add(new ArgData(name.toString()));
                                name = new StringBuilder();
                                break;
                            default:
                                name.append(c);
                        }
                        break;
                    case PARAM_KEY:
                        switch("=".indexOf(c)) {
                            case 0:
                                state = State.PARAM_VALUE;
                                value = new StringBuilder();
                                break;
                            default:
                                key.append(c);
                        }
                        break;
                    case PARAM_VALUE:
                        switch(",)\"'".indexOf(c)) {
                            case 0:
                                parameters.put(key.toString().trim(), value.toString().trim());
                                key = new StringBuilder();
                                state = State.PARAM_KEY;
                                break;
                            case 1:
                                parameters.put(key.toString().trim(), value.toString().trim());
                                state = State.PARAM_END;
                                break;
                            case 2:
                            case 3:
                                if (value.length() == 0) {
                                    quote = c;
                                    state = State.PARAM_VALUE_QUOTE;
                                    break;
                                }
                                break;
                            default:
                                value.append(c);
                        }
                        break;
                    case PARAM_VALUE_QUOTE:
                        switch("\"'\\".indexOf(c)) {
                            case 0:
                            case 1:
                                if (c == quote) {
                                    parameters.put(key.toString().trim(), value.toString().trim());
                                    key = new StringBuilder();
                                    state = State.PARAM_VALUE_QUOTE_END;
                                } else {
                                    value.append(c);
                                }
                                break;
                            case 2:
                                value.append(c);
                                try {
                                    i = reader.read();
                                } catch (IOException e) {
                                    break;
                                }
                                if (i < 0) {
                                    break;
                                }
                                value.append((char) i);
                                break;
                            default:
                                value.append(c);
                        }
                        break;
                    case PARAM_VALUE_QUOTE_END:
                        switch(",)".indexOf(c)) {
                            case 0:
                                state = State.PARAM_KEY;
                                break;
                            case 1:
                                state = State.PARAM_END;
                                break;
                        }
                        break;
                    case PARAM_END:
                        switch (" ".indexOf(c)) {
                            case 0:
                                for (ArgData a : result) {
                                    a.parameters = parameters;
                                }
                                return result;
                        }
                        break;
                }

            } while(true);

            if (state == State.NAME && name.length() > 0) {
                result.add(new ArgData(name.toString()));
            }

            return result;

        }
    }

    public static List<List<ArgData>> parse(Reader reader) {
        Parser parser = new Parser(reader);
        return parser.parse();
    }
}

