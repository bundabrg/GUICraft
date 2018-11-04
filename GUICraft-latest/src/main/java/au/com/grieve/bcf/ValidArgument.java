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

import lombok.Getter;

import java.util.List;

public class ValidArgument {
    @Getter
    private Valid valid;
    @Getter
    private List<String> partials;

    private ValidArgument(Valid valid) {
        this.valid = valid;
        this.partials = null;
    }

    private ValidArgument(Valid valid, List<String> partials) {
        this.valid = valid;
        this.partials = partials;
    }

    public static ValidArgument VALID() {
        return new ValidArgument(Valid.VALID);
    }

    public static ValidArgument INVALID() {
        return new ValidArgument(Valid.INVALID);
    }

    public static ValidArgument PARTIAL(List<String> partials) {
        return new ValidArgument(Valid.PARTIAL, partials);
    }

    public boolean isValid() {
        return valid == Valid.VALID;
    }

    public boolean isPartial() {
        return valid == Valid.PARTIAL;
    }

    public boolean isInvalid() {
        return valid == Valid.INVALID;
    }

    public enum Valid {
        VALID,
        PARTIAL,
        INVALID
    }

}
