package com.gmail.kvkkuo.JotS.listeners;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.gmail.kvkkuo.JotS.JotS;
import com.gmail.kvkkuo.JotS.utils.IconMenu;
 
public class LoginListener implements Listener {
	
	public JotS plugin;
	
	public LoginListener(JotS plugin){
		this.plugin = plugin;
	}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player p = event.getPlayer();
    	UUID id = p.getUniqueId();
    	
        plugin.loadConfig(p);
        
        // Upgrade Menu
        IconMenu menu = new IconMenu(p.getName() + "'s Skills", 9, event1 -> {
			Player p1 = event1.getPlayer();
			event1.setWillClose(true);
			UUID id1 = p1.getUniqueId();
			// Shard to be equipped
			ItemStack i = p1.getInventory().getItemInMainHand();
			int s = event1.getPosition();
			//Unequip all
			if (s == 8) {
				Integer[] ups = plugin.upgrades.get(id1);
				for (int x = 0; x < 4; x++) {
					if (ups[x] > 0) {
						p1.getInventory().addItem(UpgradeMenu.getShard(ups[x]));
					}
					ups[x] = 0;
				}
				plugin.upgrades.put(id1, ups);
				p1.sendMessage("All shards have been refunded!");
			}
			// If it isn't Unequip Shard
			else {
				Integer[] ups = plugin.upgrades.get(id1);
				if (ups[s/2] > 0) {
					p1.getInventory().addItem(UpgradeMenu.getShard(ups[s/2]));
					p1.sendMessage("Your " + UpgradeMenu.getShard(ups[s/2]).getItemMeta().getDisplayName() + " has been refunded to you.");
					ups[s/2] = 0;
				}
				String in = i.getItemMeta().getDisplayName();
				if (in.endsWith("Shard")) {
					Integer u = 0;
					if (in.endsWith("Alpha Shard")) {
						u = 1;
					}
					if (in.endsWith("Gamma Shard")) {
						u = 2;
					}
					if (in.endsWith("Delta Shard")) {
						u = 3;
					}
					p1.getInventory().remove(i);
					if (i.getAmount() > 1) {
						ItemStack usedstack = new ItemStack(i.getType(), i.getAmount() - 1);
						usedstack.setItemMeta(i.getItemMeta());
						p1.getInventory().addItem(usedstack);
					}
					p1.sendMessage("You equip the " + in + ".");
					ups[s/2] = u;
					plugin.upgrades.put(id1, ups);
					UpgradeMenu.DisplaySkills(plugin.skillmenu.get(id1), plugin.factions.get(id1), ups).open(p1);
				}
			}
		}, plugin);
        plugin.skillmenu.put(id, menu);
    }
    
    @EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
    	plugin.leaveServer(event.getPlayer());
	}
    
    @EventHandler
	public void onPlayerKicked(PlayerKickEvent event) {
		plugin.leaveServer(event.getPlayer());
	}
}