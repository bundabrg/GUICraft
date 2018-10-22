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

package au.com.grieve.guicraft.vault;

import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.vault.commands.VaultCommands;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public class Vault {
    @Getter
    private static Vault instance;

    @Getter
    private Economy economy;

    public Vault(Economy economy) {
        this.economy = economy;

        GUICraft gui = GUICraft.getInstance();

        // Command Replacements
        gui.getCommandManager().getCommandReplacements().addReplacement("vault", "vault|v");

        // Commands
        gui.getCommandManager().registerCommand(new VaultCommands());

    }

    public static void init() {
        if (instance == null) {
            // Check if Vault is available
            if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
                return
            }

            // Check if an Economy Provider is available
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return;
            }

            Bukkit.getLogger().log(Level.INFO, "Hooking Vault.");
            instance = new Vault(rsp.getProvider());
        }
    }
}
