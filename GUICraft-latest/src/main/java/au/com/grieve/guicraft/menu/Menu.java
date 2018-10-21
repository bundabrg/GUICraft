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

package au.com.grieve.guicraft.menu;

import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.config.PackageResolver;
import au.com.grieve.guicraft.menu.actions.OpenAction;
import au.com.grieve.guicraft.menu.types.InventoryMenu;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Menu {

    @Getter
    private static Menu instance;

    @Getter
    private Map<String, MenuType> menuTypes = new HashMap<>();
    @Getter
    private Map<String, MenuAction> menuActions = new HashMap<>();

    public Menu() {
        instance = this;

        GUICraft gui = GUICraft.getInstance();

        // Command Replacements
        gui.getCommandManager().getCommandReplacements().addReplacement("menu", "menu|m");

        // Tab Completions
        gui.getCommandManager().getCommandCompletions().registerAsyncCompletion("menu.config", c -> {
            PackageResolver resolver = gui.getLocalConfig().getResolver("menu");
            return new LinkedHashSet<>(resolver.getKeys());
        });
        gui.getCommandManager().getCommandCompletions().registerAsyncCompletion("menu.package", c -> {
            PackageResolver resolver = gui.getLocalConfig().getResolver("menu");
            return new LinkedHashSet<>(resolver.getPackages());
        });

        // Menu Types
        registerMenuType("inventory", new InventoryMenu());

        // Menu Actions
        registerAction("open", new OpenAction());

    }

    /**
     * Register a MenuType
     */
    public void registerMenuType(String name, MenuType type) {
        menuTypes.put(name, type);
    }

    /**
     * Unregister MenuType
     */
    public void unregisterMenuType(String name) {
        menuTypes.remove(name);
    }

    /**
     * Register a new Action
     */
    public void registerAction(String name, MenuAction action) {
        menuActions.put(name, action);
    }

    /**
     * Unregister an Action
     */
    public void unregisterAction(String name) {
        menuActions.remove(name);
    }




    public static Menu getInstance() {
        return instance;
    }

}
