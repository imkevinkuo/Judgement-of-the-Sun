package com.gmail.kvkkuo.JotS.classes;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.RayTrace;
import com.gmail.kvkkuo.JotS.utils.Utils;
import com.gmail.kvkkuo.JotS.utils.Utils.Plane;

public class Witherknight {
	public static String[] skills = new String[]{
			// Bomb --------------------------------------------------------------------------------------------
			"Caustic Bomb", "Throw a bomb that poisons and slows enemies.",
			"Corrupting Bomb", "Throw a bomb that leaves behind a cloud of Poisonous gas.",
			"Splitting Bomb", "Throw a bomb that explodes into three more explosive TNT blocks.",
			"Shrouding Bomb", "Throw a bomb, creating a zone that Blinds enemies and grants you Speed.",
			// Wither Skulls ------------------------------------------------------------------------------------------
			"Wither Barrage", "Shoots 3 Wither Skulls. Skulls explode and can apply Wither.",
			"Wither Seeker", "Shoots a Wither Skull that locks on to the nearest target.",
			"Wither Rebirth", "Creates a zone that Withers enemies and heals you after a delay.",
			"Wither Storm", "Launches several waves of Wither Skulls in a circle around you.",
			// Darkness -------------------------------------------------------------------------------------------
			"Ghost Grip", "Slows the first enemy your crosshair is pointing at.",
			"Eldritch Eye", "Inflicts Wither and Blind on enemies in a line.",
			"Black Blast", "Inflicts Wither and Blind in a blast around the first target.",
			"Darkspark", "Fires several bursts of darkness around you, causing enemies to Wither and Levitate.",
			// Shadow -------------------------------------------------------------------------------------------
			"Shadow Switch", "Switches your location with a target within your crosshair.",
			"Shadow Trap", "Drops a trap that Poisons nearby enemies when triggered.",
			"Shadow Snare", "Drops a trap that will Slow all nearby enemies and then teleport you to them.",
			"Shadow Shroud", "Drops a trap that will blind all nearby enemies and then teleport you to them."
	};
	public static Integer cast(Player p, Integer spell, Integer cooldown, Integer upgrade, Plugin pl) {
		if (cooldown <= 0) {
			boolean c = true;
			if (spell.equals(0)) {
				if (upgrade.equals(0)) {
					Witherknight.Caustic(p, cooldown, pl);
				}
				if (upgrade.equals(1)) {
					Witherknight.Corrupting(p, cooldown, pl);
				}
				if (upgrade.equals(2)) {
					Witherknight.Splitting(p, cooldown, pl);
				}
				if (upgrade.equals(3)) {
					Witherknight.Shrouding(p, cooldown, pl);
				}
				if (c) {cooldown = 12;}
			}
			if (spell.equals(1)) {
				if (upgrade.equals(0)) {
					Witherknight.Barrage(p, cooldown, pl);
				}
				if (upgrade.equals(1)) {
					Witherknight.Homing(p, cooldown, pl);
				}
				if (upgrade.equals(2)) {
					Witherknight.Triad(p, cooldown, pl);
				}
				if (upgrade.equals(3)) {
					Witherknight.Storm(p, cooldown, pl);
				}
				if (c) {cooldown = 12;}
			}
			if (spell.equals(2)) {
				if (upgrade.equals(0)) {
					c = Witherknight.Grip(p, cooldown);
				}
				if (upgrade.equals(1)) {
					Witherknight.Eye(p, cooldown, pl);
				}
				if (upgrade.equals(2)) {
					Witherknight.Blast(p, cooldown, pl);
				}
				if (upgrade.equals(3)) {
					Witherknight.Desecrate(p, cooldown, pl);
				}
				if (c) {cooldown = 20;}
			}
			if (spell.equals(3)) {
				if (upgrade.equals(0)) {
					c = Witherknight.Switch(p, cooldown);
				}
				if (upgrade.equals(1)) {
					Witherknight.Trap(p, cooldown, pl);
				}
				if (upgrade.equals(2)) {
					Witherknight.Snare(p, cooldown, pl);
				}
				if (upgrade.equals(3)) {
					Witherknight.Shroud(p, cooldown, pl);
				}
				if (c) {cooldown = 20;}
			}
		}
		return cooldown;
	}
	public static void Caustic(Player p, Integer cooldown, Plugin pl) {
		Item item = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.SLIME_BLOCK));
		item.setVelocity(p.getLocation().getDirection().multiply(1));
		item.setPickupDelay(Integer.MAX_VALUE);

		new BukkitRunnable() {
			@Override
			public void run() {
			if (item.isOnGround()) {
				for (Entity e : item.getNearbyEntities(4, 4, 4)) {
					if (e instanceof LivingEntity && !e.equals(p)) {
						LivingEntity le = (LivingEntity) e;
						le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 80, 0));
					}
				}
				item.getWorld().playSound(item.getLocation(), Sound.ENTITY_CREEPER_HURT, 5, 1);
				FireworkPlayer.fire(item.getLocation(), Type.BALL_LARGE, Color.OLIVE, false);
				item.remove();
				this.cancel();
			}
			}
		}.runTaskTimer(pl, 1, 1);
	}
	public static void Corrupting(Player p, Integer cooldown, Plugin plugin) {
		Item item = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.MOSSY_COBBLESTONE));

		item.setVelocity(p.getLocation().getDirection().multiply(1));
		item.setPickupDelay(Integer.MAX_VALUE);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (item.isOnGround()) {
					BukkitTask bt = new BukkitRunnable() {
						@Override
						public void run() {
							for (Location loca:Utils.getCirclePoints(item.getLocation(), Plane.XZ, 5, 40)) {
								Location newloc = loca.clone();
								Location below = loca.add(0, -1, 0);
								while (below.getBlock().isPassable()) {
									newloc = newloc.add(0, -1, 0);
									below = below.add(0, -1, 0);
								}
								while (!newloc.getBlock().isPassable()) {
									newloc = newloc.add(0, 1, 0);
									below = below.add(0, 1, 0);
								}
								p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, newloc, 1);
							}
							for (Entity en : item.getNearbyEntities(5, 5, 5)) {
								if (en instanceof LivingEntity && !en.equals(p)) {
									LivingEntity le = (LivingEntity) en;
									le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
									le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 3));
								}
							}
						}
					}.runTaskTimer(plugin, 15, 20);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							bt.cancel();
						}
					}, 115);
					FireworkPlayer.fire(item.getLocation(), Type.BALL_LARGE, Color.GREEN, true);
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}
	public static void Splitting(Player p, Integer cooldown, Plugin pl) {
		Item item = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.OBSIDIAN));

		item.setVelocity(p.getLocation().getDirection().multiply(1));
		item.setPickupDelay(Integer.MAX_VALUE);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (item.isOnGround()) {
					Double x = (Math.random() - 0.5)/2;
					Double z = (Math.random() - 0.5)/2;
					Vector vi =  new Vector(x, 0.5, z);
					Vector vii =  new Vector(-x, 0.5, -z);
					Vector viii =  new Vector(-x, 0.5, z);
					for (int in = 0; in < 3; in ++) {
						ItemStack i = new ItemStack(Material.TNT);
						ItemMeta im = i.getItemMeta();
						im.setDisplayName("TNT" + in + p.getName());
						Item frag = p.getWorld().dropItem(item.getLocation(), i);
						if (in == 0) {
							frag.setVelocity(vi);
						}
						if (in == 1) {
							frag.setVelocity(vii);
						}
						if (in == 2) {
							frag.setVelocity(viii);
						}
						frag.setPickupDelay(Integer.MAX_VALUE);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
							public void run() {
								Bukkit.getWorld(frag.getWorld().getName()).createExplosion(frag.getLocation(), 0);
								for (Entity e:frag.getNearbyEntities(4, 4, 4)) {
									if (e instanceof LivingEntity && !e.equals(p)) {
										((LivingEntity) e).damage(4, p);
										e.setVelocity(e.getLocation().toVector().subtract(frag.getLocation().toVector()).normalize());
									}
								}
								frag.remove();
							}
						},	(40));
					}
					Bukkit.getWorld(item.getWorld().getName()).createExplosion(item.getLocation(), 0);
					for (Entity e:item.getNearbyEntities(4, 4, 4)) {
						if (e instanceof LivingEntity && !e.equals(p)) {
							((LivingEntity) e).damage(6, p);
							e.setVelocity(e.getLocation().toVector().subtract(item.getLocation().toVector()).normalize());
						}
					}
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(pl, 1, 1);
	}
	public static void Shrouding(Player p, Integer cooldown, Plugin plugin) {
		Item item = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.BLACK_STAINED_GLASS));

		item.setVelocity(p.getLocation().getDirection().multiply(1));
		item.setPickupDelay(Integer.MAX_VALUE);
		BlockData md = Material.OBSIDIAN.createBlockData();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (item.isOnGround()) {
					BukkitTask bt = new BukkitRunnable() {
						@Override
						public void run() {
							for (Location loca:Utils.getCirclePoints(item.getLocation(), Plane.XZ, 5)) {
								p.getWorld().spawnParticle(Particle.BLOCK_CRACK, loca, 1, md);
							}
							for (Entity en : item.getNearbyEntities(5, 5, 5)) {
								if (en instanceof LivingEntity) {
									LivingEntity le = (LivingEntity) en;
									if (en.equals(p)) {
										le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
										le.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0));
									}
									else {
										le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
										le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
									}
								}
							}
						}
					}.runTaskTimer(plugin, 15, 20);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							bt.cancel();
						}
					}, 115);
					FireworkPlayer.fire(item.getLocation(), Type.BALL_LARGE, Color.BLACK, true);
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}
	public static void Barrage(Player p, Integer cooldown, Plugin pl) {
		WitherSkull s1 = p.launchProjectile(WitherSkull.class);
		s1.setShooter(p);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
			public void run() {
				WitherSkull s2 = p.launchProjectile(WitherSkull.class);
				s2.setShooter(p);
		}},	10);
	}
	public static void Homing(Player p, Integer cooldown, Plugin pl) {
		WitherSkull s1 = p.launchProjectile(WitherSkull.class);
		s1.setShooter(p);
		Integer count = 0;
		while (count < 60) {
			count++;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
				public void run() {
					for (Entity e : s1.getNearbyEntities(10, 10, 10)) {
						if (!e.equals(p) && e instanceof LivingEntity) {
							s1.setVelocity(e.getLocation().toVector().subtract(s1.getLocation().toVector()).multiply(0.15));
						}
					}
			}},	(count*1)+20);
		}
	}
	public static void Triad(Player p, Integer cooldown, Plugin pl) {
		for (int i = 0; i < 3; i++) {
			WitherSkull s1 = p.launchProjectile(WitherSkull.class);
			s1.setShooter(p);
			s1.setVelocity(p.getLocation().getDirection().add(new Vector(Math.random() - 0.5, 0, Math.random() - 0.5).multiply(i/2)));
		}
	}
	public static void Storm(Player p, Integer cooldown, Plugin plugin) {

	}
	public static boolean Grip(Player p, Integer cooldown) {
		Entity e = Utils.getCrosshair(p);
		if (e != null) {
			if (e.getLocation().distance(p.getLocation()) < 20) {
				LivingEntity le = (LivingEntity) e;
				p.getWorld().spawnParticle(Particle.SPELL_WITCH, le.getLocation(), 20);
				le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 3));
				if (le instanceof Player) {
					((Player) le).sendMessage(p.getName() + "'s Darkness Grip slows you!");
				}
			}
			else {
				p.sendMessage("Target too far away!");
				return false;
			}
		}
		else {
			p.sendMessage("You must be looking at a target to use Darkness Grip.");
			return false;
		}
		return true;
	}
	public static void Eye(Player p, Integer cooldown, Plugin plugin) {
		World w = p.getWorld();
		Integer range = 10, count = 0;
		RayTrace eye = new RayTrace(p, 10, 3);
		for (count = 0; count < range; count++) {
			Location l = eye.next().clone();
			if (l.getBlock().getType().equals(Material.AIR)) {
				new BukkitRunnable() {
					public void run() {
						for (Entity e:w.getNearbyEntities(l, 3, 3, 3)) {
							if (!(e.equals(p) && e instanceof LivingEntity)) {
								((LivingEntity) e).damage(4, p);
								((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 4));
								((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 4));
							}
						}
						w.spawnParticle(Particle.SPELL_WITCH, l, 20, 0.2, 0.2, 0.2, 0);
					}
				}.runTaskLater(plugin, count);
			}
			else {
				break;
			}
		}
	}
	public static void Blast(Player p, Integer cooldown, Plugin plugin) {
		Integer count = 0;
		BlockIterator eye = new BlockIterator(p, 10);
		while (eye.hasNext()) {
			Location l = eye.next().getLocation();
			if (l.getBlock().getType().equals(Material.AIR)) {
				count++;
				if (count%3 == 0) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							for (Entity e:p.getWorld().getNearbyEntities(l, 3, 3, 3)) {
								if (e instanceof LivingEntity && !(e.equals(p))) {
									((LivingEntity) e).damage(3);
									((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 4));
									((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 4));
									Bukkit.getWorld(l.getWorld().getName()).createExplosion(l, 0);
									break;
								}
							}
							p.getWorld().spawnParticle(Particle.SPELL_WITCH, l, 6, 0.2, 0.2, 0.2, 0);
						}
					},	count);
				}
			}
			else {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						Bukkit.getWorld(l.getWorld().getName()).createExplosion(l, 0);
						for (Entity en:p.getWorld().getNearbyEntities(l, 4, 4 ,4)) {
							if (en instanceof LivingEntity) {
								((LivingEntity) en).damage(4, p);
							}
						}
					}},	count);
				break;
			}
		}
	}
	public static void Desecrate(Player p, Integer cooldown, Plugin pl) {
		for (Integer count = 0; count < 8; count++) {
			Integer c = count;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
				public void run() {
					p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2, 1);
					for (Location loc:Utils.getStarPoints(p.getLocation(), Plane.XZ, c, 4)) {
						p.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 10, 0.2, 0, 0.2, 1);
						for (Entity e:p.getWorld().getNearbyEntities(loc, 3, 3, 3)) {
							if (!e.equals(p)) {
								e.setVelocity(e.getLocation().getDirection().multiply(-1.2).setY(0.4));
								if (e instanceof LivingEntity) {
									if (p.getHealth() < p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
										p.setHealth(p.getHealth() + 1);
									}
									((LivingEntity) e).damage(4, p);
									((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 60, 3));
								}
							}
						}
					}
				}
			},	count*8);
		}
	}
	public static boolean Switch(Player p, Integer cooldown) {
		Entity e = Utils.getCrosshair(p);
		if (e != null) {
			if (e.getLocation().distance(p.getLocation()) < 20) {
				FireworkPlayer.fire(p.getLocation(), Type.CREEPER, Color.SILVER, false);
				Location el = e.getLocation();
				Location pl = p.getLocation();
				p.teleport(el);
				e.teleport(pl);
				p.getWorld().spawnParticle(Particle.PORTAL, el, 20, 0, 0, 0, 1);
				p.getWorld().spawnParticle(Particle.PORTAL, pl, 20, 0, 0, 0, 1);
				if (e instanceof Player) {
					((Player) e).sendMessage("You have been Shadow Switched by " + p.getName() + "!");
				}
			}
			else {
				p.sendMessage("Target too far away!");
				return false;
			}
		}
		else {
			p.sendMessage("You must be looking at an enemy to Shadow Switch.");
			return false;
		}
		return true;
	}
	public static void Trap(Player p, Integer cooldown, Plugin pl) {
		Item trap = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.NETHER_QUARTZ_ORE));
		trap.setPickupDelay(Integer.MAX_VALUE);

		int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
			public void run() {
				Integer activated = 0;
				if (!trap.isDead()) {
					for (Entity e : trap.getNearbyEntities(4, 4, 4)) {
						if (e instanceof LivingEntity && !e.equals(p)) {
							activated = 1;
							((LivingEntity) e).damage(5, p);
							((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 3));
							if (e instanceof Player) {
								((Player) e).sendMessage(p.getName() + "'s nearby Shadow Trap was triggered!");
							}
						}
					}
					if (activated == 1) {
						p.sendMessage("Your Shadow Trap has been triggered!");
						trap.getWorld().playSound(trap.getLocation(), Sound.ENTITY_CREEPER_HURT, 5, 1);
						FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false);
						trap.remove();
					}
				}
			}
		}, 5, 5);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
			public void run() {
				if (!trap.isDead()) {
					Bukkit.getScheduler().cancelTask(taskID);
					FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false);
					trap.remove();
				}
			}
		}, (600));
	}
	public static void Snare(Player p, Integer cooldown, Plugin pl) {
		Item trap = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.NETHER_QUARTZ_ORE));
		trap.setPickupDelay(Integer.MAX_VALUE);

		int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
			public void run() {
				Integer activated = 0;
				if (!trap.isDead()) {
					for (Entity e : trap.getNearbyEntities(4, 4, 4)) {
						if (e instanceof LivingEntity && !e.equals(p)) {
							if (e instanceof Player) {
								((Player) e).sendMessage(p.getName() + "'s nearby Shadow Snare was triggered!");
							}
							((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 3));
							activated = 1;
						}
					}
					if (activated == 1) {
						p.sendMessage("Your Shadow Snare has been triggered!");
						trap.getWorld().playSound(trap.getLocation(), Sound.ENTITY_CREEPER_HURT, 5, 1);
						FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false);
						Location target = trap.getLocation();
						trap.remove();
						
						target.add(0, 12, 0);
						for (int i = 0; i < 5; i++) {
							int in = i;
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
								public void run() {
									FireworkPlayer.fire(target, Type.BURST, Color.GRAY, false);
									if (in == 4) {
										p.teleport(target);
									}
									else {
										target.subtract(0, 3, 0);
									}
								}
							},	(40 + i*5));
						}
					}
				}
			}
		},	5, 5);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
			public void run() {
				if (!trap.isDead()) {
					Bukkit.getScheduler().cancelTask(taskID);
					FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false);
					trap.remove();
				}
			}
		},	(600));
	}
	public static void Shroud(Player p, Integer cooldown, Plugin pl) {
		Item trap = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.NETHER_QUARTZ_ORE));
		trap.setPickupDelay(Integer.MAX_VALUE);

		int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
			public void run() {
				Integer activated = 0;
				if (!trap.isDead()) {
					for (Entity e : trap.getNearbyEntities(4, 4, 4)) {
						if (e instanceof LivingEntity && !e.equals(p)) {
							if (e instanceof Player) {
								((Player) e).sendMessage(p.getName() + "'s Shadow Shroud begins to gather darkness...");
							}
							((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 50, 1));
							((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 3));
							activated = 1;
						}
					}
					if (activated == 1) {
						p.sendMessage("Your Shadow Shroud has been triggered!");
						trap.getWorld().playSound(trap.getLocation(), Sound.ENTITY_CREEPER_HURT, 5, 1);
						FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false);
						Location target = trap.getLocation();
						trap.remove();
						
						target.add(0, 12, 0);
						for (int i = 0; i < 5; i++) {
							int in = i;
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
								public void run() {
									FireworkPlayer.fire(target, Type.BURST, Color.GRAY, false);
									if (in == 4) {
										p.teleport(target);
									}
									else {
										target.subtract(0, 3, 0);
									}
								}
							},	(40 + i*5));
						}
					}
				}
			}
		},	5, 5);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
			public void run() {
				if (!trap.isDead()) {
					Bukkit.getScheduler().cancelTask(taskID);
					FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false);
					trap.remove();
				}
			}
		},	(600));
	}
}
