PrayTimes in Java
=================

The Java version of Islamic PrayTimes Calculator in http://praytimes.org

Usage
=====

In order to use it, just download the library or you may choose to create a JAR file from the source using Maven build system.

`````java
	package mypackage;

	import static org.praytimes.Configuration.angle;
	import static org.praytimes.Configuration.minutes;

	import java.util.*;
	import org.praytimes.*;

	public static void main2(String[] args) {
		PrayTimes pt = new PrayTimes(Method.ISNA);
		
		// Adjustments
		pt.adjust(Time.FAJR, angle(20));
		pt.adjust(Time.DHUHR, minutes(2));

		// Offset tunings
		pt.tuneOffset(Time.FAJR, 2);
		
		// Calculate praytimes
		Location location = new Location(-6.1744444, 106.8294444, 10);
		Map<Time, Double> times = 
			pt.getTimes(new GregorianCalendar(), location); // Time offset is included in the calendar

		// Print the result
		for (Time t : new Time[] { Time.FAJR, Time.SUNRISE, Time.DHUHR,
				Time.ASR, Time.MAGHRIB, Time.ISHA, Time.MIDNIGHT }) {
			System.out.println(t + " : " + Util.toTime12(times.get(t), false));
		}
	}
`````
