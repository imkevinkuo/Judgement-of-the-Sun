package com.gmail.kvkkuo.JotS;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.gmail.kvkkuo.JotS.bosses.Boss;
import com.gmail.kvkkuo.JotS.commands.BossCommand;
import com.gmail.kvkkuo.JotS.commands.PlayCommand;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.kvkkuo.JotS.commands.ClassCommand;
import com.gmail.kvkkuo.JotS.listeners.*;
import com.gmail.kvkkuo.JotS.utils.Cooldown;
import com.gmail.kvkkuo.JotS.utils.IconMenu;

public class JotS extends JavaPlugin {

	public static Material[] selectors = {
			Material.IRON_SWORD,
			Material.BLAZE_POWDER,
			Material.FEATHER,
			Material.CLAY_BALL,
			Material.BONE,
			Material.GLOWSTONE_DUST
	};

	public static String[] classNames = {
			"Duelist", "Raider", "Assassin", "Guardian", "Witherknight", "Paladin"
	};
	
	BukkitTask Cooldown;
	public BossDamageListener bossDamage = new BossDamageListener(this);
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
    	//
		getCommand("play").setExecutor(new PlayCommand(this));
    	// Bosses
		getServer().getPluginManager().registerEvents(bossDamage, this);
		getCommand("boss").setExecutor(new BossCommand(this));
		Boss.CreateItems();
		// JotS
    	Cooldown = new Cooldown(this).runTaskTimer(this, 40, 20);
    	getCommand("class").setExecutor(new ClassCommand(this));
    	getServer().getPluginManager().registerEvents(login, this);
    	getServer().getPluginManager().registerEvents(cast, this);
    	getServer().getPluginManager().registerEvents(damage, this);
    	getServer().getPluginManager().registerEvents(respawn, this);
    	getServer().getPluginManager().registerEvents(upgrade, this);
		skillmenu = new HashMap<>();
    	storymenu = new HashMap<>();
    	factions = new HashMap<>();
    	spell = new HashMap<>();
		cooldowns = new HashMap<>();
		upgrades = new HashMap<>();
		File jotsDir = new File("plugins/JotS");
		if(!jotsDir.exists()) {
			jotsDir.mkdir();
			File jotspDir = new File("plugins/JotS/players");
			jotspDir.mkdir();
		}
	}
 
    @Override
    public void onDisable() {
    	for (Player p : Bukkit.getOnlinePlayers()) {
    		leaveServer(p);
    	    p.kickPlayer(ChatColor.RED+"Restarting");
    	}
    }

	public void leaveServer(Player p) {
		UUID id = p.getUniqueId();
		saveConfig(p);
		Utils.clearMetadata(p, this);
		if (skillmenu.get(id) != null) {
			skillmenu.get(id).destroy();
		}
	}

	public void createConfig(Player p) {
		File file = new File(String.format("plugins/JotS/players/%s.yml", p.getName()));
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (!file.exists()) {
			try {
				file.createNewFile();
				System.out.println("File Created: "+ p.getName() + ".yml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			config.set("player.class", -1);
			config.set("player.spell", 0);
			Integer[] zeros = {0, 0, 0, 0};
			config.set("player.upgrades", Arrays.asList(zeros));
			config.set("player.cooldowns", Arrays.asList(zeros));
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Load again after setting default values
		loadConfig(p);
	}

	public void saveConfig(Player p) {
		UUID id = p.getUniqueId();
		File file = new File(String.format("plugins/JotS/players/%s.yml", p.getName()));
		if (!file.exists()) {
			createConfig(p);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("player.class", factions.get(id));
		config.set("player.spell", spell.get(id));
		config.set("player.upgrades", upgrades.get(id));
		config.set("player.cooldowns", cooldowns.get(id));
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadConfig(Player p) {
		UUID id = p.getUniqueId();
		File file = new File(String.format("plugins/JotS/players/%s.yml", p.getName()));
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()){
			createConfig(p);
		}
		else {
			factions.put(id, config.getInt("player.class"));
			spell.put(id, config.getInt("player.spell"));
			Integer[] upgradesArr = config.getIntegerList("player.upgrades").toArray(new Integer[4]);
			Integer[] cooldownsArr = config.getIntegerList("player.cooldowns").toArray(new Integer[4]);
			upgrades.put(id, upgradesArr);
			cooldowns.put(id, cooldownsArr);
		}
	}
}