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

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Load extends Action {

	public Load(final int drone, final Product product, final int quantity,
		  final int warehouseId) {
		super(drone, product, quantity);
		this.warehouseId = warehouseId;
	}

	final int warehouseId;

	public String print() {
		return droneId + " " + "L" + " " + warehouseId + " " + product.id() + " " + quantity;
	}

	@Override public String toString() {
		return "Load(" + "warehouseId=" + warehouseId +", droneId=" + droneId + ", product=" + product + ", quantity="
			  + quantity + ')';
	}
}
