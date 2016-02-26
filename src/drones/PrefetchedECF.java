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

import java.io.*;
import java.util.*;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>
 */
public class PrefetchedECF extends ParallelECF {

	public PrefetchedECF(World world) {
		super(world);
	}

	@Override
	public void computeSchedule(BufferedWriter out) throws Exception {
		System.out.println("Preparing prefetch phase");

		// If there is only one warehouse, skip prefetching phase
		if ( world.warehouseById.size() > 1 ) {
			// Match orders and their closest warehouses
			final Map<Warehouse, Set<Order>> ordersByWarehouse = new HashMap<Warehouse, Set<Order>>();
			for (final Warehouse wh : world.warehouseById.values()) {
				ordersByWarehouse.put(wh, new HashSet<Order>());
			}

			for (final Order o : world.ordersById.values()) {
				int minDist = Integer.MAX_VALUE;
				Warehouse nearest = null;

				for (final Warehouse wh : world.warehouseById.values()) {
					final int dist = World.dist(o.x, o.y, wh.x, wh.y);
					if (dist < minDist) {
						minDist = dist;
						nearest = wh;
					}
				}
				assert nearest != null;

				ordersByWarehouse.get(nearest).add(o);
			}

			// Compute surplus and demands for each warehouse
			final Map<Warehouse, Map<Product, Integer>> excesses = new HashMap<>();
			for (final Warehouse wh : world.warehouseById.values()) {
				excesses.put(wh, new HashMap<Product, Integer>());
			}

			for (final Warehouse wh : world.warehouseById.values()) {
				for (final Product p : world.productsById.values()) {
					int excess = wh.stock.get(p);

					for (final Order o : ordersByWarehouse.get(wh)) {
						excess -= o.quantities.containsKey(p) ? o.quantities.get(p) : 0;
					}

					//System.out.println("wh[" + wh.id + "] prod[" + p.id + "] " + excess);
					excesses.get(wh).put(p, excess);
				}
			}

			// Schedule drones to move stuff between warehouses
			// Decision: this is a small problem that could probably be solved optimally in feasible amounts
			//               of running time. In the spirit of the contest, though, *coding* time is of the essence.
			//               Hence, we just use ECF and see how that turns out.

			//	 1. Create "warehouse world"
			final World whWorld = new World();
			// These things are never changed during ECF so we can copy them without consequence
			whWorld.nRows = world.nRows;
			whWorld.nCols = world.nCols;
			whWorld.deadline = world.deadline;
			whWorld.maxLoad = world.maxLoad;
			whWorld.nProducts = world.nProducts;
			whWorld.nWarehouses = world.nWarehouses;
			whWorld.nDrones = world.nDrones;
			whWorld.productsById = world.productsById;

			// Drones *are* changed but we can use the result -- positions and idle times -- during our main
			// run of ECF as they are.
			whWorld.dronesById = world.dronesById;

			// Warehouses need to be made anew and tailored so only excess items are redistributed
			// Note: One could think to include all stock here so that items can be moved from A to B and B to C concurrently,
			//          instead of everything making the long trip from A to C. However, 1) ECF would not create such schedules
			//          and 2) we have few drones and the triangle inequality, to we suspect it won't help much all too often.
			whWorld.warehouseById = new HashMap<>();
			for (final Map.Entry<Integer, Warehouse> e : world.warehouseById.entrySet()) {
				final Warehouse wh = e.getValue();
				final Map<Product, Integer> stock = new HashMap<>();
				// Compute stock for prefetching phase: positive excess becomes stock, ignore everything else
				for (final Product p : excesses.get(wh).keySet()) {
					if (excesses.get(wh).get(p) > 0) {
						stock.put(p, excesses.get(wh).get(p));
					} else {
						stock.put(p, 0);
					}
				}
				whWorld.warehouseById.put(e.getKey(), new Warehouse(wh.id, wh.x, wh.y, stock));
			}

			// Negative excesses becomes orders. We need to create many small-ish orders so ECF does not
			// prioritize stocking a single warehouse unduly. (Ideally, we'd bin-pack, but well.)
			// TODO: is that a reasonable design decision?
			whWorld.ordersById = new HashMap<>();
			final Map<Integer, Integer> order2wh = new HashMap<>();
			// Failing a better idea short of brute-force, let's just create one order per single-product drone load.
			// Note: data suggests that most products fit into a drone at least twice; with more time,
			//          one could/should investigate how much capacity goes to waste on the given instances.
			// TODO vvvv optimize below vvvv
			int nextId = 0; // our IDs do not conflict with the original ones.
			for (final Warehouse wh : whWorld.warehouseById.values()) {
				for (final Product p : whWorld.productsById.values()) {
					int demand = -excesses.get(wh).get(p);
					//System.out.println(demand);
					while (demand > 0) {
						final int orderSize = Math.min(demand, whWorld.maxLoad / p.weight);
						whWorld.ordersById.put(nextId, new Order(nextId, wh.x, wh.y, createSingletonQuantities(p, orderSize)));
						order2wh.put(nextId, wh.id);
						demand -= orderSize;
						nextId += 1;
					}
					assert excesses.get(wh).get(p) >= 0 || demand == 0 : demand;
				}
			}
			whWorld.nOrders = whWorld.ordersById.size();
			// ^^^^ TODO optimize above ^^^^

			// Skip calling ECF if there are no actual orders
			if ( whWorld.nOrders > 0 ) {
				System.out.println("Scheduling prefetch phase (" + whWorld.ordersById.size() + " orders)");
				// 2. Schedule warehouse world
				final StringWriter prefetchCmdWriter = new StringWriter();
				final BufferedWriter prefetchOut = new BufferedWriter(prefetchCmdWriter);
				final ParallelECF whSolver = new ParallelECF(whWorld, true);
				whSolver.computeSchedule(prefetchOut);
				final String prefetchCommands = prefetchCmdWriter.toString();

				System.out.println("Post-processing prefetch phase");
				// 3. Map output (DELIVER |--> UNLOAD; add wait actions to ensure all LOAD are valid)
				for ( final String line : prefetchCmdWriter.toString().split("\n") ) {
					// Lines have format "dId (L|D) (whId|oId) pId amount"
					final String[] parts = line.split(" ");
					assert parts.length == 5;

					if ( "D".equals(parts[1]) ) {
						out.write(parts[0] + " U " + order2wh.get(Integer.parseInt(parts[2])) + " " + parts[3] + " " + parts[4]);
					}
					else {
						out.write(line);
					}
					out.newLine();
				}


				int maxIdle = Integer.MIN_VALUE;
				for (final Drone d : whWorld.dronesById.values()) {
					maxIdle = Math.max(maxIdle, d.tIdle);
				}
				//System.out.println(maxIdle);
				for (final Drone d : whWorld.dronesById.values()) {
					if (d.tIdle < maxIdle) {
						out.write(d.id + " W " + (maxIdle - d.tIdle));
						out.newLine();
					}
				}

				HashMap<Product, Integer> amountsBefore = null;
				if ( this.getClass().desiredAssertionStatus()  ) { // if -ea
					// Count total stock before prefetching
					amountsBefore = new HashMap<>();
					for ( final Product p : world.productsById.values() ) {
						int amount = 0;
						for (final int whId : world.warehouseById.keySet()) {
							amount += world.warehouseById.get(whId).stock.get(p);
						}
						amountsBefore.put(p, amount);
					}
				}


				// 4. Update original world (warehouse stocks)
				//		 Note: because we make sure all drones are idle at the same time,
				//				   we can leave tIdle = 0 for all drones -- ECF won't care.
				for (final Warehouse wh : world.warehouseById.values()) {
					for (final Product p : world.productsById.values()) {
						int newStock = wh.stock.get(p) 																//  original stock
										     - Math.max(0, excesses.get(wh).get(p))           //  - excess if there was any (got redistributed)
										     + whWorld.warehouseById.get(wh.id).stock.get(p)  // + whatever there is left after prefetching (we don't catch the effect of unloads!)
												 + Math.max(0, -excesses.get(wh).get(p));         // + what got shipped to wh during prefetching
						wh.stock.put(p, newStock);
					}
				}

				HashMap<Product, Integer> amountsAfter = null;
				if ( this.getClass().desiredAssertionStatus()  ) { // if -ea
					// Count total stock after prefetching
					amountsAfter = new HashMap<>();
					for ( final Product p : world.productsById.values() ) {
						int amount = 0;
						for (final int whId : world.warehouseById.keySet()) {
							amount += world.warehouseById.get(whId).stock.get(p);
						}
						amountsAfter.put(p, amount);
					}
				}

				if ( this.getClass().desiredAssertionStatus()  ) { // if -ea
					// Verify that product amounts match.
					for ( final Product p : world.productsById.values() ) {
						assert amountsAfter.get(p) == amountsBefore.get(p)
										: "Product counts don't add up. " + p.id + ": " + amountsBefore.get(p) + " -> " + amountsAfter.get(p);
					}
				}
			}
			else {
				System.out.println("Found that no prefetching is necessary.");
			}
		}
		else {
			System.out.println("Only one warehouse, skipping prefetching phase.");
		}

		// TODO: Idea by Wolfgang: find out which warehouse yields the best score if
		//				  tackled first (resp. its associated orders). Move all drones there initially.
		//             This could be iterated in-order, calling ECF once per warehouse (is that even necessary by then?).

		// Commence with ECF
		System.out.println("Scheduling delivery phase");
		super.computeSchedule(out);
	}

	private Map<Product,Integer> createSingletonQuantities(Product product, int quantity) {
		final Map<Product, Integer> qs = new HashMap<>();

		// Unnecessary?
		//for ( final Product p : world.productsById.values() ) {
		//	qs.put(p, 0);
		//}
		qs.put(product, quantity);
		return qs;
	}
}
