package drones;

import java.io.BufferedWriter;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>
 */
public interface Solver {
	void computeSchedule(BufferedWriter out) throws Exception;
}
