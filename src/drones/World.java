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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class World {
	int nRows, nCols, deadline, maxLoad, nProducts, nWarehouses, nOrders, nDrones;

	Map<Integer, Drone> dronesById;

	Map<Integer, Product> productsById;

	Map<Integer, Warehouse> warehouseById;

	Map<Integer,Order> ordersById;

	public static World parse(BufferedReader in) throws IOException {

		final World res = new World();
		String[] line = in.readLine().split(" ");
		res.nRows = Integer.parseInt(line[0]);
		res.nCols = Integer.parseInt(line[1]);
		res.nDrones = Integer.parseInt(line[2]);
		res.deadline = Integer.parseInt(line[3]);
		res.maxLoad = Integer.parseInt(line[4]);

		res.nProducts = Integer.parseInt(in.readLine());

		res.productsById = new HashMap<Integer, Product>(res.nProducts * 4 / 3 + 5);
		final String[] weights = in.readLine().split(" ");
		for (int i = 0; i < res.nProducts; ++i) {
			res.productsById.put(i, new Product(i, Integer.parseInt(weights[i])));
		}

		res.dronesById = new HashMap<Integer, Drone>(res.nDrones * 4 / 3 + 5);
		for (int i = 0; i < res.nDrones; ++i) {
			res.dronesById.put(i, new Drone(i, res.maxLoad, res.nProducts));
		}

		res.nWarehouses = Integer.parseInt(in.readLine());
		res.warehouseById = new HashMap<Integer, Warehouse>(res.nWarehouses * 4 / 3 + 5);
		for (int i = 0; i < res.nWarehouses; ++i) {
			final String[] wl = in.readLine().split(" ");
			final HashMap<Product, Integer> stock = new HashMap<Product, Integer>(
				  res.nProducts * 4 / 3 + 5);
			final String[] prods = in.readLine().split(" ");
			for (int j = 0; j < res.nProducts; ++j) {
				stock.put(res.productsById.get(j), Integer.valueOf(prods[j]));
			}
			res.warehouseById.put(i, new Warehouse(i, Integer.parseInt(wl[0]), Integer.parseInt(
				  wl[1]), stock));
		}

		// Set starting position of drones to warehouse 0 (cf spec.)
		// Idle-time has already been initialised with 0 correctly.
		final Warehouse wh0 = res.warehouseById.get(0);
		for ( final Drone d : res.dronesById.values() ) {
			d.x = wh0.x;
			d.y = wh0.y;
		}

		res.nOrders = Integer.parseInt(in.readLine());
		res.ordersById = new HashMap<Integer,Order>(res.nOrders*4/3+5);
		for (int i = 0; i < res.nOrders; ++i) {
			final String[] wl = in.readLine().split(" ");
			final Map<Product, Integer> quantities = new TreeMap<Product, Integer>();
			final int nItems = Integer.parseInt(in.readLine());
			final String[] prods = in.readLine().split(" ");
			for (int j = 0; j < nItems; ++j) {
				final Product item = res.productsById.get(Integer.parseInt(prods[j]));
				final Integer old = quantities.get(item);
				quantities.put(item, old == null ? 1 : old + 1);
			}
			res.ordersById.put(i, new Order(i, Integer.parseInt(wl[0]), Integer.parseInt(wl[1]),
				  quantities));
		}
		return res;

	}

	static int dist(int x1, int y1, int x2, int y2) {
		return (int) Math.round(Math.ceil(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2))));
	}


	@Override public String toString() {
		return "Input(" + "nRows=" + nRows + ", nCols=" + nCols + ", deadline=" + deadline
			  + ", maxLoad=" + maxLoad + ')';
	}

	@Deprecated
	public static void main(String[] args) throws IOException {

		String[] inputs =
			  new String[]{"/home/seb/projects/HashCode2016/mother_of_all_warehouses.in",
					 "/home/seb/projects/HashCode2016/redundancy.in",
					 "/home/seb/projects/HashCode2016/busy_day.in"};

		for (final String file : inputs) {
			System.out.println("file = " + file);
			final World world = World.parse(new BufferedReader(new FileReader(file)));
			System.out.println("input = " + world);

			double weightSum = 0;

			for (final Order order : world.ordersById.values()) {
				double weight = 0;
				for (final Map.Entry<Product, Integer> entry : order.quantities.entrySet()) {
					weight += entry.getKey().weight * entry.getValue();
				}
				weightSum += weight;
				System.out.print(weight + ", ");
			}

			System.out.println("avg weight = " + weightSum / world.ordersById.size());

		}
	}

}
