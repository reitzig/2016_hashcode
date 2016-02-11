package drones;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Product extends IntWrapper {

	private final int weight;

	public Product(final int id, final int weight) {
		super(id);
		this.weight = weight;
	}


	public static int numProducts() {
		return maxUsedId.get(Product.class) + 1;
	}

	public static void main(String[] args) {
		new Product(0,1);
		new Product(1, 1);
		System.out.println("numProducts() = " + numProducts());
		System.out.println("maxUsedId.get(Drone.class) = " + maxUsedId.get(Drone.class));
	}

}
