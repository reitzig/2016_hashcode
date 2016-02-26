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

import java.io.BufferedWriter;

/**
 * @author Raphael Reitzig <reitzig@cs.uni-kl.de>
 */
public interface Solver {
	void computeSchedule(BufferedWriter out) throws Exception;
}
