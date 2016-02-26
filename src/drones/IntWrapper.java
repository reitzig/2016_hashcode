/*
 * Copyright (c) 2016.
 *
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 */

package drones;

import java.util.HashMap;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public abstract class IntWrapper implements Printable, Cloneable, Comparable<IntWrapper> {
	protected IntWrapper(final int id) {
		this.id = id;
	}

	protected final int id;

	public int id() {
		return id;
	}

	@Override public String toString() {
		return getClass().getSimpleName() + "(" + "id=" + id + ')';
	}

	public String print() {
		return String.valueOf(id());
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof IntWrapper)) return false;

		final IntWrapper that = (IntWrapper) o;

		return id == that.id;

	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public int compareTo(final IntWrapper o) {
		return Integer.compare(o.id(), this.id());
	}
}
