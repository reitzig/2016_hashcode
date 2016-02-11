package drones;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Deliver extends Action {

	public Deliver(final int drone, final Product product, final int quantity,
		  final int orderId) {
		super(drone, product, quantity);
		this.orderId = orderId;
	}

	int orderId;

	public String print() {
		return droneId + " " + "D" + " " + orderId + " " + product.id() + " " + quantity;
	}

	@Override public String toString() {
		return "Deliver(" + "orderId=" + orderId + ", droneId=" + droneId + ", product="
			  + product + ", quantity=" + quantity + ')';
	}
}
