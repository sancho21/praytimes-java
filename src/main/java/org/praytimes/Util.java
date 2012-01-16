package org.praytimes;

import java.text.DecimalFormat;

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
		DayTime dt = toDayTime(time, true);
		DecimalFormat f = new DecimalFormat("00");

		return f.format(dt.hours) + ":" + f.format(dt.minutes);
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
		DayTime dt = toDayTime(time, true);

		String suffix;
		if (dt.hours >= 12) {
			suffix = "pm";
		} else {
			suffix = "am";
		}
		int hours = (dt.hours + 12 - 1) % 12 + 1;

		DecimalFormat f = new DecimalFormat("00");
		return f.format(hours) + ":" + f.format(dt.minutes) + (noSuffix ? "" : " " + suffix);
	}

	/**
	 * Get day time
	 *
	 * @param time
	 *            Time to convert
	 * @param ignoreSeconds
	 *            If true then time will be added half minute.
	 * @return Day time
	 */
	public static DayTime toDayTime(double time, boolean ignoreSeconds) {
		// add 0.5 minutes to round
		time = DMath.fixHour(time + (ignoreSeconds ? 0.5 / 60 : 0));

		int hours = (int) Math.floor(time); // 2
		int minutes = (int) Math.floor((time - hours) * 60);
		int seconds = 0;
		if (!ignoreSeconds)  {
			seconds = (int) Math.floor(((time - hours) * 60 - minutes) * 60);
		}

		return new DayTime(hours, minutes, seconds);
	}

	public static class DayTime {
		int hours, minutes, seconds;

		public DayTime(int hours, int minutes, int seconds) {
			this.hours = hours;
			this.minutes = minutes;
			this.seconds = seconds;
		}

		public int getHours() {
			return hours;
		}

		public int getMinutes() {
			return minutes;
		}

		public int getSeconds() {
			return seconds;
		}
	}
}
