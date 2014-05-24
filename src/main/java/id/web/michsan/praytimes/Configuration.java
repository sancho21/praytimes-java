package id.web.michsan.praytimes;

/**
 * Defines a configuration
 *
 * @author <a href="mailto:ichsan@gmail.com">Muhammad Ichsan</a>
 *
 *
 */
public class Configuration {
	public static final int TYPE_ANGLE = 1;
	public static final int TYPE_MINUTE = 2;

	private final double value;
	private final int type;

	private Configuration(double value, int type) {
		this.value = value;
		this.type = type;
	}

	public double getValue() {
		return value;
	}

	public int getType() {
		return type;
	}

	public static Configuration angle(double value) {
		return new Configuration(value, TYPE_ANGLE);
	}

	public static Configuration minutes(double value) {
		return new Configuration(value, TYPE_MINUTE);
	}
}
