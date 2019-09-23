package com.gmail.kvkkuo.Elementals.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.kvkkuo.Elementals.Elementals;

public class UpgradeListener implements Listener {
	
	public Elementals plugin;	
	
	public UpgradeListener(Elementals plugin) {
		this.plugin = plugin;
	}
	
    @EventHandler
    public void useShard(PlayerInteractEvent event) {
    	if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
    		Player p = event.getPlayer();
    		UUID id = p.getUniqueId();
    		ItemStack i = p.getInventory().getItemInMainHand();
			// Upgrade Shards
			if (i.hasItemMeta() && i.getItemMeta().getDisplayName() != null) {
				if (i.getItemMeta().getDisplayName().endsWith("Shard")) {
					Integer faction = plugin.factions.get(id);
		    		Integer[] ups = plugin.upgrades.get(id);
					for (int x = 0; x < 4; x++) {
						if (ups[x].equals(null)) {
							ups[x] = 0;
						}
					}
					plugin.upgrades.put(id, ups);
					UpgradeMenu.DisplaySkills(plugin.skillmenu.get(id), faction, ups).open(p);
				}
			}
    	}
	}
}
