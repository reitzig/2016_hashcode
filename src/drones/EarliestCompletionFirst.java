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

import java.io.BufferedWriter;
import java.util.*;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class EarliestCompletionFirst implements Solver {

	public EarliestCompletionFirst(final World world) {
		this(world, false);
	}

	public EarliestCompletionFirst(final World world, boolean silent) {
		this.world = world;
		this.silent = silent;
	}

	final World world;

	final boolean silent;

	int currentTime;

	private Score score;
	private boolean dirty = false;

	int nextInterestingTime() {
		int res = Integer.MAX_VALUE;
		for (final Drone drone : world.dronesById.values()) {
			final int tIdle = drone.tIdle;
			if (tIdle < res) res = tIdle;
		}
		return res;
	}

	OrderSchedule fastestSolution(final Order order) throws CloneNotSupportedException {
		// clone state
		Map<Integer, Warehouse> whCopy = new HashMap<Integer, Warehouse>(
			  world.warehouseById.size() * 4 / 3 + 5);
		Map<Integer, Drone> dronesCopy = new HashMap<Integer, Drone>(
			  world.dronesById.size() * 4 / 3 + 5);
		Map<Product, Integer> toDeliver = new HashMap<Product, Integer>(order.quantities);

		for (final Warehouse warehouse : world.warehouseById.values()) {
			whCopy.put(warehouse.id, (Warehouse) warehouse.clone());
		}
		for (final Drone drone : world.dronesById.values()) {
			dronesCopy.put(drone.id, (Drone) drone.clone());
		}

		List<DroneLoad> droneLoads = new LinkedList<DroneLoad>();

		TreeSet<Warehouse> whByDistance = new TreeSet<Warehouse>(
			  new Comparator<Warehouse>() {
				  public int compare(final Warehouse o1, final Warehouse o2) {
					  final int dist1 = World.dist(o1.x, o1.y, order.x, order.y);
					  final int dist2 = World.dist(o2.x, o2.y, order.x, order.y);

					  return dist1 == dist2 ? Integer.compare(o1.id(), o2.id())
							 : Integer.compare(dist1, dist2);
				  }
			  });
		whByDistance.addAll(whCopy.values());

		for (final Warehouse warehouse : whByDistance) {
			int weight;
			do {
				Map<Product, Integer> droneLoad = new HashMap<Product, Integer>();
				weight = 0;
				final Set<Map.Entry<Product, Integer>> toDeliverByWeight =
					  new TreeSet<Map.Entry<Product, Integer>>(
							 new Comparator<Map.Entry<Product, Integer>>() {
								 public int compare(final Map.Entry<Product, Integer> o1,
										final Map.Entry<Product, Integer> o2) {
									 int w1 = o1.getKey().weight;
									 int w2 = o2.getKey().weight;
									 return w1 == w2 ? Integer.compare(o1.getKey().id,
											o2.getKey().id()) : Integer.compare(w1, w2);
								 }
							 });
				toDeliverByWeight.addAll(toDeliver.entrySet());
				for (final Map.Entry<Product, Integer> entry : toDeliverByWeight) {
					final Product product = entry.getKey();
					final Integer Stock = warehouse.stock.get(product);
					int stock = Stock == null ? 0 : Stock;
					final int x = Math.min(entry.getValue(), Math.min(stock,
						  (world.maxLoad - weight) / product.weight));
					if (x == 0) continue;
					droneLoad.put(product, x);
					warehouse.stock.put(product, stock - x);
					entry.setValue(entry.getValue() - x);
					weight += x * product.weight;
				}
				if (weight > 0) {
					droneLoads.add(new DroneLoad(droneLoad, warehouse.id()));
				}
			} while (weight > 0);
		}

		List<Action> actions = new LinkedList<Action>();
		int completionTime = Integer.MIN_VALUE;
		while (!droneLoads.isEmpty()) {
			Drone d = null;
			DroneLoad load = null;
			int costMin = Integer.MAX_VALUE;
			for (final DroneLoad droneLoad : droneLoads) {
				for (final Drone drone : dronesCopy.values()) {
					final Warehouse wh = whCopy.get(droneLoad.warehouseId);
					final int cost = (drone.tIdle - currentTime) + World.dist(drone.x, drone.y,
						  wh.x, wh.y);
					if (cost < costMin) {
						d = drone;
						load = droneLoad;
						costMin = cost;
					}
				}
			}
			actions.addAll(load.toActions(d, order.id()));
			final Warehouse wh = whCopy.get(load.warehouseId);
			d.tIdle += World.dist(d.x, d.y, wh.x, wh.y) + load.load.size() * 2 + World.dist(
				  order.x, order.y, wh.x, wh.y);
			d.x = order.x;
			d.y = order.y;
			completionTime = Math.max(completionTime, d.tIdle);
			dronesCopy.remove(d.id());
			droneLoads.remove(load);
		}

		for (final Integer integer : toDeliver.values()) {
			assert integer == null || integer <= 0;
		}

		return new OrderSchedule(completionTime, order.id(), actions);
	}

	OrderSchedule nextOrder() throws Exception {
		OrderSchedule min = new OrderSchedule(Integer.MAX_VALUE, -1);

		for (final Order order : world.ordersById.values()) {
			final OrderSchedule orderSchedule = fastestSolution(order);
			if (orderSchedule.completionTime < min.completionTime) {
				min = orderSchedule;
			}
		}

		return min;
	}


	public void computeSchedule(BufferedWriter out) throws Exception {
		if ( dirty ) throw new IllegalAccessError("Tried to compute schedule twice");
		dirty = true;

		score = new Score();

		Set<Integer> orderIds = new TreeSet<Integer>();
		while (!world.ordersById.isEmpty()) {
			currentTime = nextInterestingTime();
			final OrderSchedule orderSchedule = nextOrder();
			out.write(orderSchedule.print());
			// update drones
			execute(orderSchedule, world.warehouseById, world.dronesById, world.ordersById);
			if (orderIds.contains(orderSchedule.orderId)) {
				assert false;
			}
			orderIds.add(orderSchedule.orderId);
			if ( !silent )
				System.out.println("[" + (orderIds.size() * 100 / world.nOrders) + "%] " + orderIds.size() + "/" + world.nOrders);
			out.flush();
			score.completionTime = Math.max(score.completionTime, orderSchedule.completionTime);
		}

		if ( !silent) System.out.println("Score: " + score);
	}

	private void execute(final OrderSchedule orderSchedule,
		  Map<Integer, Warehouse> warehouses, Map<Integer, Drone> drones,
		  Map<Integer, Order> orders) {
		int orderId = -1;
		for (final Action action : orderSchedule.actions) {
			if (action instanceof Load) {
				final Load load = (Load) action;
				// remove from warehouse; ahead of time
				final Warehouse wh = warehouses.get(load.warehouseId);
				final Integer old = wh.stock.get(load.product);
				assert old >= load.quantity : "warehouse empty " + wh.id();
				wh.stock.put(load.product, old - load.quantity);
				// set future location of drone
				final Drone drone = drones.get(load.droneId);
				drone.tIdle += World.dist(drone.x, drone.y, wh.x, wh.y) + 1;
				drone.x = wh.x;
				drone.y = wh.y;
			} else if (action instanceof Deliver) {
				final Deliver deliver = (Deliver) action;
				final Drone drone = drones.get(deliver.droneId);
				assert orderId == -1 || orderId == deliver.orderId;
				orderId = deliver.orderId;
				final Order order = orders.get(orderId);
				drone.tIdle += World.dist(drone.x, drone.y, order.x, order.y) + 1;
				drone.x = order.x;
				drone.y = order.y;

				// update order; not really needed, since order will be planned completely
				final Integer old = order.quantities.get(deliver.product);
				order.quantities.put(deliver.product, old - deliver.quantity);
			}

			score.completeOrder(orderSchedule.completionTime);
		}
		assert orderId != -1;
		for (final Integer integer : orders.get(orderId).quantities.values()) {
			assert integer == null || integer <= 0;
		}
		orders.remove(orderId);

	}


	class OrderSchedule implements Printable {

		OrderSchedule(final int completionTime, final int orderId) {
			this.completionTime = completionTime;
			this.orderId = orderId;
		}

		OrderSchedule(final int completionTime, final int orderId, List<Action> actions) {
			this.completionTime = completionTime;
			this.orderId = orderId;
			this.actions.addAll(actions);
		}

		final List<Action> actions = new LinkedList<Action>();

		final int completionTime;

		final int orderId;

		public String print() {
			StringBuilder res = new StringBuilder();
			for (final Action action : actions) {
				res.append(action.print());
				res.append("\n");
			}
			return res.toString();
		}
	}

	class DroneLoad {
		DroneLoad(final Map<Product, Integer> load, final int warehouseId) {
			this.load = load;
			this.warehouseId = warehouseId;
		}

		Map<Product, Integer> load;
		int warehouseId;

		public List<Action> toActions(Drone drone, int orderId) {
			List<Action> res = new LinkedList<Action>();
			for (final Map.Entry<Product, Integer> entry : load.entrySet()) {
				res.add(new Load(drone.id(), entry.getKey(), entry.getValue(), warehouseId));
			}
			for (final Map.Entry<Product, Integer> entry : load.entrySet()) {
				res.add(new Deliver(drone.id(), entry.getKey(), entry.getValue(), orderId));
			}
			return res;
		}

		@Override public String toString() {
			return "DroneLoad(" + "load=" + load + ", warehouseId=" + warehouseId + ')';
		}
	}

	private class Score {

		private Set<Integer> orderCompletionTimes = new HashSet<>();
		int completionTime = world.deadline;

		void completeOrder(int time) {
			orderCompletionTimes.add(time);
		}

		long get() {
			int score = 0;

			for ( int oc : orderCompletionTimes ) {
				score += completionTime - oc;
			}

			return Math.round(Math.ceil(score * 100.0) / completionTime);
		}

		@Override
		public String toString() {
			return Long.toString(get());
		}
	}
}
