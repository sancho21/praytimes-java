package id.web.michsan.praytimes;

import static id.web.michsan.praytimes.Configuration.angle;
import static id.web.michsan.praytimes.Configuration.minutes;
import id.web.michsan.praytimes.PrayTimes.Time;

import java.util.HashMap;
import java.util.Map;

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
		MWL.configure(Time.FAJR, angle(18));
		MWL.configure(Time.ISHA, angle(17));

		ISNA = new Method("Islamic Society of North America (ISNA)");
		ISNA.configure(Time.FAJR, angle(15));
		ISNA.configure(Time.ISHA, angle(15));

		EGYPT = new Method("Egyptian General Authority of Survey");
		EGYPT.configure(Time.FAJR, angle(19.5));
		EGYPT.configure(Time.ISHA, angle(17.5));

		MAKKAH = new Method("Umm Al-Qura University, Makkah");
		MAKKAH.configure(Time.FAJR, angle(18.5));
		MAKKAH.configure(Time.ISHA, minutes(90));

		KARACHI = new Method("University of Islamic Sciences, Karachi");
		KARACHI.configure(Time.FAJR, angle(18));
		KARACHI.configure(Time.ISHA, angle(18));

		TEHRAN = new Method("Institute of Geophysics, University of Tehran");
		TEHRAN.configure(Time.FAJR, angle(17.7));
		TEHRAN.configure(Time.ISHA, angle(14));
		TEHRAN.configure(Time.MAGHRIB, angle(4.5));
		TEHRAN.setMidnightMethod(MidnightMethod.JAFARI);

		JAFARI = new Method("Shia Ithna-Ashari, Leva Institute, Qum");
		JAFARI.configure(Time.FAJR, angle(16));
		JAFARI.configure(Time.ISHA, angle(14));
		JAFARI.configure(Time.MAGHRIB, angle(4));
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

	private final Map<Time, Configuration> configurations;
	private double asrFactor;
	private MidnightMethod midnightMethod;
	private HighLatMethod highLatMethod;

	public Method(String name) {
		this.name = name;
		configurations = new HashMap<PrayTimes.Time, Configuration>();

		// Base values for all methods
		setMidnightMethod(MidnightMethod.STANDARD);
		configure(Time.MAGHRIB, minutes(0));
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
	 *            The factor could be {@link #ASR_FACTOR_STANDARD},
	 *            {@link #ASR_FACTOR_HANAFI}.
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

	/**
	 * Get configuration value
	 *
	 * @param time
	 *            Time to adjust the angle
	 * @return Angle in degree
	 */
	public Double getConfigurationValue(Time time) {
		return configurations.get(time) != null ? configurations.get(time)
				.getValue() : null;
	}

	/**
	 *
	 * Set minute difference of specific time or twilight angle.
	 * <ul>
	 * <li>As for {@link PrayTimes.Time#IMSAK}: Minutes before fajr</li>
	 * <li>As for {@link PrayTimes.Time#DHUHR}: Minutes after mid-day</li>
	 * <li>As for {@link PrayTimes.Time#MAGHRIB}: Minutes after sunset</li>
	 * <li>As for {@link PrayTimes.Time#ISHA}: Minutes after maghrib</li>
	 * </ul>
	 *
	 * @param time
	 *            Time to adjust the minutes. The valid times are
	 *            {@link PrayTimes.Time#IMSAK}, {@link PrayTimes.Time#DHUHR},
	 *            {@link PrayTimes.Time#MAGHRIB} and {@link PrayTimes.Time#ISHA}
	 * @param configuration
	 *            angle in degree
	 */
	public void configure(Time time, Configuration configuration) {
		validateConfiguration(time, configuration);
		configurations.put(time, configuration);
	}

	private void validateConfiguration(Time time, Configuration configuration) {
		if (configuration.getType() == Configuration.TYPE_ANGLE
				&& time != Time.IMSAK && time != Time.FAJR
				&& time != Time.MAGHRIB && time != Time.ISHA) {
			throw new IllegalArgumentException("Can not set angle for " + time);
		}

		if (configuration.getType() == Configuration.TYPE_MINUTE
				&& time != Time.IMSAK && time != Time.DHUHR
				&& time != Time.MAGHRIB && time != Time.ISHA) {
			throw new IllegalArgumentException("Can not set minutes for "
					+ time);
		}
	}

	public boolean hasMinuteConfiguration(Time time) {
		Configuration conf = configurations.get(time);
		return conf != null && conf.getType() == Configuration.TYPE_MINUTE;
	}

	public Configuration getConfiguration(Time time) {
		return configurations.get(time);
	}

	@Override
	public Method clone() {
		Method m = new Method(name);
		m.configurations.clear();
		m.configurations.putAll(configurations);

		m.asrFactor = asrFactor;
		m.midnightMethod = midnightMethod;
		m.highLatMethod = highLatMethod;
		return m;
	}

	@Override
	public String toString() {
		return "Method [name=" + name + ", configurations=" + configurations
				+ ", asrFactor=" + asrFactor + ", midnightMethod="
				+ midnightMethod + ", highLatMethod=" + highLatMethod + "]";
	}

}
