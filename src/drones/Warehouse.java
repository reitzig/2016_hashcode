package drones;

import java.util.HashMap;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Warehouse extends IntWrapper {

	public final int x,y;

	public Map<Product,Integer> stock;

	public Warehouse(final int id, final int x, final int y,
		  final Map<Product, Integer> stock) {
		super(id);
		this.x = x;
		this.y = y;
		this.stock = stock;
	}

	@Override protected Object clone() throws CloneNotSupportedException {
		return new Warehouse(this.id(), this.x, this.y, new HashMap<Product, Integer>(
			  this.stock));
	}

	@Override public String toString() {
		return "Warehouse("+id() + ", x=" + x + ", y=" + y + ", stock=" + stock + ')';
	}
}
