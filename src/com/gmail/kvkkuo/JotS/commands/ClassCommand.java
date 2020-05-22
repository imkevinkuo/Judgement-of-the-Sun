package com.gmail.kvkkuo.JotS.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.kvkkuo.JotS.JotS;
import com.gmail.kvkkuo.JotS.listeners.UpgradeMenu;

public class ClassCommand implements CommandExecutor {
 
	public JotS plugin;
	
	public ClassCommand(JotS plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("class")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Integer pFaction = plugin.factions.get(p.getUniqueId());
				if (args.length == 0) {
					String faction = "Civilian";
					if (pFaction >= 0) {
						faction = JotS.classNames[pFaction];
					}
					String uclass = Character.toUpperCase(faction.charAt(0)) + faction.substring(1);
					p.sendMessage(ChatColor.LIGHT_PURPLE + "You are a " + ChatColor.GOLD + uclass + ".");
				}
				if (args.length == 1) {
					String arg1 = args[0].toLowerCase();
					if (arg1.equals("dummy")) {
						LivingEntity z = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
						z.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 8));
					}
					if (arg1.equals("set")) {
						String s = "Specify a class ID.";
						for (int i = 0; i < JotS.classNames.length; i++) {
							s += "\n" + i + ": " +  JotS.classNames[i];
						}
						p.sendMessage(s);
					}
					if (arg1.equals("rc")) {
						p.sendMessage("Cooldowns reset!");
						plugin.cooldowns.put(p.getUniqueId(), new Integer[]{1,1,1,1});
					}
					if (arg1.equals("upgrade")) {
						ItemStack i = new ItemStack(Material.DIAMOND);
						i.setAmount(4);
						ItemMeta im = i.getItemMeta();
						im.setDisplayName("Alpha Shard");
						i.setItemMeta(im);
						p.getInventory().addItem(i);
						im.setDisplayName("Gamma Shard");
						i.setItemMeta(im);
						p.getInventory().addItem(i);
						im.setDisplayName("Delta Shard");
						i.setItemMeta(im);
						p.getInventory().addItem(i);
					}
					if (arg1.equals("skills")) {
						UpgradeMenu.DisplaySkills(plugin.skillmenu.get(p.getUniqueId()),
								plugin.factions.get(p.getUniqueId()), plugin.upgrades.get(p.getUniqueId())).open(p);
					}
				}
				if (args.length == 2) {
					String arg1 = args[0].toLowerCase();
					if (args[1].length() > 1) {
						if (!Character.isDigit(args[1].charAt(0))) {
							p.sendMessage("Include digit.");
							return false;
						}
					}
					Integer arg2 = Integer.parseInt(args[1]);
					if (arg1.equals("set")) {
						if (arg2 >= 0 && arg2 < 6) {
							p.sendMessage("You are now a " + JotS.classNames[arg2] + ".");
							plugin.factions.put(p.getUniqueId(), arg2);
							p.getInventory().addItem(new ItemStack(JotS.selectors[arg2], 1));
						}
					}
					if (arg1.equals("upgrade")) {
						p.sendMessage("Usage: /class upgrade <spell> <level>");
						p.sendMessage("<spell> must be: 0-3 or 4 (all).");
						p.sendMessage("<level> must be 0-3.");
					}
				}
				if (args.length == 3) {
					String arg1 = args[0].toLowerCase();
					Integer arg2 = 0;
					Integer arg3 = 0;
					try {
						arg2 = Integer.parseInt(args[1]);
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
					try {
						arg3 = Integer.parseInt(args[2]);
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
					if (arg1.equals("upgrade")) {
						if (arg2 >= 0 && arg2 <= 4 && arg3 >= 0 && arg3 <= 3) {
							Integer[] ups = plugin.upgrades.get(p.getUniqueId());
							if (arg2 == 4) {
								ups = new Integer[]{arg3, arg3, arg3, arg3};
							}
							else {
								ups[arg2] = arg3;
							}
							plugin.upgrades.put(p.getUniqueId(), ups);
							p.sendMessage("spellslot " + arg2 + " upgraded to type " + arg3);
						}
						else {
							p.sendMessage("Usage: /class upgrade <spell> <level>");
							p.sendMessage("<spell> must be: 0-3 or 4 (all).");
							p.sendMessage("<level> must be 0-3.");
						}
					}
				}
		    }
			else {
				sender.sendMessage("Cannot execute from console!");
				return false;
		    }
			return true;
		}
		return false; 
	}
}