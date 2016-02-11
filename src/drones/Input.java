package drones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Input {
	private int nRows, nCols, deadline, maxLoad;

	private Map<Integer, Drone> dronesById;

	private Map<Integer, Product> productsById;

	private Map<Integer, Warehouse> warehouseById;

	private List<Order> orders;

	public static Input parse(BufferedReader in) throws IOException {

		final Input res = new Input();
		String[] line = in.readLine().split(" ");
		res.nRows = Integer.parseInt(line[0]);
		res.nCols = Integer.parseInt(line[1]);
		int nDrones = Integer.parseInt(line[2]);
		res.deadline = Integer.parseInt(line[3]);
		res.maxLoad = Integer.parseInt(line[4]);

		res.dronesById = new HashMap<Integer, Drone>(nDrones * 4 / 3 + 5);
		for (int i = 0; i < nDrones; ++i) {
			res.dronesById.put(i, new Drone(i, res.maxLoad));
		}

		int nProducts = Integer.parseInt(in.readLine());

		res.productsById = new HashMap<Integer, Product>(nProducts * 4 / 3 + 5);
		final String[] weights = in.readLine().split(" ");
		for (int i = 0; i < nProducts; ++i) {
			res.productsById.put(i, new Product(i, Integer.parseInt(weights[i])));
		}

		int nWarehouses = Integer.parseInt(in.readLine());
		for (int i = 0; i < nWarehouses; ++i) {
			final String[] wl = in.readLine().split(" ");
			final HashMap<Product, Integer> stock = new HashMap<Product, Integer>(
				  Product.numProducts() * 4 / 3 + 5);
			final String[] prods = in.readLine().split(" ");
			for (int j = 0; j < Product.numProducts(); ++j) {
				stock.put(res.productsById.get(j), Integer.valueOf(prods[j]));
			}
			res.warehouseById.put(i, new Warehouse(Integer.parseInt(wl[0]), Integer.parseInt(
				  wl[1]), stock));
		}

		int nOrders = Integer.parseInt(in.readLine());
		for (int i = 0; i < nOrders; ++i) {
			final String[] wl = in.readLine().split(" ");
			final HashMap<Product, Integer> quantities = new HashMap<Product, Integer>(
				  Product.numProducts() * 4 / 3 + 5);
			final int nItems = Integer.parseInt(in.readLine());
			final String[] prods = in.readLine().split(" ");
			for (int j = 0; j < nItems; ++j) {
				final Product item = res.productsById.get(Integer.parseInt(prods[i]));
				final Integer old = quantities.get(item);
				quantities.put(item, old == null ? 1 : old + 1);
			}
			res.orders.add(new Order(i,Integer.parseInt(wl[0]), Integer.parseInt(
				  wl[1]),quantities));
		}
		return res;

	}

}
