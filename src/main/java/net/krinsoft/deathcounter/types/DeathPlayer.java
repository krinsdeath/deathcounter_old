package net.krinsoft.deathcounter.types;

import com.fernferret.allpay.GenericBank;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import net.krinsoft.deathcounter.DeathCounter;
import net.krinsoft.deathcounter.interfaces.IDatabase;
import net.krinsoft.deathcounter.util.DeathLogger;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class DeathPlayer
        implements IDatabase {

    public DeathCounter plugin;
    public DeathLogger log;
    public String name;
    public int cow;
    public int pig;
    public int squid;
    public int chicken;
    public int sheep;
    public int skeleton;
    public int zombie;
    public int creeper;
    public int spider;
    public int wolf;
    public int ghast;
    public int slime;
    public int pigzombie;
    public int enderman;
    public int silverfish;
    public int cavespider;
    public int player;
    public int total;

    public DeathPlayer(DeathCounter instance, String name) {
        this.plugin = instance;
        this.log = this.plugin.log;
        this.name = name;
        load();
    }

    public void add(Player ply, String mob) {
        if (mob.equals("pig")) {
            this.pig += 1;
            update("pig");
        } else if (mob.equals("cow")) {
            this.cow += 1;
            update("cow");
        } else if (mob.equals("sheep")) {
            this.sheep += 1;
            update("sheep");
        } else if (mob.equals("squid")) {
            this.squid += 1;
            update("squid");
        } else if (mob.equals("chicken")) {
            this.chicken += 1;
            update("chicken");
        } else if (mob.equals("skeleton")) {
            this.skeleton += 1;
            update("skeleton");
        } else if (mob.equals("zombie")) {
            this.zombie += 1;
            update("zombie");
        } else if (mob.equals("wolf")) {
            this.wolf += 1;
            update("wolf");
        } else if (mob.equals("spider")) {
            this.spider += 1;
            update("spider");
        } else if (mob.equals("ghast")) {
            this.ghast += 1;
            update("ghast");
        } else if (mob.equals("creeper")) {
            this.creeper += 1;
            update("creeper");
        } else if (mob.equals("slime")) {
            this.slime += 1;
            update("slime");
        } else if (mob.equals("pigzombie")) {
            this.pigzombie += 1;
            update("pigzombie");
        } else if (mob.equals("player")) {
            this.player += 1;
            update("player");
        } else if (mob.equals("enderman")) {
            this.enderman += 1;
            update("enderman");
        } else if (mob.equals("silverfish")) {
            this.silverfish += 1;
            update("silverfish");
        } else if (mob.equals("cavespider")) {
            this.cavespider += 1;
            update("cavespider");
        }
        this.total += 1;
        update("total");
        if (this.plugin.eco) {
            GenericBank bank = this.plugin.getBank();
            bank.give(ply, this.plugin.config.getDouble("economy." + mob, 0.0D), -1);
        }
        this.plugin.players.put(ply.getName(), this);
    }

    public final void load() {
        if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
            loadYaml();
        } else if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
            loadSqlite();
        } else if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql")) {
            loadMysql();
        }
    }

    private void loadYaml() {
        this.pig = this.plugin.users.getInt(this.name + ".pig", 0);
        this.cow = this.plugin.users.getInt(this.name + ".cow", 0);
        this.sheep = this.plugin.users.getInt(this.name + ".sheep", 0);
        this.chicken = this.plugin.users.getInt(this.name + ".chicken", 0);
        this.squid = this.plugin.users.getInt(this.name + ".squid", 0);

        this.skeleton = this.plugin.users.getInt(this.name + ".skeleton", 0);
        this.zombie = this.plugin.users.getInt(this.name + ".zombie", 0);
        this.ghast = this.plugin.users.getInt(this.name + ".ghast", 0);
        this.wolf = this.plugin.users.getInt(this.name + ".wolf", 0);
        this.creeper = this.plugin.users.getInt(this.name + ".creeper", 0);
        this.slime = this.plugin.users.getInt(this.name + ".slime", 0);
        this.pigzombie = this.plugin.users.getInt(this.name + ".pigzombie", 0);
        this.spider = this.plugin.users.getInt(this.name + ".spider", 0);

        this.player = this.plugin.users.getInt(this.name + ".player", 0);
        this.total = this.plugin.users.getInt(this.name + ".total", 0);
        this.log.info("player " + this.name + " loaded");
    }

    private void loadSqlite() {
        try {
            String query = "SELECT * FROM `users` WHERE `name` = '" + this.name + "';";
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(query);
            rs.next();
            if (rs.getRow() == 0) {
                insert();
                rs = state.executeQuery(query);
                rs.next();
                if (rs.getRow() == 0) {
                    this.log.warn("Something is wrong.");
                    this.log.warn("User creation aborted.");
                    return;
                }
            }
            this.pig = rs.getInt("pig");
            this.cow = rs.getInt("cow");
            this.sheep = rs.getInt("sheep");
            this.chicken = rs.getInt("chicken");
            this.squid = rs.getInt("squid");
            this.skeleton = rs.getInt("skeleton");
            this.zombie = rs.getInt("zombie");
            this.ghast = rs.getInt("ghast");
            this.wolf = rs.getInt("wolf");
            this.creeper = rs.getInt("creeper");
            this.slime = rs.getInt("slime");
            this.pigzombie = rs.getInt("pigzombie");
            this.spider = rs.getInt("spider");
            this.player = rs.getInt("player");
            state.close();
            conn.close();
            this.log.info("player " + this.name + " loaded");
        } catch (ClassNotFoundException e) {
            this.log.warn("SQLite JDBC Driver not found. Defaulting to YAML.");
            this.log.warn("Error: " + e);
            this.plugin.config.setProperty("settings.storage.type", "yaml");
        } catch (SQLException e) {
            this.log.warn("Error: " + e);
        }
    }

    private void loadMysql() {
    }

    public void save() {
        if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
            saveYaml();
        } else if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
            saveSqlite();
        } else if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql")) {
            saveMysql();
        }
    }

    private void saveYaml() {
        this.plugin.users.setProperty(this.name + ".pig", Integer.valueOf(this.pig));
        this.plugin.users.setProperty(this.name + ".cow", Integer.valueOf(this.cow));
        this.plugin.users.setProperty(this.name + ".sheep", Integer.valueOf(this.sheep));
        this.plugin.users.setProperty(this.name + ".squid", Integer.valueOf(this.squid));
        this.plugin.users.setProperty(this.name + ".chicken", Integer.valueOf(this.chicken));
        this.plugin.users.setProperty(this.name + ".skeleton", Integer.valueOf(this.skeleton));
        this.plugin.users.setProperty(this.name + ".zombie", Integer.valueOf(this.zombie));
        this.plugin.users.setProperty(this.name + ".ghast", Integer.valueOf(this.ghast));
        this.plugin.users.setProperty(this.name + ".wolf", Integer.valueOf(this.wolf));
        this.plugin.users.setProperty(this.name + ".creeper", Integer.valueOf(this.creeper));
        this.plugin.users.setProperty(this.name + ".slime", Integer.valueOf(this.slime));
        this.plugin.users.setProperty(this.name + ".pigzombie", Integer.valueOf(this.pigzombie));
        this.plugin.users.setProperty(this.name + ".spider", Integer.valueOf(this.spider));
        this.plugin.users.setProperty(this.name + ".player", Integer.valueOf(this.player));
        this.plugin.users.setProperty(this.name + ".total", Integer.valueOf(this.total));

        this.plugin.users.setProperty(this.name + ".enderman", Integer.valueOf(this.enderman));
        this.plugin.users.setProperty(this.name + ".silverfish", Integer.valueOf(this.silverfish));
        this.plugin.users.setProperty(this.name + ".cavespider", Integer.valueOf(this.cavespider));
        this.plugin.users.save();
        if (this.plugin.config.getInt("settings.log_verbosity", 1) >= 3) {
            this.log.info("player " + this.name + " saved successfully");
        }
    }

    private void saveSqlite() {
        try {
            String query = "UPDATE `users` SET`pig` = " + this.pig + "," + "`cow` = " + this.cow + "," + "`sheep` = " + this.sheep + "," + "`chicken` = " + this.chicken + "," + "`squid` = " + this.squid + "," + "`skeleton` = " + this.skeleton + "," + "`zombie` = " + this.zombie + "," + "`ghast` = " + this.ghast + "," + "`wolf` = " + this.wolf + "," + "`creeper` = " + this.creeper + "," + "`slime` = " + this.slime + "," + "`pigzombie` = " + this.pigzombie + "," + "`spider` = " + this.spider + "," + "`player` = " + this.player + " " + "WHERE `name` = '" + this.name + "';";

            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
            Statement state = conn.createStatement();
            int rs = state.executeUpdate(query);
            if ((rs == 1)
                    && (this.plugin.config.getInt("settings.log_verbosity", 1) >= 3)) {
                this.log.info("player " + this.name + " saved successfully");
            }

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

    private void saveMysql() {
    }

    public void insert() {
        if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
            insertYaml();
        } else if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
            insertSqlite();
        } else if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql")) {
            insertMysql();
        }
    }

    private void insertYaml() {
        this.plugin.users.setProperty(this.name + ".pig", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".cow", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".sheep", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".squid", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".chicken", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".skeleton", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".zombie", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".ghast", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".wolf", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".creeper", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".slime", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".pigzombie", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".spider", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".player", Integer.valueOf(0));
        this.plugin.users.setProperty(this.name + ".total", Integer.valueOf(0));
        if (this.plugin.config.getInt("settings.log_verbosity", 1) >= 2) {
            this.log.info("player " + this.name + " created");
        }
    }

    private void insertSqlite() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
            Statement state = conn.createStatement();
            int rs = state.executeUpdate("INSERT INTO `users` (`name`) VALUES('" + this.name + "');");
            if ((rs == 0)
                    && (this.plugin.config.getInt("settings.log_verbosity", 1) >= 2)) {
                this.log.info("player " + this.name + " created");
            }

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

    private void insertMysql() {
    }

    public void update(String mob) {
        if (this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
            updateYaml(mob);
        } else if (!this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
            if (!this.plugin.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql"));
        }
    }

    public void updateYaml(String mob) {
        int update = 0;
        if (mob.equals("pig")) {
            update = this.pig;
        } else if (mob.equals("cow")) {
            update = this.cow;
        } else if (mob.equals("sheep")) {
            update = this.sheep;
        } else if (mob.equals("squid")) {
            update = this.squid;
        } else if (mob.equals("chicken")) {
            update = this.chicken;
        } else if (mob.equals("skeleton")) {
            update = this.skeleton;
        } else if (mob.equals("zombie")) {
            update = this.zombie;
        } else if (mob.equals("wolf")) {
            update = this.wolf;
        } else if (mob.equals("spider")) {
            update = this.spider;
        } else if (mob.equals("ghast")) {
            update = this.ghast;
        } else if (mob.equals("creeper")) {
            update = this.creeper;
        } else if (mob.equals("slime")) {
            update = this.slime;
        } else if (mob.equals("pigzombie")) {
            update = this.pigzombie;
        } else if (mob.equals("enderman")) {
            update = this.enderman;
        } else if (mob.equals("silverfish")) {
            update = this.silverfish;
        } else if (mob.equals("cavespider")) {
            update = this.cavespider;
        } else if (mob.equals("player")) {
            update = this.player;
        } else if (mob.equals("total")) {
            update = this.total;
        }
        this.plugin.users.setProperty(this.name + "." + mob, Integer.valueOf(update));
        if (this.plugin.config.getInt("settings.log_verbosity", 1) >= 3) {
            this.log.info(this.name + "'s kill count for " + mob + " has been updated to " + update);
        }
    }

    /** @deprecated */
    public void updateSqlite(String mob) {
        int update = 0;
        if (mob.equals("pig")) {
            update = this.pig;
        } else if (mob.equals("cow")) {
            update = this.cow;
        } else if (mob.equals("sheep")) {
            update = this.sheep;
        } else if (mob.equals("squid")) {
            update = this.squid;
        } else if (mob.equals("chicken")) {
            update = this.chicken;
        } else if (mob.equals("skeleton")) {
            update = this.skeleton;
        } else if (mob.equals("zombie")) {
            update = this.zombie;
        } else if (mob.equals("wolf")) {
            update = this.wolf;
        } else if (mob.equals("spider")) {
            update = this.spider;
        } else if (mob.equals("ghast")) {
            update = this.ghast;
        } else if (mob.equals("creeper")) {
            update = this.creeper;
        } else if (mob.equals("slime")) {
            update = this.slime;
        } else if (mob.equals("pigzombie")) {
            update = this.pigzombie;
        } else if (mob.equals("player")) {
            update = this.player;
        }
        try {
            String query = "UPDATE `users` SET `" + mob + "` = " + update + " WHERE `name` = '" + this.name + "';";
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/DeathCounter/users.db");
            Statement state = conn.createStatement();
            int rs = state.executeUpdate(query);
            state.close();
            conn.close();
            if ((this.plugin.config.getInt("settings.log_verbosity", 1) >= 3) && (rs == 0)) {
                this.log.info(this.name + "'s kill count for " + mob + " has been updated to " + update);
            }
        } catch (ClassNotFoundException e) {
            this.log.warn("SQLite JDBC Driver not found. Defaulting to YAML.");
            this.log.warn("Error: " + e);
            this.plugin.config.setProperty("settings.storage.type", "yaml");
        } catch (SQLException e) {
            this.log.warn("Error: " + e);
        }
    }

    /** @deprecated */
    public void updateMysql(String mob) {
    }

    @Override
    public String toString() {
        StringBuilder to = new StringBuilder("DeathPlayer{");
        to.append("name=").append(this.name).append(",");
        to.append("total=").append(this.total).append("}@").append(hashCode());
        return to.toString();
    }

    @Override
    public int hashCode() {
        int hash = 19;
        hash = 51 * hash + this.total;
        hash = 51 * hash + (this.name == null ? 0 : this.name.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object aThat) {
        if (this == aThat) {
            return true;
        }
        if ((aThat == null) || (aThat.getClass() != getClass())) {
            return false;
        }
        DeathPlayer that = (DeathPlayer) aThat;
        return (that.hashCode() == hashCode()) && (that.toString().equals(toString()));
    }
}
