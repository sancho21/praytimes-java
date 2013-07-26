package org.praytimes;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.praytimes.Method.HighLatMethod;
import org.praytimes.Method.MidnightMethod;

/**
 * <p>
 * Pray times calculator based on <a href="http://praytimes.org">PrayTimes.js</a>: Prayer Times Calculator (ver 2.3).
 * </p>
 * <p>
 * <strong>Usage:</strong><br/>
 * <code>
 * {@link PrayTimes} pt = new {@link PrayTimes}({@link Method#ISNA});<br/>
 * pt.adjustMinutes({@link Time#MAGHRIB}, 1);<br/>
 * pt.adjustAngle({@link Time#ISHA}, 18);<br/>
 * pt.tuneOffset({@link Time#FAJR}, 2);<br/>
 * <br/>
 * Map<Time, Double> times = pt.getTimes(new GregorianCalendar(2009, 2, 27), new Location(-6.1744444, 106.8294444, 10));<br/>
 * for ({@link Time} t : new {@link Time}[] { {@link Time#FAJR}, {@link Time#SUNRISE}, {@link Time#DHUHR}, {@link Time#ASR}, {@link Time#MAGHRIB}, {@link Time#ISHA} }) { <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;// System.out.println(t + " : " + {@link Util}.toTime12(times.get(t), false)); <br/>
 * }
 * </code>
 * </p>
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class PrayTimes {

	public enum Time {
		IMSAK, FAJR, SUNRISE, DHUHR, ASR, SUNSET, MAGHRIB, ISHA, MIDNIGHT
	}

	private final Method method;
	private final int numIterations = 1;
	private final Map<Time, Integer> offsets;
	private double lat, lng, elv, jDate;
	private int timeZone;

	public PrayTimes() {
		this(Method.MWL);
	}

	public PrayTimes(Method calculationMethod) {
		method = calculationMethod.clone();

		method.setMinutes(Time.IMSAK, 10);
		method.setMinutes(Time.DHUHR, 0);
		method.setAsrFactor(Method.ASR_FACTOR_STANDARD);
		method.setHighLatMethod(HighLatMethod.NIGHT_MIDDLE);

		// TODO: This is to fix "eval to assume minutes as angle". I don't know
		// if this is correct.
		method.setAngle(Time.IMSAK, 10);
		method.setAngle(Time.MAGHRIB, 1);

		// Default offsets
		offsets = new HashMap<Time, Integer>();
	}

	public Method getMethod() {
		return method;
	}

	/**
	 * Adjust minute difference of specific time.
	 * <ul>
	 * <li>As for {@link Time#IMSAK}: Minutes before fajr</li>
	 * <li>As for {@link Time#DHUHR}: Minutes after mid-day</li>
	 * <li>As for {@link Time#MAGHRIB}: Minutes after sunset</li>
	 * <li>As for {@link Time#ISHA}: Minutes after maghrib</li>
	 * </ul>
	 *
	 * @param time
	 *            Time to adjust the minutes. The valid times are
	 *            {@link Time#IMSAK}, {@link Time#DHUHR}, {@link Time#MAGHRIB}
	 *            and {@link Time#ISHA}.
	 * @param minutes
	 *            Minute difference
	 */
	public void adjustMinutes(Time time, int minutes) {
		method.setMinutes(time, minutes);
	}

	/**
	 * Set twilight angle of specific time
	 *
	 * @param time
	 *            Time to adjust the angle
	 * @param angle
	 *            angle in degree
	 */
	public void adjustAngle(Time time, double angle) {
		method.setAngle(time, angle);
	}

	/**
	 * Tune offset of final calculation
	 *
	 * @param time
	 *            Time of the offset
	 * @param minutes
	 *            Minutes of the offset
	 */
	public void tuneOffset(Time time, int minutes) {
		offsets.put(time, minutes);
	}

	/**
	 * Set Asr factor for shadow.
	 *
	 * @param factor
	 *            The factor could be {@link Method#ASR_FACTOR_STANDARD}, {@link Method#ASR_FACTOR_HANAFI}.
	 */
	public void setAsrFactor(double factor) {
		method.setAsrFactor(factor);
	}

	/**
	 * Set method for midnight calculation
	 *
	 * @param midnightMethod
	 *            Midnight method
	 */
	public void setMidnightMethod(MidnightMethod midnightMethod) {
		method.setMidnightMethod(midnightMethod);
	}

	/**
	 * Set higher latitudes methods
	 *
	 * @param highLatMethod
	 *            The method
	 */
	public void setHighLatMethod(HighLatMethod highLatMethod) {
		method.setHighLatMethod(highLatMethod);
	}

	/**
	 * Get wall clock times of pray times for a location in specific date.
	 *
	 * @param date
	 *            Date of pray times
	 * @param location
	 *            Location of calculation
	 * @return Wall clock times
	 */
	public Map<Time, Double> getTimes(GregorianCalendar date, Location location) {
		lat = location.getLat();
		lng = location.getLng();
		elv = location.getElv();

		timeZone = date.getTimeZone().getRawOffset() / 3600000;

		jDate = julian(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1,
				date.get(Calendar.DAY_OF_MONTH)) - lng / (15 * 24);

		return computeTimes();
	}

	//---------------------- Calculation Functions -----------------------

	// compute mid-day time
	private double midDay(double time) {
		double eqt = sunPosition(jDate + time).equation;
		// System.out.println("eqt = " + eqt);
		double noon = DMath.fixHour(12 - eqt);
		return noon;
	}

	// compute the time at which sun reaches a specific angle below horizon
	private double sunAngleTime(double angle, double time, boolean isCcw) {
		double decl = sunPosition(jDate + time).declination;
		double noon = midDay(time);
		double t = 1 / 15.0 * DMath.arccos((-DMath.sin(angle) - DMath.sin(decl)
				* DMath.sin(lat))
				/ (DMath.cos(decl) * DMath.cos(lat)));
		return noon + (isCcw ? -t : t);
	}

	// compute asr time
	private double asrTime(double factor, double time) { // BENAR
		double decl = sunPosition(jDate + time).declination;
		double angle = -DMath.arccot(factor + DMath.tan(Math.abs(lat - decl)));
		return sunAngleTime(angle, time, false);
	}

	// compute declination angle of sun and equation of time
	// Ref: http://aa.usno.navy.mil/faq/docs/SunApprox.php
	private static SunPosition sunPosition(double julianDate) {
		double d = julianDate - 2451545.0;

		double g = DMath.fixAngle(357.529 + 0.98560028 * d);
		double q = DMath.fixAngle(280.459 + 0.98564736 * d);
		double L = DMath.fixAngle(q + 1.915 * DMath.sin(g) + 0.020
				* DMath.sin(2 * g));

		double e = 23.439 - 0.00000036 * d;
		double RA = DMath.arctan2(DMath.cos(e) * DMath.sin(L), DMath.cos(L)) / 15;
		double decl = DMath.arcsin(DMath.sin(e) * DMath.sin(L)); // declination of the Sun
		double eqt = q / 15 - DMath.fixHour(RA); // equation of time

		return new SunPosition(decl, eqt);
	}

	private static class SunPosition {
		private final double declination;
		private final double equation;

		private SunPosition(double declination, double equation) {
			this.declination = declination;
			this.equation = equation;
		}
	}

	// convert Gregorian date to Julian day
	// Ref: Astronomical Algorithms by Jean Meeus
	private double julian(int year, int month, int day) {
		if (month <= 2) {
			year -= 1;
			month += 12;
		}

		double A = Math.floor(year / 100);
		double B = 2 - A + Math.floor(A / 4);

		return Math.floor(365.25 * (year + 4716))
				+ Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;
	}


	//---------------------- Compute Prayer Times -----------------------

	// compute prayer times at given julian date
	private Map<Time, Double> computePrayerTimes(Map<Time, Double> times) {
		times = dayPortion(times);

		Map<Time, Double> compResult = new HashMap<Time, Double>();

		compResult.put(Time.IMSAK, sunAngleTime(method.getAngle(Time.IMSAK), times.get(Time.IMSAK), true));
		compResult.put(Time.FAJR, sunAngleTime(method.getAngle(Time.FAJR), times.get(Time.FAJR), true));
		compResult.put(Time.SUNRISE, sunAngleTime(riseSetAngle(), times.get(Time.SUNRISE), true));
		compResult.put(Time.DHUHR, midDay(times.get(Time.DHUHR)));
		compResult.put(Time.ASR, asrTime(method.getAsrFactor(), times.get(Time.ASR)));
		compResult.put(Time.SUNSET, sunAngleTime(riseSetAngle(), times.get(Time.SUNSET), false));
		compResult.put(Time.MAGHRIB, sunAngleTime(method.getAngle(Time.MAGHRIB), times.get(Time.MAGHRIB), false));
		compResult.put(Time.ISHA, sunAngleTime(method.getAngle(Time.ISHA), times.get(Time.ISHA), false));

		return compResult;
	}

	// compute prayer times
	private Map<Time, Double> computeTimes() {
		// default times
		Map<Time, Double> times = new HashMap<Time, Double>();
		times.put(Time.IMSAK, 5d);
		times.put(Time.FAJR, 5d);
		times.put(Time.SUNRISE, 6d);
		times.put(Time.DHUHR, 12d);
		times.put(Time.ASR, 13d);
		times.put(Time.SUNSET, 18d);
		times.put(Time.MAGHRIB, 18d);
		times.put(Time.ISHA, 18d);

		// main iterations
		for (int i = 1; i <= numIterations; i++)
			times = computePrayerTimes(times);

		adjustTimes(times);

		// add midnight time
		times.put(
				Time.MIDNIGHT,
				method.getMidnightMethod() == MidnightMethod.JAFARI ? times
						.get(Time.SUNSET)
						+ timeDiff(times.get(Time.SUNSET), times.get(Time.FAJR))
						/ 2
						: times.get(Time.SUNSET)
								+ timeDiff(times.get(Time.SUNSET),
										times.get(Time.SUNRISE)) / 2);

		return tuneTimes(times);
	}

	// adjust times
	private void adjustTimes(Map<Time, Double> times) {
		for (Entry<Time, Double> time : times.entrySet()) {
			times.put(time.getKey(), time.getValue() + timeZone - lng / 15);
		}

		if (method.getHighLatMethod() != HighLatMethod.NONE)
			adjustHighLats(times);

		if (method.getMinute(Time.IMSAK) != null)
			times.put(Time.IMSAK,
					times.get(Time.FAJR) - method.getMinute(Time.IMSAK) / 60d);

		if (method.getMinute(Time.MAGHRIB) != null) {
			times.put(Time.MAGHRIB,
					times.get(Time.SUNSET) + method.getMinute(Time.MAGHRIB)
							/ 60d);
		}

		if (method.getMinute(Time.ISHA) != null)
			times.put(Time.ISHA,
					times.get(Time.MAGHRIB) + method.getMinute(Time.ISHA) / 60d);

		if (method.getMinute(Time.DHUHR) != null)
			times.put(Time.DHUHR,
					times.get(Time.DHUHR) + method.getMinute(Time.DHUHR) / 60d);

		// TODO: Info = Double di JS lebih panjang 1 digit presisi:
		// Java = 12.02000680274645
		// JS	= 12.020006802746453
		// System.out.println("params.dhuhr = " + method.getMinute(Time.DHUHR));
		// System.out.println("times.dhuhr = " + times.get(Time.DHUHR));
	}

	private Map<Time, Double> clone(Map<Time, Double> times) {
		Map<Time, Double> clone = new HashMap<PrayTimes.Time, Double>();
		clone.putAll(times);

		return clone;
	}

	// return sun angle for sunset/sunrise
	private double riseSetAngle() {
		// var earthRad = 6371009; // in meters
		// var angle = DMath.arccos(earthRad/(earthRad+ elv));
		double angle = 0.0347 * Math.sqrt(elv); // an approximation
		return 0.833 + angle;
	}

	// apply offsets to the times
	private Map<Time, Double> tuneTimes(Map<Time, Double> times) {
		times = clone(times);

		for (Entry<Time, Double> time : times.entrySet()) {
			Integer off = offsets.get(time.getKey());

			if (off != null) {
				times.put(time.getKey(), time.getValue() + off / 60d);
			}
		}

		return times;
	}

	// adjust times for locations in higher latitudes
	private void adjustHighLats(Map<Time, Double> times) {
		double nightTime = timeDiff(times.get(Time.SUNSET),
				times.get(Time.SUNRISE));

		times.put(
				Time.IMSAK,
				adjustHLTime(times.get(Time.IMSAK), times.get(Time.SUNRISE),
						method.getAngle(Time.IMSAK), nightTime, true));
		times.put(
				Time.FAJR,
				adjustHLTime(times.get(Time.FAJR), times.get(Time.SUNRISE),
						method.getAngle(Time.FAJR), nightTime, true));
		times.put(
				Time.ISHA,
				adjustHLTime(times.get(Time.ISHA), times.get(Time.SUNSET),
						method.getAngle(Time.ISHA), nightTime, false));
		times.put(
				Time.MAGHRIB,
				adjustHLTime(times.get(Time.MAGHRIB), times.get(Time.SUNSET),
						method.getAngle(Time.MAGHRIB), nightTime, false));
	}

	// adjust a time for higher latitudes
	private double adjustHLTime(Double time, double base, double angle,
			double night, boolean isCcw) {
		double portion = nightPortion(angle, night);
		double timeDiff = isCcw ? timeDiff(time, base) : timeDiff(base, time);
		if (time == null || timeDiff > portion)
			time = base + (isCcw ? -portion : portion);
		return time;
	}

	// the night portion used for adjusting times in higher latitudes
	private double nightPortion(double angle, double night) {
		HighLatMethod method = this.method.getHighLatMethod();
		double portion = 1 / 2d; // MidNight
		if (method == HighLatMethod.ANGLE_BASED)
			portion = 1 / 60d * angle;
		if (method == HighLatMethod.ONE_SEVENTH)
			portion = 1 / 7d;
		return portion * night;
	}

	// convert hours to day portions
	private Map<Time, Double> dayPortion(Map<Time, Double> times) {
		Map<Time, Double> dayPortion = new HashMap<Time, Double>();

		for (Entry<Time, Double> time : times.entrySet()) {
			dayPortion.put(time.getKey(), time.getValue() / 24d);
		}

		return dayPortion;
	}

	// compute the difference between two times
	private double timeDiff(double time1, double time2) {
		return DMath.fixHour(time2- time1);
	}


	/* Test drive ----------------------------------------------------------- */
