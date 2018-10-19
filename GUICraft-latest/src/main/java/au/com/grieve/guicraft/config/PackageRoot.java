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

package au.com.grieve.guicraft.config;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

public abstract class PackageRoot extends PackageSection {
    @Getter
    private boolean dirty;

    /**
     * Create a PackageSection that is a child of the PackageConfiguration
     */
    protected PackageRoot(PackageConfiguration root, String namespace, ConfigurationSection proxy) {
        super(root, proxy);
        this.rootNode = this;
        this.namespace = namespace;
    }

    public void setDirty(boolean state) {
        this.dirty = state;
    }

    public void setDirty() {
        setDirty(true);
    }

    public void save() {
    }

}
