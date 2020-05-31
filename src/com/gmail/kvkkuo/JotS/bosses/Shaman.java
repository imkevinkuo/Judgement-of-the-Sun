package com.gmail.kvkkuo.JotS.bosses;

import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Shaman extends Boss {
    
    public static void Spawn(Location l, Plugin plugin, EntityType et) {
    	LivingEntity boss = (LivingEntity) l.getWorld().spawnEntity(l, et);
    	// Health, Name, Potion
    	boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
    	boss.setHealth(200);
    	// Global settings
    	Boss.spawnActions(boss, Boss.getRace(et) + " Shaman", plugin);
    	// Set Equipment
    	boss.getEquipment().setArmorContents(shamanarmor);
    	boss.getEquipment().setItemInMainHand(shamanwand);
    	// Begin abilties
    	Shaman.Fight(boss, plugin);
    }
    // Abilities:
	// Bolt: Magic single target
	// Spray: Poison cone?
	// Inferno: Fire AoE
	// Mist: Ice something lol
    public static void Fight(LivingEntity zk, Plugin plugin) {
    	// Pull Ability
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!zk.isDead()) {
					double d = Math.random();
					if (d < 0.1) {
						Shaman.Bolt(zk);
					}
					if (d >= 0.1 && d < 0.5) {
						Shaman.Force(zk, plugin);
					}
					if (d >= 0.5 && d < 0.6) {
						Shaman.Spray(zk, plugin);
					}
					if (d >= 0.6) {
						Shaman.Mist(zk, plugin);
					}
				}
				else {
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 80, 100);
    }

	// Chance to activate when hurt
	public static void Retreat(LivingEntity boss, Plugin plugin) {
		boss.setFallDistance(-20);
		Vector vec = boss.getLocation().getDirection().multiply(-1).setY(0.3);
		boss.setVelocity(vec);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				// some ability
			}
		},	(10));
	}

	// Damages a single target
    public static void Bolt(LivingEntity zk) {
		Player p = Utils.getNearestPlayer(zk.getLocation(), 12);
    	if (p != null) {
			p.damage(4, zk);
			FireworkPlayer.fire(p.getLocation(), Type.BURST, Color.WHITE, false, false, false);
		}
    }
    
    public static void Force(LivingEntity boss, Plugin plugin) {
		for (Entity e:boss.getNearbyEntities(6, 6, 6)) {
			if (e instanceof Player) {
    			Player p = (Player) e;
    			p.damage(2, boss);
    			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
    			p.setVelocity(p.getLocation().getDirection().multiply(-1.5).setY(0.2));
				if (Math.random() < 0.4) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							Shaman.Bolt(boss);
						}
					},	(40));
				}
				else {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							Shaman.Spray(boss, plugin);
						}
					},	(40));
				}
			}
		}
    }
    
    public static void Spray(LivingEntity zk, Plugin plugin) {
		for (Entity e:zk.getNearbyEntities(20, 20, 20)) {
			if (e instanceof Player) {
    			Player p = (Player) e;
    			for (int i = 0; i < 6; i ++) {
    				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    					public void run() {
    						Vector v = zk.getLocation().getDirection(); //Multiply the player's direction by the power
    						Random rand = new Random();
    						v.add(new Vector((rand.nextDouble() - 0.5), (rand.nextDouble() - 0.5)/2, (rand.nextDouble() - 0.5))); //Add the velocity by a random number
    						p.getWorld().playEffect(p.getLocation(), Effect.BLAZE_SHOOT, 0);
    						SmallFireball b = zk.launchProjectile(SmallFireball.class);
    						b.setVelocity(v);
    						b.setShooter(zk);
    					}
    				},	(i*3));
    			}
				break;
			}
		}
    }
    public static void Mist(LivingEntity zk, Plugin plugin) {
		for (Entity e:zk.getNearbyEntities(20, 20, 20)) {
			if (e instanceof Player) {
    			for (int i = 0; i < 6; i ++) {
    				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    					public void run() {
    						Random rand = new Random();
    						Vector v = new Vector((rand.nextDouble() - 0.5), (rand.nextDouble() - 0.5), (rand.nextDouble() - 0.5)).multiply(0.3); //Add the velocity by a random number
    						Location loc = zk.getEyeLocation().toVector().add(zk.getLocation().getDirection().multiply(1)).toLocation(zk.getWorld(), zk.getLocation().getYaw(), zk.getLocation().getPitch());
    						ThrownPotion poison = zk.getWorld().spawn(loc, ThrownPotion.class);
    						ItemStack i = new ItemStack(Material.SPLASH_POTION);
    						poison.setItem(i);
    						poison.setShooter(zk);
    						poison.setVelocity(v);
    					}
    				},	(i*1));
    			}
				break;
			}
		}
    }
}
