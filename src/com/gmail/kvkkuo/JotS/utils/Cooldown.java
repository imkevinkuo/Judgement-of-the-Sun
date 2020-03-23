package com.gmail.kvkkuo.JotS.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.kvkkuo.JotS.JotS;
import com.gmail.kvkkuo.JotS.listeners.CastListener;

public class Cooldown extends BukkitRunnable {
	public JotS plugin;
	public Cooldown(JotS plugin){
		this.plugin = plugin;
	}
	 
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasMetadata("illusion") || p.hasMetadata("deception")) {
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, p.getEyeLocation(), 12, 0.5, 0.5, 0.5, 0);
			}
			if (p.hasMetadata("rage") || p.hasMetadata("fury")) {
				p.getWorld().spawnParticle(Particle.FLAME, p.getEyeLocation(), 12, 0.5, 0.5, 0.5, 0);
			}
			UUID id = p.getUniqueId();
			Integer spell = plugin.spell.get(id);
			Integer[] cds = plugin.cooldowns.get(id);
			boolean update = false;
			for (int x = 0; x < 4; x++) {
				if (cds[x] > 0) {
					update = update || (x == spell);
					cds[x] = cds[x] - 1;
				}
			}
			plugin.cooldowns.put(id, cds);
			CastListener.updateCycler(p, plugin.factions.get(id), cds[spell], plugin.upgrades.get(id)[spell], spell);
			if (update) {
				p.updateInventory();
			}
		}
	}
}