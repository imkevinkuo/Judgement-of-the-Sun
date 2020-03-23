package com.gmail.kvkkuo.JotS.commands;

import com.gmail.kvkkuo.JotS.JotS;
import com.gmail.kvkkuo.JotS.bosses.*;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BossCommand implements CommandExecutor {
 
	public JotS plugin;
	
	public BossCommand(JotS plugin){
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("boss")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("k") || args[0].equalsIgnoreCase("killall")) {
					for (World w : Bukkit.getWorlds()) {
						for (LivingEntity le : w.getLivingEntities()) {
							if (le.hasMetadata("boss") || le.hasMetadata("minion")) {
								le.damage(1337);
							}
						}
					}
				} else if (sender instanceof Player) {
					final Player p = (Player) sender;
					boolean failure = true;
					if (args.length == 3) {
						String arg1 = args[0].toLowerCase();
						String arg2 = args[1].toLowerCase();
						String arg3 = args[2].toLowerCase();
						EntityType et = EntityType.PIG_ZOMBIE;
						if (arg3.equals("s")) {
							et = EntityType.SKELETON;
						}
						if (arg3.equals("z")) {
							et = EntityType.ZOMBIE;
						}
						if (arg1.equals("spawn")) {
							if (Arrays.asList(new String[] {"marauder", "shaman", "rogue", "warlock", "hunter", "ninja"}).contains(arg2)) {
								failure = false;
								if (arg2.equals("marauder")) {
									Marauder.Spawn(Utils.getBlockRelative(p.getLocation(), 6, 0, 0), plugin, et);
								}
								if (arg2.equals("shaman")) {
									Shaman.Spawn(Utils.getBlockRelative(p.getLocation(), 6, 0, 0), plugin, et);
								}
								if (arg2.equals("rogue")) {
									Rogue.Spawn(Utils.getBlockRelative(p.getLocation(), 6, 0, 0), plugin, et);
								}
								if (arg2.equals("warlock")) {
									Warlock.Spawn(Utils.getBlockRelative(p.getLocation(), 6, 0, 0), plugin, et);
								}
								if (arg2.equals("hunter")) {
									Hunter.Spawn(Utils.getBlockRelative(p.getLocation(), 6, 0, 0), plugin, et);
								}
								if (arg2.equals("ninja")) {
									Ninja.Spawn(Utils.getBlockRelative(p.getLocation(), 6, 0, 0), plugin, et);
								}
							}
						}
					}
					if (failure) {
						p.sendMessage("/boss spawn <shell> <race>");
						p.sendMessage("Types: marauder, shaman, rogue, warlock, hunter, ninja");
						p.sendMessage("Races: s, z, p");
					}
				} else {
					sender.sendMessage("Cannot execute from console!");
					return false;
				}
				return true;
			}
		}
		return false; 
	}
}