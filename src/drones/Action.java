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
public abstract class Action implements Printable {
	protected Action(final int droneId, final Product product, final int quantity) {
		this.droneId = droneId;
		this.product = product;
		this.quantity = quantity;
	}

	final int droneId;

	final Product product;

	final int quantity;


}
