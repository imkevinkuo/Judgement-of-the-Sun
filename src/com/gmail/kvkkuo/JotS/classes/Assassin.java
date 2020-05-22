package com.gmail.kvkkuo.JotS.classes;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import com.gmail.kvkkuo.JotS.utils.Geometry;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import com.gmail.kvkkuo.JotS.utils.Utils;

public class Assassin {
	public static String[] SKILLS = Utils.readSkillsFromCSV("assassin.csv");

	public static HashMap<UUID, Integer> mirages = new HashMap<UUID, Integer>();
	public static HashMap<UUID, ItemStack[]> armors = new HashMap<UUID, ItemStack[]>();
	public static HashMap<UUID, Location> mark = new HashMap<UUID, Location>();

	public static Integer cast(Player p, Integer spell, Integer cooldown, Integer upgrade, Plugin plugin) {
		if (cooldown <= 0) {
			boolean c = true;
			if (spell.equals(0)) {
				if (upgrade.equals(0)) {
					Assassin.Smoke(p, plugin);
				}
				if (upgrade.equals(1)) {
					Assassin.Spray(p, plugin);
				}
				if (upgrade.equals(2)) {
					Assassin.Arrow(p, plugin);
				}
				if (upgrade.equals(3)) {
					Assassin.Kinetic(p, plugin);
				}
				if (c) {cooldown = 12;}
			}
			if (spell.equals(1)) {
				if (upgrade != null) {
					Assassin.Rush(p, plugin, upgrade);
				}
				if (c) {cooldown = 12;}
			}
			if (spell.equals(2)) {
				if (upgrade.equals(0)) {
					Assassin.Stealth(p, plugin, 80);
				}
				if (upgrade.equals(1)) {
					Assassin.Illusion(p, plugin);
				}
				if (upgrade.equals(2)) {
					Assassin.Deception(p, plugin);
				}
				if (upgrade.equals(3)) {
					c = Assassin.Mirage(p, plugin);
					if (!c) {
						cooldown = 3;
					}
				}
				if (c) {cooldown = 20;}
			}
			if (spell.equals(3)) {
				if (upgrade.equals(0)) {
					 c = Assassin.Shift(p);
				}
				if (upgrade.equals(1)) {
					c = Assassin.Mark(p);
					if (!c) {
						cooldown = 2;
					}
				}
				if (upgrade.equals(2)) {
					c = Assassin.Bind(p, plugin);
				}
				if (upgrade.equals(3)) {
					Assassin.Soul(p, plugin);
				}
				if (c) {cooldown = 20;}
			}
		}
		return cooldown;
	}
	public static void Smoke(Player p, Plugin plugin) {
		Item item = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.QUARTZ_BLOCK));
		item.setVelocity(p.getLocation().getDirection());
		item.setPickupDelay(Integer.MAX_VALUE);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (item.isOnGround()) {
					Utils.applyNearby(item.getLocation(), p, 4, 4, 4, (LivingEntity le) -> {
						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 3));
					});
					item.getWorld().playSound(item.getLocation(), Sound.BLOCK_SAND_BREAK, 1, 1);
					FireworkPlayer.fire(item.getLocation(), Type.BALL_LARGE, Color.GRAY, true);
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}
	public static void Spray(Player p, Plugin plugin) {
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SPIDER_DEATH, 5, 0);
		for (int i = 0; i < 8; i ++) {
			ItemStack is = new ItemStack(Material.STRING);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("string" + i);
			is.setItemMeta(im);
			new BukkitRunnable() {
				public void run() {
					Item item = p.getWorld().dropItem(p.getEyeLocation(), is);
					Random rand = new Random();
					item.setVelocity(p.getLocation().getDirection().multiply(1+rand.nextDouble()).add(new Vector((rand.nextDouble() - 0.5)/2, (rand.nextDouble() - 0.5)/2, (rand.nextDouble() - 0.5)/2)));
					item.setPickupDelay(Integer.MAX_VALUE);
					new BukkitRunnable() {
						@Override
						public void run() {
							Utils.applyNearby(item.getLocation(), p, 1, 1, 1, (LivingEntity le) -> {
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1));
								p.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.COBWEB);
								item.remove();
								this.cancel();
							});
							if (item.isOnGround()) {
								p.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.COBWEB);
								item.remove();
								this.cancel();
							}
						}
					}.runTaskTimer(plugin, 1, 1);
				}
			}.runTaskLater(plugin, i*2);
		}
	}
	public static void Arrow(Player p, Plugin plugin) {
		for (int i = 0; i < 12; i ++) {
			new BukkitRunnable() {
				public void run() {
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
					Vector v = p.getLocation().getDirection().multiply(2); //Multiply the player's direction by the power
					Random rand = new Random();
					v.add(new Vector((rand.nextDouble() - 0.5) / 2, (rand.nextDouble()) / 2, (rand.nextDouble() - 0.5) / 2)); //Add the velocity by a random number
					Arrow b = p.launchProjectile(Arrow.class);
					b.setVelocity(v);
					b.setShooter(p);
				}
			}.runTaskLater(plugin, i*2);
		}
	}
	public static void Kinetic(Player p, Plugin plugin) {
		Item item = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.WHITE_WOOL));
		item.setPickupDelay(Integer.MAX_VALUE);
		item.setVelocity(p.getLocation().getDirection());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (item.isOnGround()) {
					Utils.applyNearby(item.getLocation(), null, 4, 4, 4, (LivingEntity le) -> {
						le.setVelocity(le.getEyeLocation().subtract(item.getLocation()).toVector().normalize());
					});
					item.getWorld().playSound(item.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
					FireworkPlayer.fire(item.getLocation(), Type.BALL_LARGE, Color.GRAY, true);
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}
	
	public static void Rush(Player p, Plugin plugin, int type) {
		Location initial = p.getLocation();
		p.setVelocity(new Vector(0, 0.5, 0));
		Vector v = p.getLocation().getDirection().multiply(4).setY(0);
		new BukkitRunnable() {
			public void run() {
				p.setVelocity(v);
			}
		}.runTaskLater(plugin, 2);
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				/* after 8 blocks travelled, sharply slow down */
				if (p.getLocation().distance(initial) > 8) {
					p.setVelocity(p.getVelocity().multiply(0.2));
					this.cancel();
				}
				BlockData md = Material.QUARTZ_BLOCK.createBlockData();
				p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 3, 0, 0, 0, 0.1, md);
				for (Entity e : p.getNearbyEntities(1, 1, 1)) {
					if (e instanceof LivingEntity) {
						LivingEntity le = (LivingEntity) e;
						le.damage(4, p);
						if (type == 1) {
							Stealth(p, plugin, 80);
						}
						if (type == 2) {
							e.setVelocity(new Vector(0, 1, 0));
						}
						if (type == 3) {
							Lightning(p, le, plugin);
							this.cancel();
						}
					}
				}
			}
		}.runTaskTimer(plugin, 3, 1);
		new BukkitRunnable() {
			public void run() {
				bt.cancel();
			}
		}.runTaskLater(plugin, 10);
	}
	private static void Lightning(Player p, LivingEntity e, Plugin plugin) {
		/* Start by counting how many enemies nearby and schedule the delayed task */
		Integer count = 0;
		for (Entity e2 : p.getNearbyEntities(8, 5, 8)) {
			if (count < 5 && !e.equals(e2) && e2 instanceof LivingEntity) {
				count++;
				new BukkitRunnable() {
					public void run() {
						((LivingEntity) e2).damage(2, p);
						Location el = e2.getLocation();
						p.teleport(el);
						p.getWorld().spawnParticle(Particle.PORTAL, p.getEyeLocation(), 20, 0.4, 0.4, 0.4, 0);
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
					}
				}.runTaskLater(plugin, count*2);
			}
		}
		if (count > 1) {
			Location oLoc = p.getLocation();
			Vector oVector = p.getVelocity();
			p.setNoDamageTicks(20);
			p.sendMessage("Your Lightning flashes you around your enemies.");
			
			new BukkitRunnable() {
				public void run() {
					p.teleport(oLoc);
					p.setVelocity(oVector);
				}
			}.runTaskLater(plugin, count*2 + 2);
		}
	}

	public static void Stealth(Player p, Plugin plugin, int duration) {
		// remove and store armor
		UUID id = p.getUniqueId();
		armors.put(id, p.getEquipment().getArmorContents());
		p.getEquipment().setArmorContents(new ItemStack[4]);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0));
		p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, p.getLocation(), 20, 1, 1, 1, 0.2);
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
		new BukkitRunnable() {
		public void run() {
				exitStealth(p, null, plugin);
			}
		}.runTaskLater(plugin, duration);
	}
	// Target will be null if stealth timed out
	public static void exitStealth(Player p, LivingEntity target, Plugin plugin) {
		UUID id = p.getUniqueId();
		if (armors.containsKey(id)) {
			p.getEquipment().setArmorContents(armors.get(id));
			armors.remove(id);
			if (target != null) {
				target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 2));
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1, 1);
			}
		}
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
	}
	public static void Illusion(Player p, Plugin plugin) {
		p.setMetadata("illusion", new FixedMetadataValue(plugin, true));
		BlockData md = Material.OAK_WOOD.createBlockData();
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
		for (Location loc: Geometry.getCirclePoints(p.getLocation(), Geometry.Plane.XZ, 2, 20)) {
			p.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 1, 0, 0, 0, 1, md);
		}
		new BukkitRunnable() {
			public void run() {
				p.removeMetadata("illusion", plugin);
			}
		}.runTaskLater(plugin, 100);
	}
	public static void IllusionT(Player p, Plugin plugin) {
		Stealth(p, plugin, 80);
		Utils.applyNearby(p.getLocation(), p, 3, 3, 3, (LivingEntity le) -> {
			le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
		});
	}
	public static void Deception(Player p, Plugin plugin) {
		p.setMetadata("deception", new FixedMetadataValue(plugin, true));
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
		for (Location loc: Geometry.getCirclePoints(p.getLocation(), Geometry.Plane.XZ, 2, 20)) {
			BlockData md = Material.STONE.createBlockData();
			p.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 1, 0, 0, 0, 1, md);
		}
		new BukkitRunnable() {
			public void run() {
				p.removeMetadata("deception", plugin);
			}
		}.runTaskLater(plugin, 100);
	}
	public static void DeceptionT(Player p, Plugin plugin) {
		Stealth(p, plugin, 80);
		Utils.applyNearby(p.getLocation(), p, 3, 3, 3, (LivingEntity le) -> {
			le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 0));
		});
	}
	public static boolean Mirage(Player p, Plugin plugin) {
		UUID id = p.getUniqueId();
		Stealth(p, plugin, 50);
		if (mirages.containsKey(id)) {
			mirages.put(id, mirages.get(id) + 1);
		}
		else {
			mirages.put(id, 0);
		}
		if (mirages.get(id) == 2) {
			return true;
		}
		return false;
	}
	
	public static boolean Shift(Player p) {
		LivingEntity le = Geometry.getCrosshair(p);
		if (le != null) {
			Location ebe = le.getLocation().add(le.getLocation().getDirection().setY(0).multiply(-1).normalize());
			FireworkPlayer.fire(p.getLocation(), Type.BURST, Color.BLACK, true);
			ebe.setYaw(le.getLocation().getYaw());
			ebe.setPitch(le.getLocation().getPitch());
			p.teleport(ebe);
		}
		else {
			p.sendMessage("You must be looking at someone to cast Umbra Shift.");
			return false;
		}
		return true;
	}
	public static boolean Mark(Player p) {
		if (mark.get(p.getUniqueId()) == null) {
			FireworkPlayer.fire(p.getLocation(), Type.BURST, Color.BLACK, true);
			mark.put(p.getUniqueId(), p.getLocation());
			p.sendMessage("You set an Umbra Mark at your location.");
			return false;
		}
		else {
			if (mark.get(p.getUniqueId()).distance(p.getLocation()) <= 20) {
				FireworkPlayer.fire(p.getLocation(), Type.BURST, Color.BLACK, true);
				p.teleport(mark.get(p.getUniqueId()));
				p.sendMessage("You return to your Umbra Mark.");
				mark.remove(p.getUniqueId());
				return true;
			}
			else {
				p.sendMessage("You are too far away from your Umbra Mark!");
			}
		}
		return false;
	}
	public static boolean Bind(Player p, Plugin plugin) {
		Entity e = Geometry.getCrosshair(p);
		if (e != null) {
			FireworkPlayer.fire(p.getLocation(), Type.BURST, Color.BLACK, true);
			FireworkPlayer.fire(p.getLocation(), Type.BURST, Color.BLACK, true);
			BukkitTask bt = new BukkitRunnable() {
				@Override
				public void run() {
					double d = p.getLocation().distance(e.getLocation());
					if (d > 20) {
						p.sendMessage("Target moved too far away!");
						this.cancel();
					}
					else if (d > 5) {
						Location ebe = e.getLocation();
						ebe.setYaw(p.getLocation().getYaw());
						ebe.setPitch(p.getLocation().getPitch());
						p.teleport(ebe);
						FireworkPlayer.fire(p.getLocation(), Type.BURST, Color.BLACK, true);
					}
				}
			}.runTaskTimer(plugin, 1, 10);
			new BukkitRunnable() {
			public void run() {
					bt.cancel();
				}
			}.runTaskLater(plugin, 100);
		}
		else {
			p.sendMessage("You must be looking at someone to cast Umbra Bind.");
			return false;
		}
		return true;
	}
	public static void Soul(Player p, Plugin plugin) {
		FireworkPlayer.fire(p.getLocation(), Type.BURST, Color.WHITE, true);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);
		p.setMetadata("soul", new FixedMetadataValue(plugin, true));
		p.setGlowing(true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 3));
		new BukkitRunnable() {
			public void run() {
				p.sendMessage("Your Umbra Soul fades.");
				p.removeMetadata("soul", plugin);
				p.setGlowing(false);
			}
		}.runTaskLater(plugin, 60);
	}
}
