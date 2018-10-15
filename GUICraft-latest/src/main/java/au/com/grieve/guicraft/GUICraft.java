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

import au.com.grieve.guicraft.actions.OpenAction;
import au.com.grieve.guicraft.commands.GUICraftCommand;
import au.com.grieve.multi_version_plugin.VersionPlugin;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class GUICraft extends VersionPlugin {

    // Variables
    @Getter private BukkitCommandManager commandManager;
    @Getter private Map<String, Class<? extends GUIAction>> actions = new HashMap<>();

    @Override
    public void onEnable() {

        // Register all Commands
        registerCommands();

        // Register Actions
        registerActions();
    }

    private void registerCommands() {
        commandManager = new BukkitCommandManager(getPlugin());
        commandManager.enableUnstableAPI("help");

        commandManager.getCommandReplacements().addReplacement("guicraft","guicraft|gui|gc");
        commandManager.getCommandReplacements().addReplacement("action","action|a");

        commandManager.registerCommand(new GUICraftCommand());
        commandManager.obtainRootCommand("guicraft").
    }

    private void registerActions() {
        registerAction("open", OpenAction.class);
    }

    /**
     * Register a new Action
     */
    public void registerAction(String name, Class<? extends GUIAction> clazz) {
        actions.put(name, clazz);


    }

    /**
     * Unregister an Action
     */
    public void unregisterAction(String name) {
        actions.remove(name);
    }
}
