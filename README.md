PrayTimes in Java
=================

The Java version of Islamic PrayTimes Calculator in http://praytimes.org

Usage
=====

In order to use it, just download the library or you may choose to create a JAR file from the source using Maven build system.

`````java
	package mypackage;

	import static id.web.michsan.praytimes.Configuration.angle;
	import static id.web.michsan.praytimes.Configuration.minutes;

	import java.util.GregorianCalendar;
	import java.util.Map;

	import id.web.michsan.praytimes.Location;
	import id.web.michsan.praytimes.Method;
	import id.web.michsan.praytimes.PrayTimes;
	import id.web.michsan.praytimes.PrayTimes.Time;
	import id.web.michsan.praytimes.Util;

	public class HelloPrayTimes {

		public static void main(String[] args) {
			PrayTimes pt = new PrayTimes(Method.ISNA);

			// Adjustments
			pt.adjust(Time.FAJR, angle(20));
			pt.adjust(Time.DHUHR, minutes(2));

			// Offset tunings
			pt.tuneOffset(Time.FAJR, 2);

			// Calculate praytimes
			double elevation = 10;
			double lat = -6.1744444;
			double lng = 106.8294444;
			Location location = new Location(lat, lng, elevation);
			// Timezone is defined in the calendar
			Map<Time, Double> times = pt
					.getTimes(new GregorianCalendar(), location);

			// Print the result
			for (Time t : new Time[] { Time.FAJR, Time.SUNRISE, Time.DHUHR,
					Time.ASR, Time.MAGHRIB, Time.ISHA, Time.MIDNIGHT }) {
				System.out.println(t + " : " + Util.toTime12(times.get(t), false));
			}
		}

	}
`````
