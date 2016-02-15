package drones;

/**
 * Created by raphael on 15.02.16.
 */
public class ParallelECF extends EarliestCompletionFirst {

	public ParallelECF(World world) {
		super(world);
	}

	@Override
	OrderSchedule nextOrder() throws CloneNotSupportedException {
			OrderSchedule min = new OrderSchedule(Integer.MAX_VALUE, -1);

			// TODO This loop can be parallelized. Major gains possible, as this is *the* expensive part.
			for (final Order order : world.ordersById.values()) {
				final OrderSchedule orderSchedule = fastestSolution(order);
				if (orderSchedule.completionTime < min.completionTime) {
					min = orderSchedule;
				}
			}
			return min;
		}
}
