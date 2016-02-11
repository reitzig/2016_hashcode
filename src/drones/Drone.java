package drones;

import java.util.HashMap;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Drone extends IntWrapper {

	public final int maxLoad;

	public Map<Product,Integer> inventory;

	public Drone(final int id, final int maxLoad, final int nProducts) {
		super(id);
		this.maxLoad = maxLoad;
		this.inventory = new HashMap<Product, Integer>(nProducts*4/3+5);
	}
}
