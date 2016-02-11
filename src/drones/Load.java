package drones;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Load extends Action {

	public Load(final int drone, final Product product, final int quantity,
		  final int warehouseId) {
		super(drone, product, quantity);
		this.warehouseId = warehouseId;
	}

	final int warehouseId;

	public String print() {
		return droneId + " " + "D" + " " + warehouseId + " " + product.id() + " " + quantity;
	}

}
