/*
 * Copyright (c) 2016.
 *
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 */

package drones;

import java.io.*;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>
 */
public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("Usage: Main alg in out");
			System.exit(2);
		}

		// Make sure input file is there and parses, and output file is writable.
		final World world = World.parse(new BufferedReader(new FileReader(args[1])));
		final BufferedWriter out = new BufferedWriter(new FileWriter(args[2]));

		// Initialize algorithm
		final Solver solver;
		if ( "EarliestCompletionFirst".equals(args[0]) ) {
			solver = new EarliestCompletionFirst(world);
		}
		else if ( "ParallelECF".equals(args[0]) ) {
			solver = new ParallelECF(world);
		}
		else if ( "PrefetchedECF".equals(args[0]) ) {
			solver = new PrefetchedECF(world);
		}
		else {
			throw new RuntimeException("Unknown algorithm " + args[0]);
		}

		System.out.println("alg: " + args[0]);
		System.out.println("in: " + args[1]);
		System.out.println("out: " + args[2]);
		System.out.println(world);

		solver.computeSchedule(out);
		out.close();
	}
}
