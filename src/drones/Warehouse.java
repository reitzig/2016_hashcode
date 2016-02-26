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
public class Warehouse extends IntWrapper {

	public final int x,y;

	public Map<Product,Integer> stock;

	public Warehouse(final int id, final int x, final int y,
		  final Map<Product, Integer> stock) {
		super(id);
		this.x = x;
		this.y = y;
		this.stock = stock;
	}

	@Override protected Object clone() throws CloneNotSupportedException {
		return new Warehouse(this.id(), this.x, this.y, new HashMap<Product, Integer>(
			  this.stock));
	}

	@Override public String toString() {
		return "Warehouse("+id() + ", x=" + x + ", y=" + y + ", stock=" + stock + ')';
	}
}
