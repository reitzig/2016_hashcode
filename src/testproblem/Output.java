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

import com.sun.org.apache.xpath.internal.operations.Operation;

import java.util.List;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public class Output {



	public static class Operation {

	}

	public static class Square extends Operation {

		public Square(final int x, final int y, final int radius) {
			this.x = x;
			this.y = y;
			this.radius = radius;
		}

		private int x,y,radius;

		@Override public String toString() {
			return "PAINT_SQUARE " + x + " " + y + " " + radius;
		}
	}

	public static class Line extends Operation {

	}

	public static class Erase extends Operation {
		
	}
}
