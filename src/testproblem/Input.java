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

package testproblem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Input {

	public Input(BufferedReader in) throws IOException {
		final String[] dims = in.readLine().split("\\s");
		nRows = Integer.parseInt(dims[0]);
		nCols = Integer.parseInt(dims[1]);
		colored = new boolean[nRows][nCols];

		for (int row = 0; row < nRows; ++row) {
			final String line = in.readLine();
			for (int col = 0; col < nCols; ++col) {
				colored[row][col] = line.charAt(col) == '#';
			}
		}

	}

	int nRows, nCols;
	boolean[][] colored;


	public static void main(String[] args) throws IOException {
		final Input input = new Input(new BufferedReader(new FileReader(
			  "/home/seb/projects/HashCode2016/testproblem/logo.in")));
		for (int row = 0; row < input.nRows; ++row) {
			for (int col = 0; col < input.nCols; ++col) {
				System.out.print(input.colored[row][col] ? '#' : ' ');
			}
			System.out.println();
		}
	}

}
