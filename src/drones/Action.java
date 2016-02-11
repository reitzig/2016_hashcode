package drones;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public abstract class Action implements Printable {
	protected Action(final int droneId, final Product product, final int quantity) {
		this.droneId = droneId;
		this.product = product;
		this.quantity = quantity;
	}

	final int droneId;

	final Product product;

	final int quantity;


}
