package drones;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>
 */
public class PrefetchedECF extends ParallelECF {

	public PrefetchedECF(World world) {
		super(world);
	}

	@Override
	public void computeSchedule(BufferedWriter out) throws Exception {
		// Match orders and their closest warehouses
		final Map<Warehouse, Set<Order>> ordersByWarehouse = new HashMap<Warehouse, Set<Order>>();
		for ( final Warehouse wh : world.warehouseById.values() ) {
			ordersByWarehouse.put(wh, new HashSet<Order>());
		}

		for ( final Order o : world.ordersById.values() ) {
			int minDist = Integer.MAX_VALUE;
			Warehouse nearest = null;

			for ( final Warehouse wh : world.warehouseById.values() ) {
				final int dist = World.dist(o.x, o.y, wh.x, wh.y);
				if ( dist < minDist ) {
					minDist = dist;
					nearest = wh;
				}
			}
			assert nearest != null;

			ordersByWarehouse.get(nearest).add(o);
		}

		// Compute surplus and demands for each warehouse
		final Map<Product, Map<Warehouse, Integer>> excesses = new HashMap<>();
		for ( final Product p : world.productsById.values() ) {
			excesses.put(p, new HashMap<Warehouse, Integer>());
		}

		for ( final Warehouse wh : world.warehouseById.values() ) {
			for ( final Product p : world.productsById.values() ) {
				int excess = wh.stock.get(p);

				for ( final Order o : ordersByWarehouse.get(wh) ) {
					excess -= o.quantities.get(p);
				}

				excesses.get(p).put(wh, excess);
			}
		}

		// Schedule drones to move stuff between warehouses
		// TODO implement

		// Commence with ECF
		super.computeSchedule(out);
	}
}
