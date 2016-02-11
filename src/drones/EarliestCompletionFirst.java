package drones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
					  return Integer.compare(dist1, dist2);
				  }
			  });
		whByDistance.addAll(whCopy.values());

		for (final Warehouse warehouse : whByDistance) {
			int weight = 0;
			do {
				Map<Product, Integer> droneLoad = new HashMap<Product, Integer>();
				for (final Map.Entry<Product, Integer> entry : toDeliver.entrySet()) {
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

		final OrderSchedule orderSchedule = new OrderSchedule();
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
			orderSchedule.actions.addAll(load.toActions(d, order.id()));
			final Warehouse wh = whCopy.get(load.warehouseId);
			d.tIdle += World.dist(d.x, d.y, wh.x, wh.y) + load.load.size() * 2 + World.dist(
				  order.x, order.y, wh.x, wh.y);
			d.x = order.x;
			d.y = order.y;
			orderSchedule.completionTime = Math.max(orderSchedule.completionTime, d.tIdle);
		}

		return orderSchedule;
	}

	OrderSchedule nextOrder() throws CloneNotSupportedException {
		OrderSchedule min = new OrderSchedule();
		for (final Order order : world.ordersById.values()) {
			final OrderSchedule orderSchedule = fastestSolution(order);
			if (orderSchedule.completionTime < min.completionTime) {
				min = orderSchedule;
			}
		}
		return min;
	}


	public void computeSchedule(BufferedWriter out)
		  throws IOException, CloneNotSupportedException {
		while (!world.ordersById.isEmpty()) {
			currentTime = nextInterestingTime();
			final OrderSchedule orderSchedule = nextOrder();
			out.write(orderSchedule.print());
			// update drones
			execute(orderSchedule, world.warehouseById, world.dronesById, world.ordersById);
		}
	}

	public static void main(String[] args) throws IOException, CloneNotSupportedException {
		final EarliestCompletionFirst first = new EarliestCompletionFirst(
			  World.parse(new BufferedReader(new FileReader(
					 "/home/seb/projects/HashCode2016/busy_day.in"))));
		first.computeSchedule(new BufferedWriter(new PrintWriter(System.out)));
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
			assert orderId != -1;
			for (final Integer integer : orders.get(orderId).quantities.values()) {
				assert integer == 0;
			}
		}
	}


	class OrderSchedule implements Printable {

		final List<Action> actions = new LinkedList<Action>();

		int completionTime = Integer.MIN_VALUE;

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

	}

}
