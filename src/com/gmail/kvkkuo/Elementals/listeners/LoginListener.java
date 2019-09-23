package com.gmail.kvkkuo.Elementals.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.gmail.kvkkuo.Elementals.Elementals;
import com.gmail.kvkkuo.Elementals.utils.IconMenu;
 
public class LoginListener implements Listener {
	
	public Elementals plugin;	
	
	public LoginListener(Elementals plugin){
		this.plugin = plugin;
	}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player p = event.getPlayer();
    	final UUID id = p.getUniqueId();
    	
        plugin.loadConfig(p);
        
        // Upgrade Menu
        IconMenu menu = new IconMenu(p.getName() + "'s Skills", 9, new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                Player p = event.getPlayer();
                event.setWillClose(true);
                UUID id = p.getUniqueId();
                // Shard to be equipped
                ItemStack i = p.getInventory().getItemInMainHand();
                Integer s = event.getPosition();
				//Unequip all
				if (s == 8) {
					Integer[] ups = plugin.upgrades.get(id);
					for (int x = 0; x < 4; x++) {
						if (ups[x] > 0) {
							p.getInventory().addItem(UpgradeMenu.getShard(ups[x]));
						}
						ups[x] = 0;
					}
					plugin.upgrades.put(id, ups);
					p.sendMessage("All shards have been refunded!");
				}
                // If it isn't Unequip Shard
                else {
					Integer[] ups = plugin.upgrades.get(id);
					if (ups[s/2] > 0) {
						p.getInventory().addItem(UpgradeMenu.getShard(ups[s/2]));
						p.sendMessage("Your " + UpgradeMenu.getShard(ups[s/2]).getItemMeta().getDisplayName() + " has been refunded to you.");
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
						p.getInventory().remove(i);
						if (i.getAmount() > 1) {
							ItemStack usedstack = new ItemStack(i.getType(), i.getAmount() - 1);
							usedstack.setItemMeta(i.getItemMeta());
							p.getInventory().addItem(usedstack);
						}
						p.sendMessage("You equip the " + in + ".");
						ups[s/2] = u;
						plugin.upgrades.put(id, ups);
						UpgradeMenu.DisplaySkills(plugin.skillmenu.get(id), plugin.factions.get(id), ups).open(p);
					}

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