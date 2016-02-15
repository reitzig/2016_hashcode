package drones;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Parallelizes the core loop of EarliestCompletionFirst. Otherwise identical; should output the same schedule.
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>
 */
public class ParallelECF extends EarliestCompletionFirst {

	private ThreadPoolExecutor exec;

	public ParallelECF(World world) {
		super(world);
	}

	@Override
	OrderSchedule nextOrder() throws CloneNotSupportedException, ExecutionException, InterruptedException {
		final Map<Order, Future<OrderSchedule>> bestSchedules = new HashMap<>();
		for (final Order order : world.ordersById.values()) {
			final RunnableFuture<OrderSchedule> fut = new FutureTask<>(new Callable<OrderSchedule>() {
				@Override
				public OrderSchedule call() throws Exception {
					return fastestSolution(order);
				}
			});

			bestSchedules.put(order, fut);
			exec.execute(fut);
		}


		OrderSchedule min = new OrderSchedule(Integer.MAX_VALUE, -1);
		for ( final Future<OrderSchedule> osfut : bestSchedules.values() ) {
			if ( osfut.get().completionTime < min.completionTime) {
				min = osfut.get();
			}
		}

		return min;
	}

	@Override
	public void computeSchedule(BufferedWriter out) throws Exception {
		exec = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
							1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(world.ordersById.size()));
		exec.prestartAllCoreThreads();

		super.computeSchedule(out);

		exec.shutdownNow();
	}
}
