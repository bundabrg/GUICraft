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

package au.com.grieve.guicraft.economy.types;

import au.com.grieve.guicraft.GUICraft;
import au.com.grieve.guicraft.economy.Economy;
import au.com.grieve.guicraft.economy.EconomyType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements EconomyType {

    private static net.milkbowl.vault.economy.Economy vault;

    public static void register() {
        // Check if Vault is available
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
            return;
        }

        // Check if an Economy Provider is available
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            return;
        }

        GUICraft.getInstance().getLogger().info("Hooking Vault");
        vault = rsp.getProvider();

        // Register Economy Type
        Economy.getInstance().registerEconomyType("vault", VaultEconomy.class);
    }
}
