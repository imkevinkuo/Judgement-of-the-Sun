package com.gmail.kvkkuo.JotS.classes;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.gmail.kvkkuo.JotS.utils.RayTrace;
import com.gmail.kvkkuo.JotS.utils.Utils;
import com.gmail.kvkkuo.JotS.utils.Utils.Plane;

public class Raider {
	public static String[] SKILLS = Utils.readSkillsFromCSV("raider.csv");
	private static BlockData firedata = Material.FIRE.createBlockData();

	public static Integer cast(Player p, Integer spell, Integer cooldown, Integer upgrade, Plugin plugin) {
		if (cooldown <= 0) {
			boolean c = true;
			if (spell.equals(0)) {
				if (upgrade.equals(0)) {
					Raider.Shot(p, cooldown, plugin);
				}
				if (upgrade.equals(1)) {
					Raider.Breath(p, cooldown, plugin);
				}
				if (upgrade.equals(2)) {
					Raider.Focus(p, cooldown, plugin);
				}
				if (upgrade.equals(3)) {
					Raider.Burst(p, cooldown, plugin);
				}
				if (c) {cooldown = 12;}
			}
			if (spell.equals(1)) {
				if (upgrade.equals(0)) {
					Raider.Healing(p, cooldown, plugin);
				}
				if (upgrade.equals(1)) {
					Raider.Blinding(p, cooldown, plugin);
				}
				if (upgrade.equals(2)) {
					Raider.Frost(p, cooldown, plugin);
				}
				if (upgrade.equals(3)) {
					Raider.Famine(p, cooldown, plugin);
				}
				if (c) {cooldown = 12;}
			}
			if (spell.equals(2)) {
				if (upgrade.equals(0)) {
					Raider.Leap(p, cooldown, plugin);
				}
				if (upgrade.equals(1)) {
					Raider.Smash(p, cooldown, plugin);
				}
				if (upgrade.equals(2)) {
					Raider.Shells(p, cooldown, plugin);
				}
				if (upgrade.equals(3)) {
					Raider.Rocket(p, cooldown, plugin);
				}
				if (c) {cooldown = 20;}
			}
			if (spell.equals(3)) {
				if (upgrade.equals(0)) {
					Raider.Hooked(p, cooldown, plugin);
				}
				if (upgrade.equals(1)) {
					Raider.Singed(p, cooldown, plugin);
				}
				if (upgrade.equals(2)) {
					Raider.Spiked(p, cooldown, plugin);
				}
				if (upgrade.equals(3)) {
					Raider.Barbed(p, cooldown, plugin);
				}
				if (c) {cooldown = 20;}
			}
		}
		return cooldown;
	}
	public static void Shot(Player p, Integer cooldown, Plugin plugin) {
		Fireball fire = p.launchProjectile(Fireball.class);
		fire.setVelocity(p.getLocation().getDirection());
		fire.setShooter(p);
		fire.setMetadata("shot", new FixedMetadataValue(plugin, true));
	}
	public static void Breath(Player p, Integer cooldown, Plugin plugin) {
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_DEATH, 5, 0);
		Integer range = 8, count = 0;
		Vector dir = p.getLocation().getDirection().normalize().multiply(0.5);
		Vector norm = new Vector(-dir.getZ(), 0, dir.getX()); // need for cone abilities
		RayTrace eye = new RayTrace(p, range, 1);
		double f = 0.5; // offset
		for (count = 0; count < range; count++) {
			Location l = eye.next().clone();
			if (l.getBlock().getType().equals(Material.AIR)) {
				Integer c = count;
				new BukkitRunnable() {
					public void run() {
						Utils.applyNearby(l, p, c, 1, c, (LivingEntity le) -> {
							Utils.magicDamage(p, le, 3, plugin);
							le.setFireTicks(40);
						});
						p.getWorld().spawnParticle(Particle.FLAME, l, 2, 0, f, 0, 0);
						Location sideA = l.clone();
						Location sideB = l.clone();
						for (int i = 0; i < c; i++) {
							p.getWorld().spawnParticle(Particle.FLAME, sideA.add(norm), 2, 0, f, 0, 0);
							p.getWorld().spawnParticle(Particle.FLAME, sideB.subtract(norm), 2, 0, f, 0, 0);
						}
					}
				}.runTaskLater(plugin, count*2);
			}
		}
	}
	public static void Focus(Player p, Integer cooldown, Plugin plugin) {
		Integer i = 0, range = 10;
		RayTrace eye = new RayTrace(p, range, 1);
		p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 5, 1);
		for (i = 0; i < range; i++) {
			Location l =  eye.next().clone();
			if (l.getBlock().getType().equals(Material.AIR) && eye.hasNext()) {
				new BukkitRunnable() {
					public void run() {
						p.getWorld().spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0);
						Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {le.setFireTicks(20);});
					}
				}.runTaskLater(plugin, i*2);
			}
			else {
				new BukkitRunnable() {
					public void run() {
						p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, l, 0, 0, 0, 1, 1);
						p.playSound(l, Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
						Utils.applyNearby(l, p, 4, 4, 4, (LivingEntity le) -> {
							Utils.magicDamage(p, le, 10, plugin);
						});
					}
				}.runTaskLater(plugin, i*2);
				break;
			}
		}
	}
	public static void Burst(Player p, Integer cooldown, Plugin plugin) {
		ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
		World w = p.getWorld();
		w.playSound(p.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1, 1);
		for (Entity e:p.getNearbyEntities(16, 16, 16)) {
			if (e instanceof LivingEntity) {
				list.add((LivingEntity) e);
			}
		}
		for (int i = 0; i < 6; i++) {
			int in = i;
			ItemStack is = new ItemStack(Material.FIRE_CHARGE);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("missile" + in + p.getName());
			is.setItemMeta(im);
			Item item = w.dropItem(p.getEyeLocation(), is);
			item.setGravity(false);
			item.setPickupDelay(Integer.MAX_VALUE);
			/* circle around */
			BukkitTask bt = new BukkitRunnable() {
				@Override
				public void run() {
					Location to = Utils.getCirclePoint(p.getEyeLocation(), 1.5, (Math.PI*in*2/6) - (double) item.getTicksLived()/5);
					Location from = item.getLocation();
					item.setVelocity(to.subtract(from).toVector().multiply(0.2));
				}
			}.runTaskTimer(plugin, 1, 1); 
			/* throw em out */
			new BukkitRunnable() {
				@Override
				public void run() {
					bt.cancel();
					item.setGravity(true);
					if (list.size() > 0) {
						w.playSound(item.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5f, 1);
						LivingEntity target = list.get(in%list.size());
						Vector v = target.getLocation().subtract(item.getLocation()).toVector().multiply(0.08);
						v.add(new Vector(0, 0.3, 0));
						item.setVelocity(v);
					}
				}
			}.runTaskLater(plugin, 20 + i*4);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (item.getTicksLived()%3 == 0) {
						w.spawnParticle(Particle.FLAME, item.getLocation(), 1, 0, 0, 0, 0);
					}
					if (item.isDead()) {
						this.cancel();
					}
					if (item.isOnGround()) {
						detonate(item, p, plugin);
						this.cancel();
					}
					for (Entity e:item.getNearbyEntities(1, 1, 1)) {
						if (e instanceof LivingEntity && !(e.equals(p))) {
							detonate(item, p, plugin);
							this.cancel();
							break;
						}
					}
				}
			}.runTaskTimer(plugin, 20 + i*4, 1);
		}
	}
	public static void detonate(Item item, Player p, Plugin plugin) {
		Utils.applyNearby(item.getLocation(), p, 1, 1, 1, (LivingEntity le) -> {
			Utils.magicDamage(p, le, 2, plugin);
			le.setFireTicks(40);
		});
		p.getWorld().playSound(item.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
		p.getWorld().spawnParticle(Particle.FLAME, item.getLocation(), 6, 0, 0, 0, 0.1);
		item.remove();
	}
	
	public static ItemStack coloredPotion(String name, Color color, Material type) {
		ItemStack is = new ItemStack(type, 1);
		PotionMeta pm = (PotionMeta) is.getItemMeta();
		pm.setColor(color);
		pm.setDisplayName(name);
		is.setItemMeta(pm);
		return is;
	}
	public static void Healing(Player p, Integer cooldown, Plugin plugin) {
		World w = p.getWorld();
		ThrownPotion tp = (ThrownPotion) w.spawnEntity(p.getLocation(), EntityType.SPLASH_POTION);
		tp.setItem(coloredPotion("heal", Color.FUCHSIA, Material.SPLASH_POTION));
		p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 0));
		p.removePotionEffect(PotionEffectType.POISON);
	}
	public static void Blinding(Player p, Integer cooldown, Plugin plugin) {
		Vector v = p.getLocation().getDirection().normalize().multiply(0.3);
		double x = v.getX();
		double z = v.getZ();
		Vector[] vectors = {new Vector(x, 0.3, z),
		                    new Vector(-x, 0.3, -z),
		                    new Vector(z, 0.3, -x),
		                    new Vector(-z, 0.3, x)};
		for (int i = 0; i < 4; i++) {
			ThrownPotion tp = p.launchProjectile(ThrownPotion.class);
			tp.setItem(coloredPotion("blind", Color.BLACK, Material.SPLASH_POTION));
			tp.setVelocity(vectors[i]);
		}
	}
	public static void Frost(Player p, Integer cooldown, Plugin plugin) {
		World w = p.getWorld();
		ThrownPotion tp = (ThrownPotion) w.spawnEntity(p.getLocation(), EntityType.SPLASH_POTION);
		tp.setItem(coloredPotion("frost", Color.AQUA, Material.SPLASH_POTION));
		p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
		
	}
	public static void Famine(Player p, Integer cooldown, Plugin plugin) {
		Vector v = p.getLocation().getDirection().normalize().multiply(0.3);
		double x = v.getX();
		double z = v.getZ();
		Vector[] vectors = {new Vector(x, 0.3, z),
		                    new Vector(-x, 0.3, -z),
		                    new Vector(z, 0.3, -x),
		                    new Vector(-z, 0.3, x)};
		for (int i = 0; i < 4; i++) {
			ThrownPotion tp = p.launchProjectile(ThrownPotion.class);
			tp.setItem(coloredPotion("famine", Color.GREEN, Material.SPLASH_POTION));
			tp.setVelocity(vectors[i]);
		}
	}
	
	public static void Leap(Player p, Integer cooldown, Plugin plugin) {
		p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 4, 1);
		p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 30, 1, 0, 1, 0.3);
		p.setVelocity(p.getLocation().getDirection().multiply(-2).setY(1));
		p.setMetadata("nofall", new FixedMetadataValue(plugin, true));
		Utils.applyNearby(p.getLocation(), p, 4, 4, 4, (LivingEntity le) -> {le.setFireTicks(60);});
	}
	public static void Smash(Player p, Integer cooldown, Plugin plugin) {
		p.setVelocity(p.getLocation().getDirection().multiply(2).setY(1));
		p.setMetadata("bsmash", new FixedMetadataValue(plugin, true));
		p.setMetadata("nofall", new FixedMetadataValue(plugin, true));
		new BukkitRunnable() {
			@Override
			public void run() {
				if (p.isOnGround() && p.hasMetadata("bsmash")) {
					Raider.SmashT(p, plugin);
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}
	public static void SmashT(Player p, Plugin plugin) {
		p.removeMetadata("bsmash", plugin);
		p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 4, 1);
		for (int r = 1; r < 4; r++) {
			int r2 = r;
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l:Utils.getCirclePoints(p.getLocation(), Plane.XZ, r2, 6*r2 + 2)) {
						p.getWorld().spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0);
					}
				}
			}.runTaskLater(plugin, (r-1)*2);
		}
		Utils.applyNearby(p.getLocation(), p, 3, 1, 3, (LivingEntity le) -> {le.setFireTicks(60);});
	}
	public static void Shells(Player p, Integer cooldown, Plugin plugin) {
		p.setVelocity(p.getLocation().getDirection().multiply(2).setY(1));
		p.setMetadata("nofall", new FixedMetadataValue(plugin, true));
		for (int l = 0; l < 5; l ++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EGG_THROW, 1, 1);
					Vector v = p.getLocation().getDirection().multiply(0.3).setY(-0.2);
					ItemStack is = new ItemStack(Material.FIRE_CHARGE);
					Item item = p.getWorld().dropItem(p.getLocation(), is);
					item.setVelocity(v);
					item.setPickupDelay(Integer.MAX_VALUE);
					new BukkitRunnable() {
						@Override
						public void run() {
							for (Entity e : item.getNearbyEntities(1, 1, 1)) {
								if (e instanceof LivingEntity && !e.equals(p)) {
									detonate(item, p, plugin);
									this.cancel();
									break;
								}
							}
							if (item.isOnGround()) {
								detonate(item, p, plugin);
								this.cancel();
							}
						}
					}.runTaskTimer(plugin, 1, 1);
				}
			}.runTaskLater(plugin, l*4);
		}
	}
	public static void Rocket(Player p, Integer cooldown, Plugin plugin) {
		int delay = 40;
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, delay, 1));
		p.playSound(p.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
		p.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, p.getEyeLocation(), 12, 0.5, 0.1, 0.5, 1);
		/*
		 * repeating task here, circles inward
		 */
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setVelocity(p.getVelocity().add(new Vector(0,2,0)));
				p.setMetadata("nofall", new FixedMetadataValue(plugin, true));
				p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 1, 0, 0, 0, 1);
				p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 30, 1, 0, 1, 0.3);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
				Utils.applyNearby(p.getLocation(), p, 4, 4, 4, (LivingEntity le) -> {
					Utils.magicDamage(p, le, 6, plugin);
				});
			}
		}.runTaskLater(plugin, delay);
	}

	public static void Hooked(Player p, Integer cooldown, Plugin plugin) {
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 4, 1);
		ArrayList<Item> hooklist = new ArrayList<Item>();
		for (int i = 0; i < 8; i ++) {
			ItemStack is = new ItemStack(Material.TRIPWIRE_HOOK);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("hook" + i + p.getName());
			is.setItemMeta(im);
			Item item = p.getWorld().dropItem(p.getEyeLocation(), is);
			item.setVelocity(p.getLocation().getDirection().multiply(0.3 + 0.12*i));
			item.setPickupDelay(Integer.MAX_VALUE);
			hooklist.add(item);
			BukkitTask bt = new BukkitRunnable() {
				@Override
				public void run() {
					if (item.isDead()) {
						this.cancel();
					}
					else {
						Utils.applyNearby(item.getLocation(), p, 1, 1, 1, (LivingEntity le) -> {
							le.damage(2, p);
							Vector vl = p.getEyeLocation().toVector().subtract(le.getLocation().toVector()).multiply(0.2);
							le.setVelocity(vl);
							for (Item hook:hooklist) {
								Vector ve = p.getEyeLocation().toVector().subtract(hook.getLocation().toVector()).multiply(0.3);
								hook.setVelocity(ve);
								new BukkitRunnable() {
									public void run() {
										hook.remove();
									}
								}.runTaskLater(plugin, 5);
							}
							this.cancel();
						});
					}
				}
			}.runTaskTimer(plugin, 1, 1);
			new BukkitRunnable() {
				public void run() {
					if (item.getTicksLived() >= 55) {
						p.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.TRIPWIRE_HOOK);
					}
					item.remove();
					bt.cancel();
				}
			}.runTaskLater(plugin, 60);
		}
	}
	public static void Singed(Player p, Integer cooldown, Plugin plugin) {
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
		for (int i = 0; i < 8; i ++) {
			ItemStack is = new ItemStack(Material.BLAZE_POWDER);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("fire" + i + p.getName());
			is.setItemMeta(im);
			Item item = p.getWorld().dropItem(p.getEyeLocation(), is);
			item.setFireTicks(60);
			item.setVelocity(p.getLocation().getDirection().multiply(0.3 + 0.12*i));
			item.setPickupDelay(Integer.MAX_VALUE);

			new BukkitRunnable() {
				public void run() {
					Utils.applyNearby(item.getLocation(), p, 1, 1, 1, (LivingEntity le) -> {
						le.setFireTicks(60);
						le.damage(2, p);
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, item.getLocation(), 3, 0, 0, 0, 0.1, firedata);
						item.remove();
						this.cancel();
					});
					if (item.isOnGround()) {
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, item.getLocation(), 3, 0, 0, 0, 0.1, firedata);
						item.remove();
						this.cancel();
					}
				}
			}.runTaskTimer(plugin, 1, 1);
		}
	}
	private static BlockData spikedata = Material.DEAD_BUSH.createBlockData();
	public static void Spiked(Player p, Integer cooldown, Plugin plugin) {
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SKELETON_STEP, 5, 1);

		for (int i = 0; i < 8; i ++) {
			ItemStack is = new ItemStack(Material.DEAD_BUSH);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("spike" + i + p.getName());
			is.setItemMeta(im);

			Item item = p.getWorld().dropItem(p.getEyeLocation(), is);
			item.setVelocity(p.getLocation().getDirection().multiply(0.3 + 0.12*i));
			item.setPickupDelay(Integer.MAX_VALUE);

			BukkitTask bt = new BukkitRunnable() {
				@Override
				public void run() {
					Utils.applyNearby(item.getLocation(), p, 1, 1, 1, (LivingEntity le) -> {
						le.damage(6, p);
						le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 3));
						p.getWorld().playSound(item.getLocation(), Sound.ENTITY_SKELETON_STEP, 0.2f, 1);
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, 5, 0, 0, 0, spikedata);
						this.cancel();
					});
				}
			}.runTaskTimer(plugin, 1, 1);
			new BukkitRunnable() {
				public void run() {
					p.getWorld().playSound(item.getLocation(), Sound.ENTITY_SKELETON_STEP, 0.2f, 1);
					p.getWorld().spawnParticle(Particle.BLOCK_CRACK, item.getLocation(), 5, 0, 0, 0, spikedata);					item.remove();
					bt.cancel();
				}
			}.runTaskLater(plugin, 120);
		}
	}
	public static void Barbed(Player p, Integer cooldown, Plugin plugin) {
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 4, 1);

		for (int i = 0; i < 8; i ++) {
			ItemStack is = new ItemStack(Material.NAME_TAG);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("barb" + i + p.getName());
			is.setItemMeta(im);

			Item item = p.getWorld().dropItem(p.getEyeLocation(), is);
			item.setVelocity(p.getLocation().getDirection().multiply(0.3 + 0.12*i));
			item.setPickupDelay(Integer.MAX_VALUE);

			new BukkitRunnable() {
				@Override
				public void run() {
					Utils.applyNearby(item.getLocation(), p, 1, 1, 1, (LivingEntity le) -> {
						le.damage(5, p);
						le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 3));
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, item.getLocation(), 5, 0, 0, 0, spikedata);					item.remove();
						p.getWorld().playSound(item.getLocation(), Sound.BLOCK_TRIPWIRE_ATTACH, 0.2f, 1);
						item.remove();
						this.cancel();
					});
					if (item.isOnGround()) {
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, item.getLocation(), 5, 0, 0, 0, spikedata);					item.remove();
						p.getWorld().playSound(item.getLocation(), Sound.BLOCK_TRIPWIRE_ATTACH, 0.2f, 1);
						item.remove();
						this.cancel();
					}
				}
			}.runTaskTimer(plugin, 1, 1);
		}
	}
}
