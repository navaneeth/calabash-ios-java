package calabash.java;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

class CalabashLogger {

	private static boolean shouldLog = false;
	private static Logger log = Logger.getLogger(CalabashLogger.class);

	public static void initialize(CalabashConfiguration configuration)
			throws CalabashException {
		if (configuration != null && configuration.isLoggingEnabled()) {
			try {
				String logFile = new File(configuration.getLogsDirectory(),
						"calabash-ios-java.log").getAbsolutePath();
				FileAppender fileAppender = new FileAppender(new PatternLayout(
						"%d %-5p - %m%n"), logFile);
				fileAppender.setAppend(true);
				fileAppender.activateOptions();

				BasicConfigurator.configure(fileAppender);
				shouldLog = true;
			} catch (IOException e) {
				throw new CalabashException("Can't setup logging system. "
						+ e.getMessage(), e);
			}
		}
	}

	public static void info(Object message) {
		if (shouldLog)
			log.info(message);
	}

	public static void error(Object message) {
		if (shouldLog)
			log.error(message);
	}

	public static void error(Object message, Throwable cause) {
		if (shouldLog)
			log.error(message, cause);
	}
}
