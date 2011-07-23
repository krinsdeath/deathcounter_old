package net.krinsoft.deathcounter.util;

import java.util.logging.Logger;

import net.krinsoft.deathcounter.DeathCounter;

public class DeathLogger {
	private DeathCounter plugin;
	private Logger log;
	private String LOG_PREFIX;

	public DeathLogger(DeathCounter instance) {
		plugin = instance;
		LOG_PREFIX = "";
	}
	
	public void info(String message) {
		if (plugin.config.getInt("settings.log_verbosity", 1) > 0) {
			LOG_PREFIX = plugin.config.getString("settings.log", "[" + plugin.description.getFullName() + "] ");
			message = LOG_PREFIX + message;
			message = logParser(message);
			log.info(message);
		}
	}
	
	public void warn(String message) {
		LOG_PREFIX = plugin.config.getString("settings.log", "[" + plugin.description.getFullName() + "] ");
		message = LOG_PREFIX + message;
		message = logParser(message);
		log.warning(message);
	}

	private String logParser(String message) {
		message = message.replaceAll("<fullname>", plugin.description.getFullName());
		message = message.replaceAll("<shortname>", plugin.description.getName());
		message = message.replaceAll("<version>", plugin.description.getVersion());
		message = message.replaceAll("<author>", plugin.description.getAuthors().get(0));
		return message;
	}

	public void setLogger(Logger logger) {
		this.log = logger;
	}

}
