package drones;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Product extends IntWrapper {

	public final int weight;

	public Product(final int id, final int weight) {
		super(id);
		this.weight = weight;
	}

}
