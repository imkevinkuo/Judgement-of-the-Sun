package com.gmail.kvkkuo.JotS.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.gmail.kvkkuo.JotS.JotS;

public class RespawnListener implements Listener {
	
	public JotS plugin;
	
	public RespawnListener(JotS plugin){
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