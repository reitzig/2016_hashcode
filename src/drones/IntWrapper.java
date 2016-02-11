package drones;

import java.util.HashMap;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public abstract class IntWrapper implements Printable, Cloneable {
	protected IntWrapper(final int id) {
		this.id = id;
	}

	protected final int id;

	public int id() {
		return id;
	}

	@Override public String toString() {
		return getClass().getSimpleName() + "(" + "id=" + id + ')';
	}

	public String print() {
		return String.valueOf(id());
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof IntWrapper)) return false;

		final IntWrapper that = (IntWrapper) o;

		return id == that.id;

	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
