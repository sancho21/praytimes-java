package id.web.michsan.praytimes;

import static id.web.michsan.praytimes.Configuration.angle;
import static id.web.michsan.praytimes.Configuration.minutes;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.JULY;
import static org.junit.Assert.assertEquals;
import id.web.michsan.praytimes.PrayTimes.Time;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;

/**
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class PrayTimesTestCase {
	private static final TimeZone SAMPLE_TIME_ZONE = TimeZone.getTimeZone("GMT+7");

	@Test
	public void shouldWork() {
		PrayTimes pt = new PrayTimes(Method.ISNA);
		pt.adjust(Time.FAJR, angle(20));
		pt.adjust(Time.DHUHR, minutes(2));
		pt.adjust(Time.MAGHRIB, minutes(1));
		pt.adjust(Time.ISHA, angle(18));

		pt.tuneOffset(Time.FAJR, 2);
		pt.tuneOffset(Time.SUNRISE, -2);
		pt.tuneOffset(Time.ASR, 2);
		pt.tuneOffset(Time.MAGHRIB, 2);
		pt.tuneOffset(Time.ISHA, 2);

		GregorianCalendar cal = new GregorianCalendar(2011, JANUARY, 17);
		cal.setTimeZone(SAMPLE_TIME_ZONE);
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
	public void shouldBeTheSameWithJsVersion_Default() throws Exception {
		String sampleFile = "src/test/file/sample-00.txt";
		GregorianCalendar cal = new GregorianCalendar(2013, JULY, 24);
		cal.setTimeZone(TimeZone.getTimeZone("GMT-5")); // -5
		assertEquals(-5, cal.getTimeZone().getRawOffset() / 3600000);
		Location location = new Location(43, -80);

		// Nothing to adjust
		Map<Time, Configuration> adjustments = new HashMap<Time, Configuration>();
		// No offsets
		Map<Time, Integer> offsets = new HashMap<Time, Integer>();

		commonTest(sampleFile, cal, location, adjustments, offsets);
	}

	@Test // Indonesia real calculation
	public void shouldBeTheSameWithJsVersion() throws Exception {
		String sampleFile = "src/test/file/sample-01.txt";
		GregorianCalendar cal = new GregorianCalendar(2013, JULY, 24);
		cal.setTimeZone(SAMPLE_TIME_ZONE);
		Location location = new Location(-6.1744444, 106.8294444);

		Map<Time, Configuration> adjustments = new HashMap<Time, Configuration>();
		adjustments.put(Time.FAJR, angle(20));
		adjustments.put(Time.DHUHR, minutes(2));
		adjustments.put(Time.MAGHRIB, angle(1));
		adjustments.put(Time.ISHA, angle(18));

		Map<Time, Integer> offsets = new HashMap<Time, Integer>();
		offsets.put(Time.FAJR, 2);
		offsets.put(Time.SUNRISE, -2);
		offsets.put(Time.ASR, 2);
		offsets.put(Time.MAGHRIB, 2);
		offsets.put(Time.ISHA, 2);

		commonTest(sampleFile, cal, location, adjustments, offsets);
	}

	@Test
	public void shouldBeTheSameWithJsVersion_Sample2() throws Exception {
		String sampleFile = "src/test/file/sample-02.txt";
		GregorianCalendar cal = new GregorianCalendar(2013, JULY, 24);
		cal.setTimeZone(TimeZone.getTimeZone("GMT-5")); // -5
		assertEquals(-5, cal.getTimeZone().getRawOffset() / 3600000);

		Location location = new Location(43, -80);

		Map<Time, Configuration> adjustments = new HashMap<Time, Configuration>();
		adjustments.put(Time.FAJR, angle(19));
		adjustments.put(Time.DHUHR, minutes(5));
		adjustments.put(Time.MAGHRIB, angle(2));
		adjustments.put(Time.ISHA, angle(15));

		Map<Time, Integer> offsets = new HashMap<Time, Integer>();
		offsets.put(Time.FAJR, 2);

		commonTest(sampleFile, cal, location, adjustments, offsets);
	}

	private void commonTest(String sampleFile, GregorianCalendar cal,
			Location location, Map<Time, Configuration> adjustments,
			Map<Time, Integer> offsets) throws FileNotFoundException,
			IOException {
		BufferedReader reader = new BufferedReader(new FileReader(sampleFile));
		String line;

		PrayTimes pt = new PrayTimes();
		pt.adjust(adjustments);
		pt.tuneOffset(offsets);

		int day = 1;
		while ((line = reader.readLine()) != null) {
			String[] splits = line.split(" \\| ");

			String fajr = splits[1];
			String sunrise = splits[2];
			String dhuhr = splits[3];
			String asr = splits[4];
			String maghrib = splits[5];
			String isha = splits[6];

			Map<Time, Double> times = pt.getTimes(cal, location);

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
