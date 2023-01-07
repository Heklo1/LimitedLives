package me.heklo.limitedlives;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LimitedLives extends JavaPlugin implements Listener {

    private static final String LIVES_FILE = "lives.yml";
    private int DEFAULT_LIVES;
    private File livesFile;
    private YamlConfiguration livesConfig;

    @Override
    public void onEnable() {
        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("limitedlives").setExecutor(new CommandHandler(this));

        if(!getDataFolder().exists())
        {
            getDataFolder().mkdirs();
        }
        this.saveDefaultConfig();
        DEFAULT_LIVES = getConfig().getInt("default-lives", 5);
        livesFile = new File(getDataFolder(), LIVES_FILE);
        if (!livesFile.exists()) {
            try
            {
                livesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        livesConfig = YamlConfiguration.loadConfiguration(livesFile);
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
        {
            new MyPlaceholders(this).register();
        }
        else
        {
            Bukkit.getLogger().info("No PlaceholderAPI found.");
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
        saveData();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        // Handle losing lives
        Player p = event.getEntity();
        int lives = getLives(p);
        if(!p.hasPermission("limitedlives.bypass"))
        {
            lives -= 1;
            String loseLifeMsg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("lose-life-msg", ""));
            loseLifeMsg = loseLifeMsg.replaceAll("%lives%", String.valueOf(lives));
            if(loseLifeMsg.length() > 0)
                p.sendMessage(loseLifeMsg);
        }

        setLives(p, lives);
        if (lives <= 0)
        {
                String punishCmd = getConfig().getString("punish-command", "");
                if(punishCmd.length() > 0)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCmd);
            setLives(p, DEFAULT_LIVES);
        }

        // Handle gaining lives
        if(p.getKiller() != null)
        {
            Player killer = p.getKiller();
            String gainLifeMsg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("gain-life-msg", ""));
            gainLifeMsg = gainLifeMsg
                    .replaceAll("%lives%", String.valueOf(lives))
                    .replaceAll("%victim%", p.getName());
            if(gainLifeMsg.length() > 0)
                killer.sendMessage(gainLifeMsg);
            setLives(killer, getLives(killer) + 1);
        }
    }

    public void setLives(OfflinePlayer player, int lives)
    {
        if(lives == DEFAULT_LIVES)
            livesConfig.set(player.getUniqueId().toString(), null);
        else
            livesConfig.set(player.getUniqueId().toString(), lives);
    }

    public int getLives(OfflinePlayer player)
    {
        return livesConfig.getInt(player.getUniqueId().toString(), DEFAULT_LIVES);
    }

    public void saveData()
    {
        try
        {
            livesConfig.save(livesFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
