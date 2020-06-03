package com.gmail.kvkkuo.JotS.utils;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import com.gmail.kvkkuo.JotS.JotS;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class FireworkPlayer {
    
    public static void fire(Location l, Type t, Color c, boolean trail, boolean fade, boolean flicker) {
    	Location loc = l.clone().add(0,  -0.2, 0);
    	Firework firework = (Firework) l.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();

        FireworkEffect.Builder b = FireworkEffect.builder().with(t).withColor(c);
		if (trail) {
			b.withTrail();
		}
		if (fade) {
			b.withFade();
		}
		if (flicker) {
			b.withFlicker();
		}
		meta.addEffect(b.build());
		meta.setPower(0);

		firework.setSilent(true);
		firework.setFireworkMeta(meta);
		new BukkitRunnable() {
			public void run() {
				firework.detonate();
			}
		}.runTaskLater(JotS.getPlugin(JotS.class), 1);
	}
}
