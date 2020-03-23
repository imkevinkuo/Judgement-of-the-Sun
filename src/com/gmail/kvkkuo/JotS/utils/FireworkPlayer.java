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

public class FireworkPlayer {
    
    public static void fire(Location l, Type t, Color c, Boolean trail) {
    	Location loc = l.clone().add(0,  -0.2, 0);
    	Firework firework = (Firework) l.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(1);
		if (trail) {
			meta.addEffect(FireworkEffect.builder().with(t).withColor(c).withTrail().build());
		}
		if (!trail) {
			meta.addEffect(FireworkEffect.builder().with(t).withColor(c).build());
		}
		firework.setFireworkMeta(meta);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JotS.getPlugin(JotS.class), new Runnable() {
            public void run() {
               	firework.detonate();
            }
        }, (2));
	}
}
