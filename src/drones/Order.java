package drones;

import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Order extends IntWrapper {
	public int x,y;
	public final Map<Product,Integer> quantities;

	public Order(final int id, final int x, final int y,
		  final Map<Product, Integer> quantities) {
		super(id);
		this.x = x;
		this.y = y;
		this.quantities = quantities;
	}

	@Override public String toString() {
		return "Order(" +id() + "," + "x=" + x + ", y=" + y + ", quantities=" + quantities + ')';
	}
}
