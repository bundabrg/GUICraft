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

package au.com.grieve.bcf.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectUtils {
    /**
     * Get all super classes
     *
     * @param clz
     * @return
     */
    public static Class<?>[] getAllSuperClasses(Class<?> clz) {
        List<Class<?>> list = new ArrayList<>();
        while ((clz = clz.getSuperclass()) != null) {
            list.add(clz);
        }
        return list.toArray(new Class<?>[list.size()]);
    }

    /**
     * Get all interfaces
     *
     * @param clz
     * @return
     */
    public static Class<?>[] getAllInterfaces(Class<?> clz) {
        Set<Class<?>> set = new HashSet<>();
        getAllInterfaces(clz, set);
        return set.toArray(new Class<?>[set.size()]);
    }

    private static void getAllInterfaces(Class<?> clz, Set<Class<?>> visited) {
        if (clz.getSuperclass() != null) {
            getAllInterfaces(clz.getSuperclass(), visited);
        }
        for (Class<?> c : clz.getInterfaces()) {
            if (visited.add(c)) {
                getAllInterfaces(c, visited);
            }
        }
    }
}
