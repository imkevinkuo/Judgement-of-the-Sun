package com.gmail.kvkkuo.JotS.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.RayTrace;
import com.gmail.kvkkuo.JotS.utils.Utils;
import com.gmail.kvkkuo.JotS.utils.Utils.Plane;

public class Paladin {
	public static String[] skills = new String[]{
		// Buff --------------------------------------------------------------------------------------------------
		"Haste", "Gain a brief burst of Speed. Paladin buffs last for 6 seconds.",
		"Augment", "Gain Strength and brief Slowness.",
		"Recovery", "Grants Regeneration and brief Blindness.",
		"Redemption", "Upon death, you will revive and throw back all enemies.",
		// Light -------------------------------------------------------------------------------------------------------
		"Smite", "Rain down a bolt of light on a single target.",
		"Judgement", "Throw all nearby airborne objects to the ground.",
		"Arclight", "Fires a bolt of lightning that chains to nearby enemies.",
		"Soulflare", "Conjure an aura of light that explodes after a short delay.",
		// Ice ---------------------------------------------------------------------------------------------------
		"Ice Spray", "Shoot two snowball barrages that slow down enemies.",
		"Freezing Water", "Shoot a line of ice that briefly freezes enemies.",
		"Suspension", "Immobilizes enemies in a sphere of water, dealing damage over time.",
		"Glacial Guard", "Conjure a shield of ice that absorbs melee, explosive, and fire damage.",
		// Fire ---------------------------------------------------------------------------------------------------
		"Holy Fire", "Shoots two fireball barrages that burn enemies.",
		"Purifying Fire", "Self-cleanse all debuffs and ignite everyone around you.",
		"Inferno", "Conjure a dome of flame that forces away those who touch it.",
		"Divine Fire", "Conjure a shield of fire that burns attackers."
	};
	
	private static BlockData icedata = Material.ICE.createBlockData();
	private static BlockData firedata = Material.FIRE.createBlockData();
	public static HashMap<UUID, Stack<Item>> guards = new HashMap<UUID, Stack<Item>>();
	public static HashMap<UUID, Stack<Item>> fires = new HashMap<UUID, Stack<Item>>();

	public static Integer cast(Player p, Integer spell, Integer cooldown, Integer upgrade, Plugin pl) {
		if (cooldown <= 0) {
			boolean c = true;
			if (spell.equals(0)) {
				if (upgrade.equals(0)) {
					Paladin.Haste(p);
				}
				if (upgrade.equals(1)) {
					Paladin.Augment(p);
				}
				if (upgrade.equals(2)) {
					Paladin.Recovery(p);
				}
				if (upgrade.equals(3)) {
					Paladin.Redemption(p, pl);
				}
				if (c) {cooldown = 20;}
			}
			if (spell.equals(1)) {
				if (upgrade.equals(0)) {
					c = Paladin.Smite(p, pl);
				}
				if (upgrade.equals(1)) {
					Paladin.Judgement(p, pl);
				}
				if (upgrade.equals(2)) {
					Paladin.Arclight(p, pl);
				}
				if (upgrade.equals(3)) {
					Paladin.Soulflare(p, pl);
				}
				if (c) {cooldown = 20;}
			}
			if (spell.equals(2)) {
				if (upgrade.equals(0)) {
					Paladin.Ice(p, pl);
				}
				if (upgrade.equals(1)) {
					Paladin.Freezing(p, pl);
				}
				if (upgrade.equals(2)) {
					c = Paladin.Suspension(p, pl);
				}
				if (upgrade.equals(3)) {
					Paladin.Guard(p, pl);
				}
				if (c) {cooldown = 20;}
			}
			if (spell.equals(3)) {
				if (upgrade.equals(0)) {
					Paladin.Fire(p, pl);
				}
				if (upgrade.equals(1)) {
					Paladin.Purifying(p, pl);
				}
				if (upgrade.equals(2)) {
					Paladin.Inferno(p, pl);
				}
				if (upgrade.equals(3)) {
					Paladin.Divine(p, pl);
				}
				if (c) {cooldown = 20;}
			}
		}
		return cooldown;
	}
	
