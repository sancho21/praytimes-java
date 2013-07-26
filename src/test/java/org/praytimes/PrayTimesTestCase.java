package org.praytimes;

import static java.util.Calendar.JANUARY;
import static java.util.Calendar.JULY;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.junit.Test;
import org.praytimes.PrayTimes.Time;

/**
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class PrayTimesTestCase {
	@Test
	public void shouldWork() {
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

		GregorianCalendar cal = new GregorianCalendar(2011, JANUARY, 17);
		Map<Time, Double> times = pt.getTimes(cal, new Location(-6.1744444,
				106.8294444, 10));

		assertEquals("04:28 am", Util.toTime12(times.get(Time.FAJR), false));
		assertEquals("05:47 am", Util.toTime12(times.get(Time.SUNRISE), false));
		assertEquals("12:05 pm", Util.toTime12(times.get(Time.DHUHR), false));
		assertEquals("03:29 pm", Util.toTime12(times.get(Time.ASR), false));
		assertEquals("06:19 pm", Util.toTime12(times.get(Time.MAGHRIB), false));
		assertEquals("07:32 pm", Util.toTime12(times.get(Time.ISHA), false));
	}

	@Test
	public void shouldBeTheSameWithJsVersion() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("src/test/file/PrayTimes.js.10years.txt"));
		String line;

		PrayTimes pt = new PrayTimes();
		pt.adjustAngle(Time.FAJR, 20);
		pt.adjustMinutes(Time.DHUHR, 2);
		pt.adjustAngle(Time.MAGHRIB, 1);
		pt.adjustAngle(Time.ISHA, 18);

		pt.tuneOffset(Time.FAJR, 2);
		pt.tuneOffset(Time.SUNRISE, -2);
		pt.tuneOffset(Time.ASR, 2);
		pt.tuneOffset(Time.MAGHRIB, 2);
		pt.tuneOffset(Time.ISHA, 2);

		GregorianCalendar cal = new GregorianCalendar(2013, JULY, 24);
		Location location = new Location(-6.1744444, 106.8294444);

		int day = 1;
		while ((line = reader.readLine()) != null) {
			String[] splits = line.split("\\ : ");
			splits = splits[1].split(" \\| ");

			String fajr = splits[0].trim();
			String sunrise = splits[1].trim();
			String dhuhr = splits[2].trim();
			String asr = splits[3].trim();
			String maghrib = splits[4].trim();
			String isha = splits[5].trim();

			Map<Time, Double> times = pt.getTimes(cal, location);

//			for (Time t : new Time[] { Time.FAJR, Time.SUNRISE, Time.DHUHR,
//					Time.ASR, Time.MAGHRIB, Time.ISHA }) {
//				System.out.println(times.get(t));
//			}

			String info = "Failed at day " + day + ": " + cal.getTime();
			assertEquals(info, fajr, Util.toTime24(times.get(Time.FAJR)));
			assertEquals(info, sunrise, Util.toTime24(times.get(Time.SUNRISE)));
			assertEquals(info, dhuhr, Util.toTime24(times.get(Time.DHUHR)));
			assertEquals(info, asr, Util.toTime24(times.get(Time.ASR)));
			assertEquals(info, maghrib, Util.toTime24(times.get(Time.MAGHRIB)));
			assertEquals(info, isha, Util.toTime24(times.get(Time.ISHA)));

			cal.add(Calendar.DATE, 1);


			day++;
		}

		reader.close();
	}
}
