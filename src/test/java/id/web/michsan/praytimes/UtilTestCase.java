package id.web.michsan.praytimes;

import static org.junit.Assert.assertEquals;
import id.web.michsan.praytimes.Util;
import id.web.michsan.praytimes.Util.DayTime;

import org.junit.Test;

/**
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 */
public class UtilTestCase {
	@Test
	public void testToDayTime() {
		DayTime dt = null;

		dt = Util.toDayTime(2 + (30 / 60d) + (15 / 3600d), true);
		assertEquals(2, dt.getHours());
		assertEquals(30, dt.getMinutes());
		assertEquals(0, dt.getSeconds());

		// should round
		dt = Util.toDayTime(2 + (30 / 60d) + (35 / 3600d), true);
		assertEquals(2, dt.getHours());
		assertEquals(31, dt.getMinutes());
		assertEquals(0, dt.getSeconds());

		// should round
		dt = Util.toDayTime(2 + (30 / 60d) + (35 / 3600d), false);
		assertEquals(2, dt.getHours());
		assertEquals(30, dt.getMinutes());
		assertEquals(35, dt.getSeconds());
	}

	@Test
	public void testToTime12() {
		assertEquals("02:30", Util.toTime12(2 + (30 / 60d) + (15 / 3600d), true));
		assertEquals("02:30 am", Util.toTime12(2 + (30 / 60d) + (15 / 3600d), false));
		assertEquals("02:30 pm", Util.toTime12(12 + 2 + (30 / 60d) + (15 / 3600d), false));
	}
}
