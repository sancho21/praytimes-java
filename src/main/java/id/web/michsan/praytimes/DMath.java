package id.web.michsan.praytimes;
//import static java.lang.Math.toRadians;

/**
 * Helper class based on <a href="http://praytimes.org">PrayTimes.js</a>.
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class DMath {

	private DMath() {

	}

	public static double sin(double d) {
		return Math.sin(toRadians(d));
	}

	public static double cos(double d) {
		return Math.cos(toRadians(d));
	}

	private static double toRadians(double d) {
		return (d * Math.PI) / 180.0;
	}

	public static double tan(double d) {
		return Math.tan(toRadians(d));
	}

	public static double arcsin(double d) {
		return Math.toDegrees(Math.asin(d));
	}

	public static double arccos(double d) {
		return Math.toDegrees(Math.acos(d));
	}

	public static double arctan(double d) {
		return Math.toDegrees(Math.atan(d));
	}

	public static double arccot(double x) {
		return Math.toDegrees(Math.atan(1 / x));
	}

	public static double arctan2(double y, double x) {
		return Math.toDegrees(Math.atan2(y, x));
	}

	/**
	 * Make angle positive within 360 degree
	 *
	 * @param a
	 *            Angle to fix
	 * @return Positive angle
	 */
	public static double fixAngle(double a) {
		return fix(a, 360);
	}

	public static double fixHour(double a) {
		return fix(a, 24);
	}

	public static double fix(double a, double b) {
		a = a - b * (Math.floor(a / b));
		return (a < 0) ? a + b : a;
	}
}
