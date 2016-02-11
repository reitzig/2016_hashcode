package drones;

import java.util.HashMap;
import java.util.Map;

/** @author Sebastian Wild (s_wild@cs.uni-kl.de) */
public abstract class IntWrapper implements Printable {
	protected IntWrapper(final int id) {
		this.id = id;
		final Integer b = maxUsedId.get(getClass());
		maxUsedId.put(getClass(), b == null ? id : Math.max(id, b));
	}

	protected final int id;

	protected static final Map<Class<? extends IntWrapper>, Integer> maxUsedId =
		  new HashMap<Class<? extends IntWrapper>, Integer>(10);

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
}
