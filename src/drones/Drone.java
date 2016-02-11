package drones;

import java.util.HashMap;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Drone extends IntWrapper {

	private final int maxLoad;

	private Map<Product,Integer> inventory;

	public Drone(final int id, final int maxLoad) {
		super(id);
		this.maxLoad = maxLoad;
		this.inventory = new HashMap<Product, Integer>(Product.numProducts()*4/3+5);
	}
}
