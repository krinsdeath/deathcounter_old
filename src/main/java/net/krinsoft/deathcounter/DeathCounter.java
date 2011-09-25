package net.krinsoft.deathcounter;

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.krinsoft.deathcounter.listeners.CommandListener;
import net.krinsoft.deathcounter.listeners.EntityEventListener;
import net.krinsoft.deathcounter.listeners.PlayerEventListener;
import net.krinsoft.deathcounter.listeners.ServerEventListener;
import net.krinsoft.deathcounter.types.DeathPlayer;
import net.krinsoft.deathcounter.types.Leaderboards;
import net.krinsoft.deathcounter.util.DeathLogger;
import net.krinsoft.deathcounter.util.DeathTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class DeathCounter extends JavaPlugin {

    public final DeathLogger log = new DeathLogger(this);
    public Configuration config;
    public Configuration users;
    public Leaderboards leaders;
    public Settings settings;
    private final EntityEventListener eListener = new EntityEventListener(this);
    private final PlayerEventListener pListener = new PlayerEventListener(this);
    private final CommandListener cListener = new CommandListener(this);
    private final ServerEventListener sListener = new ServerEventListener(this);
    public HashMap<String, DeathPlayer> players = new HashMap();
    protected PluginDescriptionFile description;
    protected PluginManager manager;
    protected Plugin plugin;
    private double allpayVersion = 3.0D;
    public AllPay banker;
    public GenericBank bank = null;
    public List<String> monsters = new ArrayList();
    public boolean perm;
    public boolean eco;

    @Override
    public void onEnable() {
        this.plugin = this;
        this.description = getDescription();
        this.manager = getServer().getPluginManager();

        if (!validateAllPay()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.banker = new AllPay(this, "[DeathCounter] ");

        this.settings = new Settings(this);
        this.leaders = new Leaderboards(this);

        this.manager.registerEvent(Event.Type.PLUGIN_ENABLE, this.sListener, Event.Priority.Monitor, this);
        this.manager.registerEvent(Event.Type.PLUGIN_DISABLE, this.sListener, Event.Priority.Monitor, this);

        this.manager.registerEvent(Event.Type.ENTITY_DEATH, this.eListener, Event.Priority.Normal, this);

        this.manager.registerEvent(Event.Type.PLAYER_LOGIN, this.pListener, Event.Priority.Normal, this);
        this.manager.registerEvent(Event.Type.PLAYER_QUIT, this.pListener, Event.Priority.Normal, this);

        getCommand("deathcount").setExecutor(this.cListener);

        long n = this.config.getInt("settings.save_interval", 30) * 60000;
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new DeathTimer(this), n, n);

        initMonsters();

        this.log.info(info("fullname") + " by " + info("authors") + " enabled!");
    }

    @Override
    public void onDisable() {
        if (this.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("yaml")) {
            this.users.save();
        } else if (this.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("sqlite")) {
            for (Player player : getServer().getOnlinePlayers()) {
                if (this.players.get(player.getName()) != null) {
                    ((DeathPlayer) this.players.get(player.getName())).save();
                }
            }
        } else if (this.config.getString("settings.storage.type", "yaml").equalsIgnoreCase("mysql"));
        getServer().getScheduler().cancelTasks(this);
        this.log.info("disabled");
    }

    public boolean validateAllPay() {
        try {
            this.banker = new AllPay(this, "Verify");
            if (this.banker.getVersion() >= this.allpayVersion) {
                return true;
            }
            this.log.warn("A plugin with an outdated version of AllPay initialized before DeathCounter.");
            this.log.warn("DeathCounter needs AllPay v" + this.allpayVersion + " or higher, but AllPay v" + this.banker.getVersion() + " was detected.");
            this.log.warn("Nag the authors of the following plugins: ");
            this.log.warn(AllPay.pluginsThatUseUs.toString());
            return false;
        } catch (Throwable t) {
            this.log.warn("A plugin with an outdated version of AllPay initialized before DeathCounter.");
            this.log.warn("DeathCounter needs AllPay v" + this.allpayVersion + " or higher, but AllPay v" + this.banker.getVersion() + " was detected.");
            this.log.warn("Nag the authors of the following plugins: ");
            this.log.warn(AllPay.pluginsThatUseUs.toString());
        }
        return false;
    }

    public String info(String field) {
        if (field.equalsIgnoreCase("name")) {
            return this.description.getName();
        }
        if (field.equalsIgnoreCase("fullname")) {
            return this.description.getFullName();
        }
        if (field.equalsIgnoreCase("version")) {
            return this.description.getVersion();
        }
        if (field.equalsIgnoreCase("authors")) {
            return this.description.getAuthors().toString();
        }
        return this.description.getName();
    }

    private void initMonsters() {
        this.monsters.add("pig");
        this.monsters.add("cow");
        this.monsters.add("sheep");
        this.monsters.add("squid");
        this.monsters.add("chicken");
        this.monsters.add("skeleton");
        this.monsters.add("zombie");
        this.monsters.add("ghast");
        this.monsters.add("wolf");
        this.monsters.add("creeper");
        this.monsters.add("slime");
        this.monsters.add("pigzombie");
        this.monsters.add("spider");
        this.monsters.add("player");

        this.monsters.add("silverfish");
        this.monsters.add("enderman");
        this.monsters.add("cavespider");
    }

    public void setBank(GenericBank bank) {
        this.bank = bank;
    }

    public GenericBank getBank() {
        return this.bank;
    }

    public AllPay getBanker() {
        return this.banker;
    }
}
