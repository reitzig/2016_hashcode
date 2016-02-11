package drones;

import java.util.HashMap;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Drone extends IntWrapper {

	public final int maxLoad;

//	private Map<Product,Integer> inventory; // not used at moment

	public int x,y,tIdle;

	public Drone(final int id, final int maxLoad, final int nProducts) {
		super(id);
		this.maxLoad = maxLoad;
//		this.inventory = new HashMap<Product, Integer>(nProducts*4/3+5);
	}

	@Override protected Object clone() throws CloneNotSupportedException {
		// warning: only works without inventory!!!
		return super.clone();
	}

	@Override public String toString() {
		return "Drone(" + id() + "," + "x=" + x + ", y=" + y + ", tIdle=" + tIdle + ')';
	}
}
