package org.praytimes;

/**
 * Helper class based on <a href="http://praytimes.org">PrayTimes.js</a>.
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class Util {

	private Util() {

	}

	/**
	 * Convert double hours to 24h format
	 *
	 * @param time
	 *            Time to convert
	 * @return Time in 24h format
	 */
	public static String floatToTime24(double time) {
		String result;

		time = DMath.fixHour(time + 0.5 / 60.0); // add 0.5 minutes to round
		int hours = (int) Math.floor(time);
		double minutes = Math.floor((time - hours) * 60.0);

		if (hours >= 0 && hours <= 9 && minutes >= 0 && minutes <= 9) {
			result = "0" + hours + ":0" + Math.round(minutes);
		} else if (hours >= 0 && hours <= 9) {
			result = "0" + hours + ":" + Math.round(minutes);
		} else if (minutes >= 0 && minutes <= 9) {
			result = hours + ":0" + Math.round(minutes);
		} else {
			result = hours + ":" + Math.round(minutes);
		}
		return result;
	}

	/**
	 * Convert double hours to 12h format
	 *
	 * @param time
	 *            Time to convert
	 * @param noSuffix
	 *            If false, then "pm" or "am" is added
	 * @return Time in 12h format
	 */
	public static String floatToTime12(double time, boolean noSuffix) {
		time = DMath.fixHour(time + 0.5 / 60); // add 0.5 minutes to round
		int hours = (int) Math.floor(time);
		double minutes = Math.floor((time - hours) * 60);
		String suffix, result;
		if (hours >= 12) {
			suffix = "pm";
		} else {
			suffix = "am";
		}
		hours = (hours + 12 - 1) % 12 + 1;
		/*
		 * hours = (hours + 12) - 1; int hrs = (int) hours % 12; hrs += 1;
		 */

		if (hours >= 0 && hours <= 9 && minutes >= 0 && minutes <= 9) {
			result = "0" + hours + ":0" + Math.round(minutes);
		} else if (hours >= 0 && hours <= 9) {
			result = "0" + hours + ":" + Math.round(minutes);
		} else if (minutes >= 0 && minutes <= 9) {
			result = hours + ":0" + Math.round(minutes);
		} else {
			result = hours + ":" + Math.round(minutes);
		}

		if (!noSuffix) {
			result += suffix;
		}

		return result;
	}
}
