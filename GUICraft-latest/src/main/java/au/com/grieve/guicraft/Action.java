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

package au.com.grieve.guicraft;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Action {

    @Getter
    private Object config;

    public Action(Object config) {
        this.config = config;
    }

    public void execute(CommandSender sender, String prefix, String[] matches) {
        for (String action : parseConfiguration(config, matches)) {
            String command = (prefix == null ? "" : prefix + " ") + action;
            System.err.println(command);
            Bukkit.dispatchCommand(sender, command);
        }
    }

    /**
     * Parse action from Configuration, returning list of Actions to execute
     *
     * @param config
     * @param subActions
     * @return
     */
    private List<String> parseConfiguration(Object config, String[] subActions) {
        List<String> result = new ArrayList<>();
        System.err.println("Config: " + config);

        if (config == null) {
            return result;
        }

        // If it is basic action(s) then its always executed
        if (!(config instanceof ConfigurationSection)) {
            // If its a list, then add them all
            if (config instanceof List<?>) {
                for (Object object : (List<?>) config) {
                    result.add(String.valueOf(object));
                }
            } else {
                result.add(String.valueOf(config));
            }
            return result;
        }

        // No subActions? Done
        if (subActions == null) {
            return result;
        }

        // Otherwise parse subActions that match and recurse
        ConfigurationSection section = (ConfigurationSection) config;
        Integer bestIndex = null;
        List<String> subActionList = Arrays.asList(subActions);
        for (String key : section.getKeys(false)) {
            if (subActionList.contains(key)) {
                int index = subActionList.indexOf(key);
                if (bestIndex == null || bestIndex > index) {
                    bestIndex = index;
                }

                if (index == 0) {
                    break;
                }
            }
        }

        if (bestIndex != null) {
            result.addAll(parseConfiguration(section.get(subActionList.get(bestIndex)), null));
        }

        return result;
    }
}
