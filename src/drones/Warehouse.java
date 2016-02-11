package drones;

import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Warehouse {

	private final int x,y;

	private Map<Product,Integer> stock;

	public Warehouse(final int x, final int y, final Map<Product, Integer> stock) {
		this.x = x;
		this.y = y;
		this.stock = stock;
	}

	
}
