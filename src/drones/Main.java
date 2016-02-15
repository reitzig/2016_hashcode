package drones;

import java.io.*;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>
 */
public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Usage: EarliestCompletionFirst in out");
			System.exit(2);
		}
		System.out.println("alg: " + args[0]);
		System.out.println("in: " + args[1]);
		System.out.println("out: " + args[2]);

		final BufferedWriter out = new BufferedWriter(new FileWriter(args[2]));
		final World world = World.parse(new BufferedReader(new FileReader(args[1])));

		final Solver solver;
		if ( "EarliestCompletionFirst".equals(args[0]) ) {
			solver = new EarliestCompletionFirst(world);
		}
		else if ( "ParallelECF".equals(args[0]) ) {
			solver = new ParallelECF(world);
		}
		else {
			throw new RuntimeException("Unknown algorithm " + args[0]);
		}

		solver.computeSchedule(out);
		out.close();
	}
}
