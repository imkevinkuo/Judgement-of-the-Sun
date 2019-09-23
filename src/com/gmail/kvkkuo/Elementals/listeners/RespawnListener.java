package com.gmail.kvkkuo.Elementals.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.gmail.kvkkuo.Elementals.Elementals;

public class RespawnListener implements Listener {
	
	public Elementals plugin;	
	
	public RespawnListener(Elementals plugin){
		this.plugin = plugin;
	}
	
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	Player p = event.getPlayer();
    	p.sendMessage("When you die, all Minions, Marks, Auras, etc. are removed.");
    	p.sendMessage("Shards and stat upgrades are retained.");
    	plugin.leaveServer(p);
    }
}