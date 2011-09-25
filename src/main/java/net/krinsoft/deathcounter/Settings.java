package net.krinsoft.deathcounter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import net.krinsoft.deathcounter.util.DeathLogger;
import org.bukkit.util.config.Configuration;

public final class Settings {

    private final DeathCounter plugin;
    private final DeathLogger log;
    private File dataFolder;

    public Settings(DeathCounter instance) {
        this.plugin = instance;
        this.log = this.plugin.log;
        setup();
    }

    public void setup() {
        this.dataFolder = this.plugin.getDataFolder();
        this.dataFolder.mkdirs();

        File config = makeDefaults(new File(this.dataFolder, "config.yml"));

        this.plugin.config = new Configuration(config);
        this.plugin.config.load();

        if (this.plugin.config.getProperty("economy.enderman") == null) {
            this.plugin.config.setProperty("economy.enderman", Double.valueOf(0.0D));
            this.plugin.config.setProperty("economy.silverfish", Double.valueOf(0.0D));
            this.plugin.config.setProperty("economy.cavespider", Double.valueOf(0.0D));
            this.plugin.config.save();
        }

        if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
            this.plugin.users = new Configuration(new File(this.dataFolder, this.plugin.config.getString("settings.storage.info.filename", "users.yml")));
            this.plugin.users.load();
        } else if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
            makeDefaultSqliteDatabase();
            if (!this.plugin.config.getBoolean("settings.1185update", false)) {
                this.plugin.config.setProperty("settings.1185update", Boolean.valueOf(true));
                this.plugin.config.save();
                updateDefaultSqliteDatabase();
            }
            if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
                this.plugin.users = new Configuration(new File(this.dataFolder, this.plugin.config.getString("settings.storage.info.filename", "users.yml")));
                this.plugin.users.load();
            }
        } else if (!this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql"));
        this.plugin.eco = this.plugin.config.getBoolean("settings.economy", false);
    }

    private void makeDefaultSqliteDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
            String initQuery = "CREATE TABLE IF NOT EXISTS `users` (id INTEGER, name VARCHAR(32), cow INTEGER DEFAULT 0, pig INTEGER DEFAULT 0, sheep INTEGER DEFAULT 0, chicken INTEGER DEFAULT 0, squid INTEGER DEFAULT 0, zombie INTEGER DEFAULT 0, skeleton INTEGER DEFAULT 0, ghast INTEGER DEFAULT 0, creeper INTEGER DEFAULT 0, slime INTEGER DEFAULT 0, spider INTEGER DEFAULT 0, wolf INTEGER DEFAULT 0, pigzombie INTEGER DEFAULT 0, player INTEGER DEFAULT 0, PRIMARY KEY(id DESC));";

            Statement state = conn.createStatement();
            state.executeUpdate(initQuery);
            state.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            this.log.warn("SQLite JDBC Driver not found. Defaulting to YAML.");
            this.log.warn("Error: " + e);
            this.plugin.config.setProperty("settings.storage.type", "yaml");
        } catch (SQLException e) {
            this.log.warn("Error: " + e);
        }
    }

    private void updateDefaultSqliteDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
            String query = "ALTER TABLE `users` ADD `enderman` INTEGER DEFAULT 0; ALTER TABLE `users` ADD `silverfish` INTEGER DEFAULT 0; ALTER TABLE `users` ADD `cavespider` INTEGER DEFAULT 0;";

            Statement state = conn.createStatement();
            state.executeUpdate(query);
            state.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            this.log.warn("SQLite JDBC Driver not found. Defaulting to YAML.");
            this.log.warn("Error: " + e);
            this.plugin.config.setProperty("settings.storage.type", "yaml");
        } catch (SQLException e) {
            this.log.warn("Error: " + e);
        }
    }

    private File makeDefaults(File file) {
        if (!file.exists()) {
            new File(file.getParent()).mkdirs();
            InputStream in = DeathCounter.class.getResourceAsStream("/defaults/" + file.getName());
            if (in != null) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    int length = 0;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                } catch (IOException e) {
                    this.log.warn("Error creating " + file.getName());
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        this.log.warn("Error closing stream.");
                        e.printStackTrace();
                    }
                }
            }
        }
        return file;
    }
}
