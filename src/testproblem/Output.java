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
