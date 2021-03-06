package com.gmail.kvkkuo.JotS.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.gmail.kvkkuo.JotS.utils.Geometry;
import com.gmail.kvkkuo.JotS.utils.RayTrace;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.gmail.kvkkuo.JotS.utils.Utils;

public class Duelist {
	public static String[] SKILLS = Utils.readSkillsFromCSV("duelist.csv");

	private static PotionEffectType[] P_BUFFS = new PotionEffectType[] {
		PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.FIRE_RESISTANCE, 
		PotionEffectType.INCREASE_DAMAGE, PotionEffectType.INVISIBILITY,
		PotionEffectType.JUMP, PotionEffectType.REGENERATION, PotionEffectType.SPEED
	};
	
	private static PotionEffectType[] P_NERFS = new PotionEffectType[] {
		PotionEffectType.WEAKNESS, PotionEffectType.BLINDNESS, 
		PotionEffectType.HUNGER, PotionEffectType.POISON,
		PotionEffectType.SLOW, PotionEffectType.CONFUSION
	};

	private static PotionEffect spearSlow = new PotionEffect(PotionEffectType.SLOW, 80, 2);
	private static PotionEffect duelistRegen = new PotionEffect(PotionEffectType.REGENERATION, 40, 2);
	private static PotionEffect duelistSpeed = new PotionEffect(PotionEffectType.SPEED, 40, 2);
	private static List<PotionEffectType> pBuffs = new ArrayList<>(Arrays.asList(P_BUFFS));
	private static List<PotionEffectType> pNerfs = new ArrayList<>(Arrays.asList(P_NERFS));
	private static PotionEffect crippleEffect = new PotionEffect(PotionEffectType.SLOW, 100, 1);
	private static PotionEffect dazeEffect1 = new PotionEffect(PotionEffectType.BLINDNESS, 100, 0);
	private static PotionEffect dazeEffect2 = new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 0);
	private static BlockData glassblock = Material.GLASS.createBlockData();
	private static BlockData redblock = Material.REDSTONE_BLOCK.createBlockData();
	private static BlockData obsblock = Material.OBSIDIAN.createBlockData();
	private static BlockData ironblock = Material.IRON_BLOCK.createBlockData();
	private static BlockData glowstone = Material.GLOWSTONE.createBlockData();
	private static DustOptions pinkDust = new DustOptions(Color.fromRGB(255, 100, 200), 1);
	private static DustOptions redDust = new DustOptions(Color.fromRGB(255, 0, 0), 1);
	
	public static HashMap<UUID, List<Item>> shields = new HashMap<>();

	public static Integer cast(Player p, Integer spell, Integer cooldown, Integer upgrade, Plugin plugin) {
		if (cooldown <= 0) {
			boolean c = true;
			if (spell.equals(0)) {
				if (upgrade.equals(0)) {
					Duelist.Spearhead(p, plugin);
				}
				if (upgrade.equals(1)) {
					Duelist.Sideswipe(p, plugin);
				}
				if (upgrade.equals(2)) {
					Duelist.Axeheave(p, plugin);
				}
				if (upgrade.equals(3)) {
					Duelist.Bladereap(p, plugin);
				}
				if (c) {cooldown = 12;}
			}
			if (spell.equals(1)) {
				if (upgrade.equals(0)) {
					Duelist.Will(p, plugin);
				}
				if (upgrade.equals(1)) {
					Duelist.Rage(p, plugin);
				}
				if (upgrade.equals(2)) {
					Duelist.Resolve(p, plugin);
				}
				if (upgrade.equals(3)) {
					Duelist.Fury(p, plugin);
				}
				if (c) {cooldown = 12;}
			}
			if (spell.equals(2)) {
				if (upgrade.equals(0)) {
					Duelist.Daze(p, plugin);
				}
				if (upgrade.equals(1)) {
					Duelist.Cripple(p, plugin);
				}
				if (upgrade.equals(2)) {
					Duelist.Shatter(p, plugin);
				}
				if (upgrade.equals(3)) {
					Duelist.Bleed(p, plugin);
				}
				if (c) {cooldown = 20;}
			}
			if (spell.equals(3)) {
				if (upgrade.equals(0)) {
					Duelist.Ward(p, plugin);
				}
				if (upgrade.equals(1)) {
					Duelist.Blade(p, plugin);
				}
				if (upgrade.equals(2)) {
					Duelist.Bind(p, plugin);
				}
				if (upgrade.equals(3)) {
					Duelist.Thief(p, plugin);
				}
				if (c) {cooldown = 20;}
			}
		}
		return cooldown;
	}
	private static void Spearhead(Player p, Plugin plugin) {
		int range = 10;
		int space = 1;

		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		p.setVelocity(p.getLocation().getDirection().setY(0).multiply(-0.5));
		new BukkitRunnable() {
			public void run() {
				p.setVelocity(p.getLocation().getDirection().setY(0));
				w.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1);
				
				RayTrace eye = new RayTrace(p, range, space);
				for (int count = 0; count < (int) range/space; count++) {
					new BukkitRunnable() {
						public void run() {
							Location l = eye.next();
							if (l.getBlock().getType().equals(Material.AIR)) {
								w.spawnParticle(Particle.CRIT, l, 4, 0, 0, 0, 0.3);
								Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {	
									le.damage(6,p); 
									le.addPotionEffect(spearSlow);
								});
							}
						}
					}.runTaskLater(plugin, count);
				}
			}
		}.runTaskLater(plugin, 10);
	}
	private static void Sideswipe(Player p, Plugin plugin) {
		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));

		double angle = p.getLocation().getYaw()*Math.PI/180;

		double startAngle = angle - Math.PI;
		for (int i = 0; i < 9; i++) {
			int in = i+1;
			
			new BukkitRunnable() {
				public void run() {
					double newAngle = startAngle - Math.PI*in/9;
					Vector dir2 = new Vector(-Math.sin(newAngle), 0.3, Math.cos(newAngle)).multiply(3);
					Location l = p.getLocation().add(p.getLocation().getDirection().normalize()).add(dir2);
					w.spawnParticle(Particle.CRIT, l, 4, 0, 0, 0, 0.3);
					Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {	
						le.damage(6, p);
						le.setVelocity(dir2.multiply(0.2));
					});
				}
			}.runTaskLater(plugin, i);
			
			new BukkitRunnable() {
				public void run() {
					double newAngle = startAngle + Math.PI*in/9;
					Vector dir2 = new Vector(-Math.sin(newAngle), 0.3, Math.cos(newAngle)).multiply(3);
					Location l = p.getLocation().add(p.getLocation().getDirection().normalize()).add(dir2);
					w.spawnParticle(Particle.CRIT, l, 4, 0, 0, 0, 0.3);
					Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {	
						le.damage(6, p);
						le.setVelocity(dir2.multiply(0.2));
					});
				}
			}.runTaskLater(plugin, i+10);
		}
		
		w.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
		new BukkitRunnable() {
			public void run() {
				w.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
			}
		}.runTaskLater(plugin, 10);
	}
	private static void Axeheave(Player p, Plugin plugin) {
		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		p.setVelocity(new Vector(0, 0.5, 0));
		BlockData md = Material.OAK_WOOD.createBlockData();
		new BukkitRunnable() {
			public void run() {
				p.setVelocity(new Vector(0, -1, 0));
				w.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
			}
		}.runTaskLater(plugin, 10);
		
		int range = 9, count = 0, magnitude = 1;
		Vector dir = p.getLocation().getDirection().setY(0).normalize().multiply(0.8);
		Vector norm = new Vector(-dir.getZ(), 0, dir.getX()).multiply(0.8);
		RayTrace eye = new RayTrace(p.getLocation(), dir, range, magnitude);
		for (count = 0; count < range; count+=magnitude) {
			Location center = eye.next().clone();
			Integer c = count;
			new BukkitRunnable() {
				@Override
				public void run() {
					Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
						le.damage(2,p); 
					});
					Location groundLoc = Geometry.getGroundLocation(center, 6);
					if (c%3 == 0) {
						w.playSound(groundLoc, Sound.BLOCK_WOOD_BREAK, 1, 1);
					}
					for (Location l: Geometry.getLine(groundLoc, norm, c)) {
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, l, 2, 0, 0, 0, 0, md);
					}
				}
			}.runTaskLater(plugin, (count/3)*5 + 10);
		}
	}
	private static void Bladereap(Player p, Plugin plugin) {
		World w = p.getWorld();
		
		Location initial = p.getLocation();
		Vector up = new Vector(0, 0.5, 0);
		p.setVelocity(up);
		Vector v = p.getLocation().getDirection().multiply(4).setY(0);
		new BukkitRunnable() {
			public void run() {
				w.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1);
				p.setVelocity(v);
			}
		}.runTaskLater(plugin, 2);
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				if (p.getLocation().distance(initial) > 3) {
					p.setVelocity(p.getVelocity().multiply(0.2));
					this.cancel();
				}
				p.getWorld().spawnParticle(Particle.CRIT, p.getLocation(), 3, 0, 0, 0, 0);
				Utils.applyNearby(p.getLocation(), p, 1, 1, 1, (LivingEntity le) -> {	
					le.damage(4,p); 
				});
			}
		}.runTaskTimer(plugin, 2, 1);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				bt.cancel();
				w.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1, 1);
				p.setVelocity(new Vector(0, 1, 0));
				Utils.applyNearby(p.getLocation(), p, 3, 3, 3, (LivingEntity le) -> {	
					le.setVelocity(new Vector(0,1,0));
				});
			}
		}.runTaskLater(plugin, 10);
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setFallDistance(-16);
				w.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
				w.spawnParticle(Particle.CRIT, p.getLocation(), 30, 0, 0, 0, 0.1);
				p.setVelocity(new Vector(0, -0.8, 0));
				Utils.applyNearby(p.getLocation(), p, 2, 2, 2, (LivingEntity le) -> {	
					le.setVelocity(new Vector(0,-1.2,0));
				});
				for (int i = 0; i < 3; i++) {
					int in = i*4;
					new BukkitRunnable() {
						@Override
						public void run() {
							for (Location cLoc: Geometry.getCirclePoints(p.getLocation(), Geometry.Plane.XZ, 2, 30)) {
								w.spawnParticle(Particle.CRIT, cLoc, 1, 0, 0, 0, 0);
							}
						}
					}.runTaskLater(plugin, in);
				}
			}
		}.runTaskLater(plugin, 20);
	}
	//
	private static void Will(Player p, Plugin plugin) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2));
		p.getWorld().playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 1, 1);
		p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getEyeLocation(), 20, 0.5, 0, 0.5, ironblock);
	}
	private static void Rage(Player p, Plugin plugin) {
		p.setMetadata("rage", new FixedMetadataValue(plugin, 0));
		stackRage(p, plugin);
		new BukkitRunnable() { // Buff timeout
			@Override
			public void run() {
				if (p.hasMetadata("rage")) {
					p.sendMessage("Your Smoldering Rage fades.");
					p.removeMetadata("rage", plugin);
				}
			}
		}.runTaskLater(plugin, 160);
	}
	public static int stackRage(Player pd, Plugin plugin) {
		// Returns the amount of rage = bonus damage
		pd.getWorld().playSound(pd.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1, 1);
		pd.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, pd.getEyeLocation(), 10, 1, 1, 1, 0);
		int rage = pd.getMetadata("rage").get(0).asInt() + 1;
		pd.setMetadata("rage", new FixedMetadataValue(plugin, rage));
		new BukkitRunnable() { // Reset buff timeout
			@Override
			public void run() {
				if (pd.getMetadata("rage").get(0).asInt() == rage) {
					pd.sendMessage("Your Smoldering Rage fades.");
					pd.removeMetadata("rage", plugin);
				}
			}
		}.runTaskLater(plugin, 20);
		return rage;
	}
	private static void Resolve(Player p, Plugin plugin) {
		p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getEyeLocation(), 20, 0.5, 0, 0.5, glowstone);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 1, 1);
		p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 160, (int)(2-(p.getHealth()/10))));
	}
	private static void Fury(Player p, Plugin plugin) {
		p.setMetadata("fury", new FixedMetadataValue(plugin, true));
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1, 1);
		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				p.getWorld().spawnParticle(Particle.LAVA, p.getLocation(), 20, 1, 1, 1, 0.1);
			}
		}.runTaskTimer(plugin, 0, 5);

		new BukkitRunnable() {
			@Override
			public void run() {
				task.cancel();
				p.sendMessage("Your Tempered Fury fades.");
				p.removeMetadata("fury", plugin);
			}
		}.runTaskLater(plugin, 160);
	}
	public static void healFury(Player pd) {
		pd.getWorld().spawnParticle(Particle.HEART, pd.getEyeLocation(), 4, 1, 1, 1, 0.2);
		pd.setHealth(pd.getHealth() + (int)(3-(pd.getHealth()/7)));
	}
	//
	private static void Empower(Player p, Plugin plugin, int type) {
		p.setMetadata("empower", new FixedMetadataValue(plugin, type));
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
		p.getWorld().spawnParticle(Particle.CLOUD, p.getEyeLocation(), 8, 0.5, 0.5, 0.5, 0);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (p.hasMetadata("empower")) {
					p.getWorld().spawnParticle(Particle.CLOUD, p.getEyeLocation(), 1, 0.5, 0.5, 0.5, 0);
				}
				else {
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (p.hasMetadata("empower")) {
					p.removeMetadata("empower", plugin);
				}
			}
		}.runTaskLater(plugin, 160);
	}
	private static void Daze(Player p, Plugin plugin) {
		Empower(p, plugin, 0);
	}
	public static void applyDaze(LivingEntity ev) {
		for (Location l:Geometry.getCirclePoints(ev.getEyeLocation(), Geometry.Plane.XZ, 1, 8)) {
			ev.getWorld().spawnParticle(Particle.NOTE, l, 1, 0, 0, 0, 0.2);
		}
		ev.addPotionEffect(dazeEffect1);
		ev.addPotionEffect(dazeEffect2);
	}
	private static void Cripple(Player p, Plugin plugin) {
		Empower(p, plugin, 1);
	}
	public static void applyCripple(LivingEntity ev) {
		ev.getWorld().spawnParticle(Particle.BLOCK_DUST, ev.getEyeLocation(), 20, 0.5, 0.5, 0.5, 0.2, obsblock);
		ev.addPotionEffect(crippleEffect);
	}
	private static void Shatter(Player p, Plugin plugin) {
		Empower(p, plugin, 2);	
	}
	public static void applyShatter(LivingEntity ev, Vector v) {
		ev.getWorld().playSound(ev.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
		ev.setVelocity(v);
	}
	private static void Bleed(Player p, Plugin plugin) {
		Empower(p, plugin, 3);	
	}
	public static void applyBleed(LivingEntity ev, Player pd, Plugin plugin) {
		ev.getWorld().spawnParticle(Particle.BLOCK_DUST, ev.getEyeLocation(), 20, 0.5, 0.5, 0.5, 0.2, redblock);
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				ev.getWorld().spawnParticle(Particle.BLOCK_DUST, ev.getEyeLocation(), 10, 0.5, 0.5, 0.5, 0.2, redblock);
				ev.damage(2, pd);
			}
		}.runTaskTimer(plugin, 0, 10);
		new BukkitRunnable() {
			@Override
			public void run() {
				bt.cancel();
			}
		}.runTaskLater(plugin, 60);
	}
	
	private static void Spellshield(Player p, Plugin plugin, int upgrade) {
		UUID uid = p.getUniqueId();
		p.setMetadata("spellshield", new FixedMetadataValue(plugin, upgrade));
		shields.put(uid, Utils.rotateItems(p, Material.PRISMARINE_SHARD, "guard",p.getEyeHeight()/2, 160, 3, plugin));

		new BukkitRunnable() {
			@Override
			public void run() {
				p.removeMetadata("spellshield", plugin);
				shields.remove(uid);
			}
		}.runTaskLater(plugin, 160);
	}
	private static void Ward(Player p, Plugin plugin) {
		Spellshield(p, plugin, 0);
	}
	private static void Blade(Player p, Plugin plugin) {
		Spellshield(p, plugin, 1);
	}
	public static void applySpellblade(LivingEntity ev, Player pd) {
		Random r = new Random();
		ev.addPotionEffect(new PotionEffect(pNerfs.get(r.nextInt(pNerfs.size())), 80, 1));
		ev.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, ev.getEyeLocation(), 20, 0.5, 0.5, 0.5, 0.5);
	}
	private static void Bind(Player p, Plugin plugin) {
		Spellshield(p, plugin, 2);
	}
	private static void Thief(Player p, Plugin plugin) {
		Spellshield(p, plugin, 3);
	}
	public static void applySilence(LivingEntity ev, Player pd, Plugin plugin) {
		ev.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, ev.getEyeLocation(), 20, 0.5, 0.5, 0.5, 0.5);
		if (ev instanceof Player) {
			Player pv = (Player) ev;
			pv.setMetadata("silenced", new FixedMetadataValue(plugin, true));
			new BukkitRunnable() {
				public void run() {
					pv.removeMetadata("silenced", plugin);
				}
			}.runTaskLater(plugin, 100);
		}
		else {
			ev.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 9));
		}
	}
	public static void consumeShield(Player p, Plugin plugin) {
		if (shields.containsKey(p.getUniqueId())) {
			List<Item> blocks = shields.get(p.getUniqueId());
			if (blocks.size() > 0) {
				Item pm = blocks.remove(0);
				p.getWorld().spawnParticle(Particle.ITEM_CRACK, pm.getLocation(), 20, 0, 0, 0, 0.1, new ItemStack(Material.PRISMARINE_SHARD));
				pm.remove();
			}
			if (blocks.size() == 0) {
				int type = p.getMetadata("spellshield").get(0).asInt();
				p.removeMetadata("spellshield", plugin);
				p.getWorld().spawnParticle(Particle.REDSTONE, p.getEyeLocation(), 20, 0.3, 0.3, 0.3, 0.5, pinkDust);
				if (type == 0) {
					p.addPotionEffect(duelistRegen);
				}
				else if (type == 1) {
					p.setMetadata("spellblade", new FixedMetadataValue(plugin, true));
					new BukkitRunnable() {
						@Override
						public void run() {
							if (p.hasMetadata("spellblade")) {
								p.sendMessage("Your Spellblade fades.");
								p.removeMetadata("spellblade", plugin);
							}
						}
					}.runTaskLater(plugin, 100);
				}
				else if (type == 2) {
					p.addPotionEffect(duelistSpeed);
				}
				else if (type == 3) {
					p.setMetadata("silencer", new FixedMetadataValue(plugin, true));
					new BukkitRunnable() {
						@Override
						public void run() {
							if (p.hasMetadata("silencer")) {
								p.sendMessage("Your Silencer fades.");
								p.removeMetadata("silencer", plugin);
							}
						}
					}.runTaskLater(plugin, 100);
				}
			}
		}
	}
}
