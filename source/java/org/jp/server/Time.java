package org.jp.server;

import java.io.Serializable;
import java.math.*;

/**
 * A class to encapsulate a two-place time.
 * Time is consistently measured in hours.
 */
public class Time extends BigDecimal implements Serializable {

	static final int scale = 2;
	static final MathContext context = MathContext.UNLIMITED;
	static final Time zero = zero();

	public static Time zero() {
		return new Time(0L);
	}

	public Time() {
		this(0L);
	}

	public Time(long unscaledTime) {
		super(unscaledTime, context);
	}

	public Time(double time) {
		super(time, context);
	}

	public Time(BigDecimal time) {
		this(time.doubleValue());
	}

	public Time(BigInteger unscaledTime) {
		super(unscaledTime, scale, context);
	}

	public Time(String stringTime) {
		super(stringTime, context);
	}

	public String toString() {
		if (isZero()) return "";
		return this.setScale(scale, RoundingMode.HALF_UP).toPlainString();
	}

	public boolean isZero() {
		return this.equals(zero);
	}

	public static Time valueOf(long unscaledTime) {
		return new Time(unscaledTime);
	}

	public static Time valueOf(String stringTime) {
		try { return new Time(stringTime); }
		catch (Exception ex) { return new Time(0L); }
	}

	public Time add(Time t) {
		return new Time(this.doubleValue() + t.doubleValue());
	}

}
