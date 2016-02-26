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
public class Deliver extends Action {

	public Deliver(final int drone, final Product product, final int quantity,
		  final int orderId) {
		super(drone, product, quantity);
		this.orderId = orderId;
	}

	int orderId;

	public String print() {
		return droneId + " " + "D" + " " + orderId + " " + product.id() + " " + quantity;
	}

	@Override public String toString() {
		return "Deliver(" + "orderId=" + orderId + ", droneId=" + droneId + ", product="
			  + product + ", quantity=" + quantity + ')';
	}
}
