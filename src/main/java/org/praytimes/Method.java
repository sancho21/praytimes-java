package org.praytimes;

import java.util.HashMap;
import java.util.Map;

import org.praytimes.PrayTimes.Time;

/**
 * Calculation method
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class Method {

	/**
	 * Muslim World League
	 */
	public static final Method MWL;

	/**
	 * Islamic Society of North America (ISNA)
	 */
	public static final Method ISNA;

	/**
	 * Egyptian General Authority of Survey
	 */
	public static final Method EGYPT;

	/**
	 * Umm Al-Qura University, Makkah
	 */
	public static final Method MAKKAH;

	/**
	 * University of Islamic Sciences, Karachi
	 */
	public static final Method KARACHI;

	/**
	 * Institute of Geophysics, University of Tehran
	 */
	public static final Method TEHRAN;

	/**
	 * Shia Ithna-Ashari, Leva Institute, Qum
	 */
	public static final Method JAFARI;

	static {
		MWL = new Method("Muslim World League");
		MWL.setAngle(Time.FAJR, 18);
		MWL.setAngle(Time.ISHA, 17);

		ISNA = new Method("Islamic Society of North America (ISNA)");
		ISNA.setAngle(Time.FAJR, 15);
		ISNA.setAngle(Time.ISHA, 15);

		EGYPT = new Method("Egyptian General Authority of Survey");
		EGYPT.setAngle(Time.FAJR, 19.5);
		EGYPT.setAngle(Time.ISHA, 17.5);

		MAKKAH = new Method("Umm Al-Qura University, Makkah");
		MAKKAH.setAngle(Time.FAJR, 18.5);
		MAKKAH.setMinutes(Time.ISHA, 90);

		KARACHI = new Method("University of Islamic Sciences, Karachi");
		KARACHI.setAngle(Time.FAJR, 18);
		KARACHI.setAngle(Time.ISHA, 18);

		TEHRAN = new Method("Institute of Geophysics, University of Tehran");
		TEHRAN.setAngle(Time.FAJR, 17.7);
		TEHRAN.setAngle(Time.ISHA, 14);
		TEHRAN.setAngle(Time.MAGHRIB, 4.5);
		TEHRAN.setMidnightMethod(MidnightMethod.JAFARI);

		JAFARI = new Method("Shia Ithna-Ashari, Leva Institute, Qum");
		JAFARI.setAngle(Time.FAJR, 16);
		JAFARI.setAngle(Time.ISHA, 14);
		JAFARI.setAngle(Time.MAGHRIB, 4);
		JAFARI.setMidnightMethod(MidnightMethod.JAFARI);
	}

	public static final int ASR_FACTOR_STANDARD = 1;
	public static final int ASR_FACTOR_HANAFI = 2;

	/**
	 * Midnight methods
	 *
	 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
	 *
	 */
	public enum MidnightMethod {
		/**
		 * The mean time from Sunset to Sunrise
		 */
		STANDARD,

		/**
		 * The mean time from Maghrib to Fajr
		 */
		JAFARI
	};

	/**
	 * Higher latitudes methods
	 *
	 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
	 *
	 */
	public enum HighLatMethod {
		/**
		 * The middle of the night method
		 */
		NIGHT_MIDDLE,

		/**
		 * The angle-based method (recommended)
		 */
		ANGLE_BASED,

		/**
		 * The 1/7th of the night method
		 */
		ONE_SEVENTH,

		/**
		 * No adjustments
		 */
		NONE
	};

	private final String name;

	private final Map<Time, Double> angles;
	private final Map<Time, Integer> minutes; // added into final angles
	private double asrFactor;
	private MidnightMethod midnightMethod;
	private HighLatMethod highLatMethod;

	public Method(String name) {
		this.name = name;
		angles = new HashMap<PrayTimes.Time, Double>();
		minutes = new HashMap<PrayTimes.Time, Integer>();

		// Base values for all methods
		setMidnightMethod(MidnightMethod.STANDARD);
	}

	public String getName() {
		return name;
	}

	public double getAsrFactor() {
		return asrFactor;
	}

	/**
	 * Set Asr factor for shadow.
	 *
	 * @param factor
	 *            The factor could be {@link #ASR_FACTOR_STANDARD}, {@link #ASR_FACTOR_HANAFI}.
	 */
	public void setAsrFactor(double factor) {
		this.asrFactor = factor;
	}

	public MidnightMethod getMidnightMethod() {
		return midnightMethod;
	}

	public void setMidnightMethod(MidnightMethod midnightMethod) {
		this.midnightMethod = midnightMethod;
	}

	public HighLatMethod getHighLatMethod() {
		return highLatMethod;
	}

	public void setHighLatMethod(HighLatMethod highLatMethod) {
		this.highLatMethod = highLatMethod;
	}

	public void copyFrom(Method method) {
		angles.putAll(method.angles);

		if (method.midnightMethod != null) {
			setMidnightMethod(method.midnightMethod);
		}
	}

	/**
	 * Get twilight angle of specific time
	 *
	 * @param time
	 *            Time to adjust the angle
	 * @return Angle in degree
	 */
	public Double getAngle(Time time) {
		return angles.get(time);
	}

	/**
	 * Set twilight angle of specific time
	 *
	 * @param time
	 *            Time to adjust the angle
	 * @param angle
	 *            angle in degree
	 */
	public void setAngle(Time time, double angle) {
		if (time != Time.IMSAK && time != Time.FAJR && time != Time.MAGHRIB
				&& time != Time.ISHA) {
			throw new IllegalArgumentException("Can not set angle for " + time);
		}

		angles.put(time, angle);
	}

	/**
	 * Get minute difference.
	 *
	 * @param time
	 *            Time to adjust the minutes.
	 * @return Minute difference
	 */
	public Integer getMinute(Time time) {
		return minutes.get(time);
	}

	/**
	 * Set minute difference of specific time.
	 * <ul>
	 * <li>As for {@link PrayTimes.Time#IMSAK}: Minutes before fajr</li>
	 * <li>As for {@link PrayTimes.Time#DHUHR}: Minutes after mid-day</li>
	 * <li>As for {@link PrayTimes.Time#MAGHRIB}: Minutes after sunset</li>
	 * <li>As for {@link PrayTimes.Time#ISHA}: Minutes after maghrib</li>
	 * </ul>
	 *
	 * @param time
	 *            Time to adjust the minutes. The valid times are
	 *            {@link PrayTimes.Time#IMSAK}, {@link PrayTimes.Time#DHUHR}, {@link PrayTimes.Time#MAGHRIB}
	 *            and {@link PrayTimes.Time#ISHA}.
	 * @param minutes
	 *            Minute difference
	 */
	public void setMinutes(Time time, int minutes) {
		if (time != Time.IMSAK && time != Time.DHUHR && time != Time.MAGHRIB
				&& time != Time.ISHA) {
			throw new IllegalArgumentException("Can not set minutes for "
					+ time);
		}

		this.minutes.put(time, minutes);
		// setAngle(time, minute / 60d);
	}

	@Override
	public Method clone() {
		Method m = new Method(name);
		m.angles.clear();
		m.angles.putAll(angles);

		m.asrFactor = asrFactor;
		m.midnightMethod = midnightMethod;
		m.highLatMethod = highLatMethod;
		return m;
	}
}
