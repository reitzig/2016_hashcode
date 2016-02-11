package drones;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Input {
	private int nRows, nCols, deadline, maxLoad, nProducts, nWarehouses, nOrders, nDrones;

	private Map<Integer, Drone> dronesById;

	private Map<Integer, Product> productsById;

	private Map<Integer, Warehouse> warehouseById;

	private List<Order> orders;

	public static Input parse(BufferedReader in) throws IOException {

		final Input res = new Input();
		String[] line = in.readLine().split(" ");
		res.nRows = Integer.parseInt(line[0]);
		res.nCols = Integer.parseInt(line[1]);
		res.nDrones = Integer.parseInt(line[2]);
		res.deadline = Integer.parseInt(line[3]);
		res.maxLoad = Integer.parseInt(line[4]);

		res.nProducts = Integer.parseInt(in.readLine());

		res.productsById = new HashMap<Integer, Product>(res.nProducts * 4 / 3 + 5);
		final String[] weights = in.readLine().split(" ");
		for (int i = 0; i < res.nProducts; ++i) {
			res.productsById.put(i, new Product(i, Integer.parseInt(weights[i])));
		}

		res.dronesById = new HashMap<Integer, Drone>(res.nDrones * 4 / 3 + 5);
		for (int i = 0; i < res.nDrones; ++i) {
			res.dronesById.put(i, new Drone(i, res.maxLoad, res.nProducts));
		}

		res.nWarehouses = Integer.parseInt(in.readLine());
		res.warehouseById = new HashMap<Integer, Warehouse>(res.nWarehouses * 4 / 3 + 5);
		for (int i = 0; i < res.nWarehouses; ++i) {
			final String[] wl = in.readLine().split(" ");
			final HashMap<Product, Integer> stock = new HashMap<Product, Integer>(
				  res.nProducts * 4 / 3 + 5);
			final String[] prods = in.readLine().split(" ");
			for (int j = 0; j < res.nProducts; ++j) {
				stock.put(res.productsById.get(j), Integer.valueOf(prods[j]));
			}
			res.warehouseById.put(i, new Warehouse(Integer.parseInt(wl[0]), Integer.parseInt(
				  wl[1]), stock));
		}

		res.nOrders = Integer.parseInt(in.readLine());
		res.orders = new ArrayList<Order>(res.nOrders);
		for (int i = 0; i < res.nOrders; ++i) {
			final String[] wl = in.readLine().split(" ");
			final HashMap<Product, Integer> quantities = new HashMap<Product, Integer>(
				  res.nProducts * 4 / 3 + 5);
			final int nItems = Integer.parseInt(in.readLine());
			final String[] prods = in.readLine().split(" ");
			for (int j = 0; j < nItems; ++j) {
				final Product item = res.productsById.get(Integer.parseInt(prods[j]));
				final Integer old = quantities.get(item);
				quantities.put(item, old == null ? 1 : old + 1);
			}
			res.orders.add(new Order(i, Integer.parseInt(wl[0]), Integer.parseInt(wl[1]),
				  quantities));
		}
		return res;

	}

	@Override public String toString() {
		return "Input(" + "nRows=" + nRows + ", nCols=" + nCols + ", deadline=" + deadline
			  + ", maxLoad=" + maxLoad + ')';
	}

	public static void main(String[] args) throws IOException {

		String[] inputs =
			  new String[]{"/home/seb/projects/HashCode2016/mother_of_all_warehouses.in",
					 "/home/seb/projects/HashCode2016/redundancy.in",
					 "/home/seb/projects/HashCode2016/busy_day.in"};

		for (final String file : inputs) {
			System.out.println("file = " + file);
			final Input input = Input.parse(new BufferedReader(new FileReader(file)));
			System.out.println("input = " + input);

			double weightSum = 0;

			for (final Order order : input.orders) {
				double weight = 0;
				for (final Map.Entry<Product, Integer> entry : order.quantities.entrySet()) {
					weight += entry.getKey().weight * entry.getValue();
				}
				weightSum += weight;
				System.out.print(weight+ ", ");
			}

			System.out.println("avg weight = " + weightSum / input.orders.size());

		}
	}

}
