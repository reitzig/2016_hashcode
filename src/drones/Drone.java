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
public class Drone extends IntWrapper {

	public final int maxLoad;

//	private Map<Product,Integer> inventory; // not used at moment

	public int x,y,tIdle;

	public Drone(final int id, final int maxLoad, final int nProducts) {
		super(id);
		this.maxLoad = maxLoad;
//		this.inventory = new HashMap<Product, Integer>(nProducts*4/3+5);
	}

	@Override protected Object clone() throws CloneNotSupportedException {
		// warning: only works without inventory!!!
		return super.clone();
	}

	@Override public String toString() {
		return "Drone(" + id() + "," + "x=" + x + ", y=" + y + ", tIdle=" + tIdle + ')';
	}
}
