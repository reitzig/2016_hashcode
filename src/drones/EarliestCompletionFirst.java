package drones;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class EarliestCompletionFirst {

	public EarliestCompletionFirst(final World world) {
		this.world = world;
	}

	final World world;

	int currentTime;

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
		Map<Integer,Warehouse> whCopy = new HashMap<Integer,Warehouse>(world.warehouseById.size()*4/3+5);
		Map<Integer,Drone> dronesCopy = new HashMap<Integer,Drone>(world.dronesById.size()*4/3+5);
		Map<Product, Integer> toDeliver = new HashMap<Product, Integer>(order.quantities);
		for (final Warehouse warehouse : world.warehouseById.values()) {
			whCopy.put(warehouse.id, (Warehouse) warehouse.clone());
		}
		for (final Drone drone : world.dronesById.values()) {
			dronesCopy.put(drone.id, (Drone) drone.clone());
		}

		TreeSet<Warehouse> whByDistance = new TreeSet<Warehouse>(new Comparator<Warehouse>() {
			public int compare(final Warehouse o1, final Warehouse o2) {
				final int dist1 = World.dist(o1.x, o1.y, order.x, order.y);
				final int dist2 = World.dist(o2.x, o2.y, order.x, order.y);
				return Integer.compare(dist1, dist2);
			}
		});
		whByDistance.addAll(whCopy.values());
		for (final Warehouse warehouse : whByDistance) {

		}

	}

	Order nextOrder() {
		// uses currentTime
	}


	public void computeSchedule(BufferedWriter out)
		  throws IOException, CloneNotSupportedException {
		while (!world.ordersById.isEmpty()) {
			currentTime = nextInterestingTime();
			final Order order = nextOrder();
			final OrderSchedule orderSchedule = fastestSolution(order);
			out.write(orderSchedule.print());
			// update drones
			execute(orderSchedule, world.warehouseById, world.dronesById, world.ordersById);
		}
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
				assert old >= load.quantity : "warehouse empty " + wh.id() ;
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
			assert orderId != -1;
			for (final Integer integer : orders.get(orderId).quantities.values()) {
				assert integer == 0;
			}
		}
	}


	class OrderSchedule implements Printable {

		final List<Action> actions = new LinkedList<Action>();

		public String print() {
			StringBuilder res = new StringBuilder();
			for (final Action action : actions) {
				res.append(action.print());
				res.append("\n");
			}
			return res.toString();
		}
	}

}
