package com.gmail.kvkkuo.Elementals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.kvkkuo.Elementals.commands.ClassCommand;
import com.gmail.kvkkuo.Elementals.listeners.*;
import com.gmail.kvkkuo.Elementals.utils.Cooldown;
import com.gmail.kvkkuo.Elementals.utils.IconMenu;
import com.gmail.kvkkuo.Elementals.utils.Utils;

public class Elementals extends JavaPlugin {
	
	BukkitTask Cooldown;
	public DamageListener damage = new DamageListener(this);
	public LoginListener login = new LoginListener(this);
	public CastListener cast = new CastListener(this);
	public RespawnListener respawn = new RespawnListener(this);
	public UpgradeListener upgrade = new UpgradeListener(this);
	public HashMap<UUID, IconMenu> skillmenu;
	public HashMap<UUID, IconMenu> storymenu;
	// Class ID
	public HashMap<UUID, Integer> factions;
	// This is the currently cycled spell. <Player UUID, Cycled Spell>
	public HashMap<UUID, Integer> spell;
	// Stores all 4 spell cooldowns and upgrades in an array
	public HashMap<UUID, Integer[]> cooldowns;
	public HashMap<UUID, Integer[]> upgrades;
    @Override
    public void onEnable() {
    	Cooldown = new Cooldown(this).runTaskTimer(this, 40, 20);
    	getCommand("class").setExecutor(new ClassCommand(this));
    	getServer().getPluginManager().registerEvents(login, this);
    	getServer().getPluginManager().registerEvents(cast, this);
    	getServer().getPluginManager().registerEvents(damage, this);
    	getServer().getPluginManager().registerEvents(respawn, this);
    	getServer().getPluginManager().registerEvents(upgrade, this);
		skillmenu = new HashMap<UUID, IconMenu>();
    	storymenu = new HashMap<UUID, IconMenu>();
    	factions = new HashMap<UUID, Integer>();
    	spell = new HashMap<UUID, Integer>();
		cooldowns = new HashMap<UUID, Integer[]>();
		upgrades = new HashMap<UUID, Integer[]>();
    }
 
    @Override
    public void onDisable() {
    	for (Player p : Bukkit.getOnlinePlayers()) {
    		saveConfig(p);
    	    p.kickPlayer(ChatColor.RED+"Restarting");
    	}
    }
    
    public void leaveServer(Player p) {
    	UUID id = p.getUniqueId();
    	saveConfig(p);
    	Utils.clearMetadata(p, this);
		if (this.skillmenu.get(id) != null) {
			this.skillmenu.get(id).destroy();
		}
    }
    
    public void createConfig(Player p) {
    	// create configuration file for player
        final File file = new File("plugins"+File.separator+"Elementals"+File.separator+"players"+File.separator+p.getName()+".yml");
        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        if(!file.exists()){
        	// new config
            System.out.println("File Created: "+ p.getName() + ".yml");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            config.set("player.class", "None");
            config.set("player.spell", 1);
            config.set("player.spell1.upgrade", 0);
            config.set("player.spell2.upgrade", 0);
            config.set("player.spell3.upgrade", 0);
            config.set("player.spell4.upgrade", 0);
            config.set("player.spell1.cooldown", 0);
            config.set("player.spell2.cooldown", 0);
            config.set("player.spell3.cooldown", 0);
            config.set("player.spell4.cooldown", 0);
        }
        try {
	        config.save(file);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
        loadConfig(p);
    }
    
    public void saveConfig(Player p) {
    	UUID id = p.getUniqueId();
    	// create configuration file for player
        File file = new File("plugins"+File.separator+"Elementals"+File.separator+"players"+File.separator+p.getName()+".yml");
        if (!file.exists()) {
        	createConfig(p);
        }
        else {
	    	// Save config
        	FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	        config.set("player.class", this.factions.get(id));
	        config.set("player.spell", this.spell.get(id));
			Integer[] ups = upgrades.get(id);
			Integer[] cds = cooldowns.get(id);
			config.set("player.spell1.upgrade", ups[0]);
	        config.set("player.spell2.upgrade", ups[1]);
	        config.set("player.spell3.upgrade", ups[2]);
	        config.set("player.spell4.upgrade", ups[3]);
	        config.set("player.spell1.cooldown", cds[0]);
	        config.set("player.spell2.cooldown", cds[1]);
	        config.set("player.spell3.cooldown", cds[2]);
	        config.set("player.spell4.cooldown", cds[3]);
	        try {
		        config.save(file);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
        }
    }
    
    public void loadConfig(Player p) {
    	UUID id = p.getUniqueId();
    	// create configuration file for player
        File file = new File("plugins"+File.separator+"Elementals"+File.separator+"players"+File.separator+p.getName()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if(!file.exists()){
            createConfig(p);
        }
        else {
	    	this.factions.put(id, config.getInt("player.class"));
	    	this.spell.put(id, config.getInt("player.spell"));
			this.upgrades.put(id, new Integer[]{config.getInt("player.spell1.upgrade"), config.getInt("player.spell2.upgrade"), config.getInt("player.spell3.upgrade"), config.getInt("player.spell4.upgrade")});
			this.cooldowns.put(id, new Integer[]{config.getInt("player.spell1.cooldown"), config.getInt("player.spell2.cooldown"), config.getInt("player.spell3.cooldown"), config.getInt("player.spell4.cooldown")});
        }
    }
}