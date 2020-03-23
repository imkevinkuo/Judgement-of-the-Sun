package com.gmail.kvkkuo.JotS.bosses;

import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Rogue extends Boss {
	    
    public static void Spawn(Location l, Plugin plugin, EntityType et) {
    	LivingEntity boss = (LivingEntity) l.getWorld().spawnEntity(l, et);
    	boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    	boss.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));
    	// Global settings
    	Boss.spawnActions(boss, Boss.getRace(et) + " Rogue", plugin);
		boss.getEquipment().setItemInMainHand(roguesword);
		boss.getEquipment().setArmorContents(roguearmor);
		boss.getEquipment().setItemInMainHandDropChance(80);
		boss.getEquipment().setBootsDropChance(80);
    	// Begin abilties
    	Rogue.Fight(boss, plugin);
    }
    
    public static void Fight(LivingEntity boss, Plugin plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!boss.isDead()) {
					double d = Math.random();
					if (d < 0.4) {
						Rogue.Charge(boss, plugin);
					}
					if (d >= 0.4) {
						Rogue.Force(boss, plugin);
					}
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 120, 100);
	}

    public static void Charge(LivingEntity boss, Plugin plugin) {
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 4));
    }
    
    public static void Force(LivingEntity boss, Plugin plugin) {
    	Boolean charged = false;
		for (Entity e:boss.getNearbyEntities(4, 4, 4)) {
			if (!charged && e instanceof Player) {
    			Player p = (Player) e;
    			p.damage(2, boss);
    			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
    			p.setVelocity(p.getLocation().getDirection().multiply(-1.5).setY(0.2));
    			charged = true;
				if (Math.random() < 0.5) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							Rogue.Charge(boss, plugin);
						}
					},	(20));
				}
				else {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							Rogue.Step(boss, p, plugin);
						}
					},	(20));
				}
			}
		}
    }
    
    public static void Spray(LivingEntity boss, Plugin plugin) {
		for (int i = 0; i < 10; i ++) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					Vector v = boss.getLocation().getDirection(); //Multiply the player's direction by the power
					Random rand = new Random();
					v.add(new Vector((rand.nextDouble() - 0.5), (rand.nextDouble() - 0.5)/2, (rand.nextDouble() - 0.5))); //Add the velocity by a random number
					Arrow b = boss.launchProjectile(Arrow.class);
					b.setVelocity(v);
					b.setShooter(boss);
				}
			},	(i*3));
		}
    }
    
    public static void Bomb(LivingEntity boss, Player p, Plugin plugin) {
    	Item item = boss.getWorld().dropItem(boss.getEyeLocation(), new ItemStack(Material.QUARTZ_BLOCK));
		
    	item.setVelocity(boss.getLocation().getDirection().multiply(1));
		item.setPickupDelay(Integer.MAX_VALUE);
		 
		new BukkitRunnable() {
		@Override
			public void run() {
		    	for (Entity e : item.getNearbyEntities(1, 1, 1)) {
		    		if (!(e.equals(boss)) && !(e.equals(item))) {
		    			if (e instanceof LivingEntity) {
			    			for (Entity en : item.getNearbyEntities(5, 5, 5)) {
			    				if (!(en.equals(boss)) && en instanceof LivingEntity) {
					    			LivingEntity le = (LivingEntity) en;
					    			le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
					    			le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0));
			    				}
			    			}
			    			FireworkPlayer.fire(item.getLocation(), Type.BALL_LARGE, Color.GRAY, true);
				    		item.remove();
				    		this.cancel();
		    			}
		    		}
		    	}
		    	if (item.isOnGround()) {
		    		for (Entity en : item.getNearbyEntities(5, 5, 5)) {
	    				if (!(en.equals(boss)) && en instanceof LivingEntity) {
			    			LivingEntity le = (LivingEntity) en;
			    			le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
			    			le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0));
	    				}
	    			}
		    		FireworkPlayer.fire(item.getLocation(), Type.BALL_LARGE, Color.GRAY, true);
		    		item.remove();
		    		this.cancel();
		    	}
			}
		}.runTaskTimer(plugin, 1, 1);
    }
    
    public static void Step(LivingEntity boss, Player p, Plugin plugin) {
    	FireworkPlayer.fire(boss.getLocation(), Type.BURST, Color.BLACK, true);
		Location l = Utils.getBlockRelative(p.getLocation(), -5, 0, 0);
		while (!l.getBlock().isPassable()) {
			l.setY(l.getY() + 1);
		}
    	boss.teleport(l);
    	boss.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0));
    	if (Math.random() < 0.5) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					Rogue.Spray(boss, plugin);
				}
			},	(20));
		}
		else {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					Rogue.Bomb(boss, p, plugin);
				}
			},	(40));
		}
    }
}