	public static boolean Smite(Player p, Plugin plugin) {
		LivingEntity target = Utils.getCrosshair(p);
		if (target != null && target.getLocation().distance(p.getLocation()) < 10) {
			Location end = target.getLocation();
			Location start = p.getLocation().add(
					target.getLocation().subtract(p.getLocation()).toVector().multiply(0.5)
					).add(0, 12, 0);
			int range = (int) start.distance(end);
			RayTrace ray = new RayTrace(start, end.clone().subtract(start).toVector(), range, 1);
			for (int count = 0; count <= range; count++) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if (ray.hasNext()) {
							p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, ray.next(), 1, 0, 0, 0, 0.1);
						}
						else {
							FireworkPlayer.fire(end, Type.BURST, Color.WHITE, false);
							Utils.applyNearby(end, p, 3, 3, 3, (LivingEntity le) -> {
								Utils.magicDamage(p, le, 4, plugin);
							});
						}
					}
				}.runTaskLater(plugin, count);
			}
			return true;
		}
		else { /* material = Air */
			p.sendMessage("You must be looking at a target to cast Smite.");
		}
		return false;
	}
	public static void Judgement(Player p, Plugin plugin) {
		World w = p.getWorld();
		Utils.applyNearby(p.getLocation(), p, 8, 8, 8, (LivingEntity le) -> {
			if (!le.isOnGround()) {
				w.strikeLightningEffect(le.getLocation());
				w.createExplosion(le.getLocation(), 0);
				Utils.magicDamage(p, le, 2, plugin);
				BukkitTask down = new BukkitRunnable() {
					@Override
					public void run() {
						le.setVelocity(le.getVelocity().setY(-3));
					}
				}.runTaskTimer(plugin, 1, 3);
				new BukkitRunnable() {
					public void run() {
						down.cancel();
					}
				}.runTaskLater(plugin, 12);
			}
		});
	}
	public static void Arclight(Player p, Plugin plugin) {
		ArrayList<LivingEntity> affected = new ArrayList<LivingEntity>();
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
		affected.add(p);
		Archelper(p, affected, new RayTrace(p, 6, 1), plugin);
	}
	
	private static void Archelper(Player p, ArrayList<LivingEntity> affected, RayTrace ray, Plugin plugin) {
		World w = p.getWorld();
		for (int i = 1; i < 7; i++) {
			new BukkitRunnable() {
				public void run() {
					if (ray.hasNext()) {
						Location l = ray.next();
						w.spawnParticle(Particle.FIREWORKS_SPARK, l, 2, 0, 0, 0, 0.05);
						Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {
							if (!affected.contains(le)) {
								w.playSound(l, Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
								w.spawnParticle(Particle.FIREWORKS_SPARK, le.getEyeLocation(), 8, 0, 0.5, 0, 0.05);
								Utils.magicDamage(p, le, 4, plugin);
								le.setVelocity(new Vector(0,0,0));
								affected.add(le);
								LivingEntity le2 = Utils.getNearestEntity(l, affected, 5, 3, 5);
								if (le2 != null) {
									Vector diff = le2.getLocation().subtract(le.getLocation()).toVector();
									RayTrace v = new RayTrace(le.getEyeLocation(), diff, diff.length(), diff.length()/7);
									new BukkitRunnable() {
										public void run() {
											Archelper(p, affected, v, plugin);
										}
									}.runTaskLater(plugin, 4);
								}
							}
						});
					}
				}
			}.runTaskLater(plugin, i);
		}
	}
	
	public static void Soulflare(Player p, Plugin plugin) {
		/* spin faster and faster then explode */
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 1);
		double h = p.getEyeHeight()/2;
		for (int i = 0; i < 5; i ++) {
			Integer in = i;
			ItemStack is = new ItemStack(Material.NETHER_STAR);
			ItemMeta im = is.getItemMeta();
			is.setItemMeta(im);
			Item item = p.getWorld().dropItem(p.getLocation().add(0,h,0), is);
			item.setPickupDelay(Integer.MAX_VALUE);
			item.setGravity(false);
			BukkitTask bt = new BukkitRunnable() {
				@Override
				public void run() {
					double t = (double) item.getTicksLived();
					Location to = Utils.getCirclePoint(p.getLocation().add(0,h,0), 3, (Math.PI*in*2/5) + Math.pow(t, 1.7)/80);
					Location from = item.getLocation();
					item.setVelocity(to.subtract(from).toVector().multiply(0.2));
					if (t%8 == 0) {
						p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, item.getLocation(), 1, 0, 0, 0, 0);
					}
				}
			}.runTaskTimer(plugin, 1, 1);
			BukkitTask trail = new BukkitRunnable() {
				@Override
				public void run() {
					p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, item.getLocation(), 1, 0, 0, 0, 0);
				}
			}.runTaskTimer(plugin, 1, 3);
			new BukkitRunnable() {
				public void run() {
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1, 1);
					p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation(), 30, 0, 0, 0, 0.2);
					item.setVelocity(item.getLocation().subtract(p.getLocation()).toVector().setY(0).normalize());
					bt.cancel();
				}
			}.runTaskLater(plugin, 60);
			new BukkitRunnable() {
				public void run() {
					if (!item.isDead()) {
						Utils.applyNearby(item.getLocation(), p, 2, 2, 2, (LivingEntity le) -> {
							le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 3));
							Utils.magicDamage(p, le, 6, plugin);
						});
						FireworkPlayer.fire(item.getLocation(), Type.BALL, Color.WHITE, false);
						trail.cancel();
						item.remove();
					}
				}
			}.runTaskLater(plugin, 70);
		}
		new BukkitRunnable() {
			public void run() {
				Utils.applyNearby(p.getLocation(), p, 4, 4, 4, (LivingEntity le) -> {
					le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 3));
					Utils.magicDamage(p, le, 6, plugin);
				});
			}
		}.runTaskLater(plugin, 60);
	}
	
	public static void Ice(Player p, Plugin plugin) {
		for (int j = 0; j < 2; j++) {
			new BukkitRunnable() {
				public void run() {
					p.getWorld().playSound(p.getLocation(), Sound.BLOCK_SNOW_PLACE, 1, 1);
				}
			}.runTaskLater(plugin, 10*j);
			for (int i = 0; i < 6; i ++) {
				new BukkitRunnable() {
					public void run() {
						Vector v = p.getLocation().getDirection(); //Multiply the player's direction by the power
						Random rand = new Random();
						v.add(new Vector((rand.nextDouble() - 0.5)/2, p.getLocation().getDirection().getY(), (rand.nextDouble() - 0.5)/2)); //Add the velocity by a random number
						Snowball snow = p.launchProjectile(Snowball.class);
						snow.setVelocity(v);
						snow.setShooter(p);
						snow.setMetadata("ice", new FixedMetadataValue(plugin, true));
					}
				}.runTaskLater(plugin, i + 10*j);
			}
		}
	}
	public static void Freezing(Player p, Plugin plugin) {
		int range = 10;
		int step = 2;
		RayTrace eye = new RayTrace(p, range, step);
		World w = p.getWorld();
		for (int count = 0; count < 5; count++) {
			new BukkitRunnable() {
				public void run() {
					Location l = eye.next();
					if (l.getBlock().getType().equals(Material.AIR)) {
						Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {
							le.damage(6, p);
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
						});
						w.playEffect(l, Effect.STEP_SOUND, Material.ICE);
					}
				}
			}.runTaskLater(plugin, count*3);
		}
	}
	public static boolean Suspension(Player p, Plugin plugin) {
		LivingEntity target = Utils.getCrosshair(p);
		if (target != null && target.getLocation().distance(p.getLocation()) < 10) {
			World w = p.getWorld();
			Vector v = new Vector(0, target.getEyeHeight()/2, 0);
			Utils.applyNearby(target.getLocation(), p, 3, 3, 3, (LivingEntity le) -> {
				le.setGravity(false);
				le.setVelocity(new Vector(0, 0.2, 0));
				for (int i = 0; i < 3; i++) {
					new BukkitRunnable() {
						@Override
						public void run() {
							Vector save = le.getVelocity();
							le.damage(1, p);
							le.setVelocity(save);
							w.playEffect(le.getLocation().add(v), Effect.STEP_SOUND, Material.WATER);
						}
					}.runTaskLater(plugin, i*20);
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						le.setVelocity(new Vector(0,0,0));
					}
				}.runTaskLater(plugin, 20);
				new BukkitRunnable() {
					@Override
					public void run() {
						le.setGravity(true);
					}
				}.runTaskLater(plugin, 40);
			});
			// Sounds and Particles
			w.playSound(target.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1, 1);
			for (int i = 0; i < 4; i++) {
				new BukkitRunnable() {
					@Override
					public void run() {
						w.playSound(target.getLocation(), Sound.WEATHER_RAIN_ABOVE, 1, 1);
					}
				}.runTaskLater(plugin, i*10);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					Location center = target.getLocation().add(v);
					for (Location l:Utils.getSpherePoints(center, 2, 19, 21)) {
						w.spawnParticle(Particle.WATER_WAKE, l, 1, 0, 0, 0, 0);
					}
				}
			}.runTaskLater(plugin, 20);
			for (int i = 0; i < 9; i++) {
				double j = i;
				new BukkitRunnable() {
					@Override
					public void run() {
						double phiMax = Math.PI*((j/9)-0.9);
						Location center = target.getLocation().add(v);
						w.spawnParticle(Particle.WATER_WAKE, center, 1, 1, 1, 1, 0.2);
						for (Location l:Utils.getSpherePoints(center, (float) 2, 
								(int) j*2 + 1, -Math.PI, phiMax, 
								(int) j*2 + 3, (double) 0, 2*Math.PI)) {
							w.spawnParticle(Particle.WATER_DROP, l, 1, 0, 0, 0, 1);
						}
					}
				}.runTaskLater(plugin, i*2);
			}
		}
		else {
			p.sendMessage("You must looking at an enemy within 10 blocks to use Suspension.");
		}
		return true;
	}
	
	public static void Guard(Player p, Plugin plugin) {
		p.setMetadata("guard", new FixedMetadataValue(plugin, true));
		double h = p.getEyeHeight()/2;
		UUID uid = p.getUniqueId();
		for (int i = 0; i < 5; i ++) {
			Integer in = i;
			ItemStack is = new ItemStack(Material.ICE);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("guard" + i + p.getName());
			is.setItemMeta(im);
			Item item = p.getWorld().dropItem(p.getEyeLocation(), is);
			item.setPickupDelay(Integer.MAX_VALUE);
			item.setGravity(false);
			guards.get(uid).push(item);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (item.isDead()) {this.cancel();}
					Location to = Utils.getCirclePoint(p.getLocation().add(0,h,0), 1.5, (Math.PI*in*2/5) + (double) item.getTicksLived()/12);
					Location from = item.getLocation();
					item.setVelocity(to.subtract(from).toVector().multiply(0.2));
				}
			}.runTaskTimer(plugin, 1, 1);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Item pm:guards.get(uid)) {
					p.getWorld().spawnParticle(Particle.BLOCK_DUST, pm.getLocation(), 20, 0, 0, 0, 0.1, icedata);
					pm.remove();
				}
				p.removeMetadata("guard", plugin);
				guards.remove(uid);
			}
		}.runTaskLater(plugin, 160);
	}
	public static boolean consumeGuard(Player p) {
		for (Entity e:p.getNearbyEntities(2, 2, 2)) {
			if (e.getType().equals(EntityType.DROPPED_ITEM)) {
				Item item = (Item) e;
				ItemStack is = item.getItemStack();
				ItemMeta im = is.getItemMeta();
				String name = im.getDisplayName();
				if (is.getType().equals(Material.ICE) && name.startsWith("guard") && name.endsWith(p.getName())) {
					p.setFireTicks(0);
					p.getWorld().spawnParticle(Particle.BLOCK_DUST, item.getLocation(), 20, 0, 0, 0, 0.1, icedata);
					p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
					item.remove();
					return true;
				}
			}
		}
		return false;
	}
	
	public static void Fire(Player p, Plugin plugin) {
		for (int b = 0; b < 2; b++) {
			for (int i = 0; i < 4; i ++) {
				new BukkitRunnable() {
					@Override
					public void run() {
						Vector v = p.getLocation().getDirection();
						Random rand = new Random();
						v.add(new Vector((rand.nextDouble() - 0.5), p.getLocation().getDirection().getY(), (rand.nextDouble() - 0.5)));
						SmallFireball fire = p.launchProjectile(SmallFireball.class);
						fire.setVelocity(v);
						fire.setShooter(p);
					}
				}.runTaskLater(plugin, b*10 + i);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 1);
				}
			}.runTaskLater(plugin, b*10);
		}
	}
	public static void Purifying(Player p, Plugin plugin) {
		p.removePotionEffect(PotionEffectType.BLINDNESS);
		p.removePotionEffect(PotionEffectType.CONFUSION);
		p.removePotionEffect(PotionEffectType.POISON);
		p.removePotionEffect(PotionEffectType.SLOW);
		p.removePotionEffect(PotionEffectType.WEAKNESS);
		p.removePotionEffect(PotionEffectType.WITHER);
		for (Entity e:p.getNearbyEntities(4, 4, 4)) {
			e.setFireTicks(80);
		}
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);
		for (int i = 0; i < 5; i++) {
			int in = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l:Utils.getCirclePoints(p.getLocation().add(0, 0.3, 0), Plane.XZ, in, 8*in+1)) {
						p.getWorld().spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0.1);
					}
				}
			}.runTaskLater(plugin, i);
		}
	}
	public static void Inferno(Player p, Plugin plugin) {
		Location center = p.getLocation();
		p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
		World w = p.getWorld();
		/* Particle Effect of dome creation */
		int phiSteps = 8;
		double phiInterval = Math.PI/(2*phiSteps);
		for (int i = 0; i < phiSteps; i++) {
			int in = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					double hiPhiTop = Math.PI/2 - phiInterval*in;
					double loPhiTop = Math.PI/2 - phiInterval*(in+1);
					for (Location l:Utils.getSpherePoints(center, 8, 3, loPhiTop, hiPhiTop, 32, 0, Math.PI*2)) {
						w.spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0);
					}
					double loPhiBot = Math.PI/2 + phiInterval*in;
					double hiPhiBot = Math.PI/2 + phiInterval*(in+1);
					for (Location l:Utils.getSpherePoints(center, 8, 3, loPhiBot, hiPhiBot, 32, 0, Math.PI*2)) {
						w.spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0);
					}
				}
			}.runTaskLater(plugin, i*3);
		}
		/* Display persistent particles */
		BukkitTask bt = new BukkitRunnable() {
			@Override
			public void run() {
				for (Location l:Utils.getSpherePoints(center, 8, 23, 32)) {
					w.spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0);
				}
			}
		}.runTaskTimer(plugin, 40, 20);
		/* Actual effect that burns and throws back those too close */
		BukkitTask bt2 = new BukkitRunnable() {
			@Override
			public void run() {
				Utils.applyNearby(center, null, 10, 10, 10, (LivingEntity le) -> {
					double distance = le.getLocation().distance(center);
					if (distance > 6) {
						if (distance < 8) { // inside
							le.setVelocity(le.getLocation().subtract(center).toVector().normalize().multiply(-1));
							le.setFireTicks(40);
						}
						else if (distance < 10) { // outside
							le.setVelocity(le.getLocation().subtract(center).toVector().normalize());
							le.setFireTicks(40);
						}
					}
				});
			}
		}.runTaskTimer(plugin, 24, 1);
		new BukkitRunnable() {
			@Override
			public void run() {
				bt.cancel();
				bt2.cancel();
			}
		}.runTaskLater(plugin, 240);
	}
	public static void Divine(Player p, Plugin plugin) {
		p.setMetadata("divine", new FixedMetadataValue(plugin, true));
		p.playSound(p.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1, 1);
		double h = p.getEyeHeight()/2;
		UUID uid = p.getUniqueId();
		for (int i = 0; i < 5; i ++) {
			Integer in = i;
			ItemStack is = new ItemStack(Material.BLAZE_POWDER);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("divine" + i + p.getName());
			is.setItemMeta(im);
			Item item = p.getWorld().dropItem(p.getEyeLocation(), is);
			item.setPickupDelay(Integer.MAX_VALUE);
			item.setGravity(false);
			item.setFireTicks(160);
			item.setInvulnerable(true);
			fires.get(uid).push(item);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (item.isDead()) {this.cancel();}
					Location to = Utils.getCirclePoint(p.getLocation().add(0,h,0), 1.5, (Math.PI*in*2/5) + (double) item.getTicksLived()/12);
					Location from = item.getLocation();
					item.setVelocity(to.subtract(from).toVector().multiply(0.2));
				}
			}.runTaskTimer(plugin, 1, 1);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Item pm:fires.get(uid)) {
					p.getWorld().spawnParticle(Particle.BLOCK_CRACK, pm.getLocation(), 3, 0, 0, 0, 0.1, firedata);
					pm.remove();
				}
				p.removeMetadata("divine", plugin);
				fires.remove(uid);
			}
		}.runTaskLater(plugin, 160);
	}
	public static boolean consumeFire(Player p, Plugin plugin) {
		if (fires.containsKey(p.getUniqueId())) {
			Stack<Item> blocks = fires.get(p.getUniqueId());
			if (blocks.size() > 0) {
				Item pm = blocks.pop();
				p.getWorld().spawnParticle(Particle.BLOCK_CRACK, pm.getLocation(), 3, 0, 0, 0, 0.1, firedata);
				pm.remove();
			}
			if (blocks.size() == 0) {
				p.removeMetadata("divine", plugin);
			}
			return true;
		}
		return false;
	}
	
	public static void Haste(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 4));
		FireworkPlayer.fire(p.getLocation(), Type.STAR, Color.WHITE, false);
	}
	public static void Augment(Player p) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 0));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
			FireworkPlayer.fire(p.getLocation(), Type.STAR, Color.MAROON, false);
	}
	public static void Recovery(Player p) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
			FireworkPlayer.fire(p.getLocation(), Type.STAR, Color.FUCHSIA, true);
	}
	public static void Redemption(Player p, Plugin plugin) {
			p.setMetadata("redemption", new FixedMetadataValue(plugin, true));
			FireworkPlayer.fire(p.getLocation(), Type.STAR, Color.YELLOW, false);
			new BukkitRunnable() {
				@Override
				public void run() {
					p.removeMetadata("redemption", plugin);
				}
			}.runTaskLater(plugin, 160);
	}
	/* Triggered by dying during Redemption buff */
	public static void Revive(Player p, Plugin plugin) {
		p.damage(0);
		p.setHealth(1);
		p.setNoDamageTicks(80);
		p.setGlowing(true);
		// noknockback thing needed too
		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 4));
		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 4));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 6));
		p.sendMessage("The Goddess Arda blesses her servants with eternal life.");
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setGlowing(false);
				FireworkPlayer.fire(p.getLocation(), Type.STAR, Color.YELLOW, true);
				for (Entity e:p.getNearbyEntities(2, 2, 2)) {
					e.setVelocity(e.getLocation().subtract(p.getLocation()).toVector().normalize());
				}
			}
		}.runTaskLater(plugin, 80);
	}
}
