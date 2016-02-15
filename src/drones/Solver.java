package drones;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by raphael on 15.02.16.
 */
public interface Solver {
	void computeSchedule(BufferedWriter out) throws Exception;
}
