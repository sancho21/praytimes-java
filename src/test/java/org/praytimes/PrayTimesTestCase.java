package org.praytimes;

import static java.util.Calendar.JANUARY;
import static org.junit.Assert.assertEquals;

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
}
