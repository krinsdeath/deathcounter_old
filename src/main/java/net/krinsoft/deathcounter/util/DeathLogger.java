package net.krinsoft.deathcounter.util;

import java.util.logging.Logger;

import net.krinsoft.deathcounter.DeathCounter;

public class DeathLogger {
	private DeathCounter plugin;
	private static final Logger LOGGER = Logger.getLogger("DeathCounter");
	private String LOG_PREFIX;

	public DeathLogger(DeathCounter instance) {
		plugin = instance;
		LOG_PREFIX = "";
	}
	
	public void info(String message) {
		if (plugin.config.getInt("settings.log_verbosity", 1) > 0) {
			LOG_PREFIX = plugin.config.getString("settings.log", "[" + plugin.info("fullname") + "] ");
			message = LOG_PREFIX + message;
			message = logParser(message);
			LOGGER.info(message);
		}
	}
	
	public void warn(String message) {
		LOG_PREFIX = plugin.config.getString("settings.log", "[" + plugin.info("fullname") + "] ");
		message = LOG_PREFIX + message;
		message = logParser(message);
		LOGGER.warning(message);
	}

	private String logParser(String message) {
		message = message.replaceAll("<fullname>", plugin.info("fullname"));
		message = message.replaceAll("<shortname>", plugin.info("name"));
		message = message.replaceAll("<version>", plugin.info("version"));
		message = message.replaceAll("<author>", plugin.info("author"));
		return message;
	}

}