// TODO: method sunPosition tidak sama!!!
	public static void main(String[] args) {
		double x = sunPosition(2456497.2032515435 + 0.25).equation;
		System.out.println("X = " + x);
	}

	public static void main2(String[] args) {
		PrayTimes pt = new PrayTimes(Method.ISNA);
		pt.adjustAngle(Time.FAJR, 20);
		pt.adjustMinutes(Time.DHUHR, 2);
		pt.adjustMinutes(Time.MAGHRIB, 1);
		pt.adjustAngle(Time.ISHA, 18);

		pt.tuneOffset(Time.FAJR, 2);
		pt.tuneOffset(Time.SUNRISE, -2);
		pt.tuneOffset(Time.ASR, 2);
		pt.tuneOffset(Time.MAGHRIB, 2);
		pt.tuneOffset(Time.ISHA, 2);

		today(pt);

		if (args.length > 0) {
			aYear(pt);
		}
	}

	private static void aYear(PrayTimes pt) {
		GregorianCalendar cal = new GregorianCalendar(2011, 0, 1);
		for (int i = 0; i < 365; i++) {
			System.out.print("@"
					+ cal.get(Calendar.DAY_OF_MONTH)
					+ "-"
					+ cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
							Locale.getDefault()) + " = ");

			Map<Time, Double> times = pt.getTimes(cal, new Location(-6.1744444,
					106.8294444, 10));
			for (Time t : new Time[] { Time.FAJR, Time.SUNRISE, Time.DHUHR,
					Time.ASR, Time.MAGHRIB, Time.ISHA, Time.MIDNIGHT }) {
				System.out.print(t + " : "
					+ Util.toTime12(times.get(t), false));
				System.out.print(",");
			}
			// System.out.println();

			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	private static void today(PrayTimes pt) {
		Map<Time, Double> times = pt.getTimes(new GregorianCalendar(),
				new Location(-6.1744444, 106.8294444, 10));

		for (Time t : new Time[] { Time.FAJR, Time.SUNRISE, Time.DHUHR,
				Time.ASR, Time.MAGHRIB, Time.ISHA, Time.MIDNIGHT }) {
			System.out.println(t + " : "
					+ Util.toTime12(times.get(t), false));
		}
	}
}
