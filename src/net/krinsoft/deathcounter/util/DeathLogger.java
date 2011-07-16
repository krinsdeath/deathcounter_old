package net.krinsoft.deathcounter.util;

import java.util.logging.Logger;

import net.krinsoft.deathcounter.DeathCounter;

public class DeathLogger {
	public DeathCounter plugin;
	public Logger log = Logger.getLogger("Minecraft");
	public String LOG_PREFIX;

	public DeathLogger(DeathCounter instance) {
		plugin = instance;
		LOG_PREFIX = "";
	}
	
	public void info(String message) {
		if (plugin.config.getInt("settings.log_verbosity", 1) > 0) {
			if (plugin.config.getString("settings.log") != null) {
				LOG_PREFIX = plugin.config.getString("settings.log");
			}
			message = LOG_PREFIX + message;
			message = message.replaceAll("<fullname>", plugin.description.getFullName());
			message = message.replaceAll("<shortname>", plugin.description.getName());
			message = message.replaceAll("<version>", plugin.description.getVersion());
			message = message.replaceAll("<author>", plugin.description.getAuthors().get(0));
			log.info(message);
		}
	}
	
	public void warn(String message) {
		if (plugin.config.getString("settings.log") != null) {
			LOG_PREFIX = plugin.config.getString("settings.log");
		}
		message = LOG_PREFIX + message;
		message = message.replaceAll("<fullname>", plugin.description.getFullName());
		message = message.replaceAll("<shortname>", plugin.description.getName());
		message = message.replaceAll("<version>", plugin.description.getVersion());
		message = message.replaceAll("<author>", plugin.description.getAuthors().get(0));
		log.warning(message);
	}

}
