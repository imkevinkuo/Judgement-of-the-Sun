package com.gmail.kvkkuo.JotS.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.gmail.kvkkuo.JotS.classes.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import org.bukkit.util.Vector;

public class Utils {

	public static String CSV_PATH = "/com/gmail/kvkkuo/JotS/csv/";

	public static String[] readSkillsFromCSV(String fileName) {
		String[] skillText = new String[32];
		int i = 0;

		try (InputStream resource = Utils.class.getResourceAsStream(CSV_PATH + fileName)) {
			List<String> doc = new BufferedReader(new InputStreamReader(resource,
					StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

			for (String line: doc) {
				for (String token : line.split("(\",)?\"")) {
					if (!token.isEmpty() && !token.startsWith("#")) {
						skillText[i++] = token;
					}
				}
			}

		} catch (IOException ex) {
			System.out.println("Could not find " + fileName);
			ex.printStackTrace();
		}

		return skillText;
	}
	
	public static void clearMetadata(Player p, Plugin plugin) {
    	p.removeMetadata("nofall", plugin);
    	p.removeMetadata("bsmash", plugin);
    	
    	p.removeMetadata("silenced", plugin);
    	p.removeMetadata("silencer", plugin);
    	p.removeMetadata("spellshield", plugin);
    	p.removeMetadata("spellblade", plugin);
    	p.removeMetadata("rage", plugin);
    	p.removeMetadata("fury", plugin);
    	
    	p.removeMetadata("illusion", plugin);
    	p.removeMetadata("deception", plugin);
    	p.removeMetadata("soul", plugin);
    	
    	p.removeMetadata("guard", plugin);
    	p.removeMetadata("divine", plugin);
    	
    	p.removeMetadata("spines", plugin);
    	p.removeMetadata("starve", plugin);
    	p.removeMetadata("kinetic", plugin);
	}

	public static List<Item> rotateItems(Player p, Material material, String itemName, int duration, double height, Plugin plugin) {
		List<Item> items = new ArrayList<>();
		for (int i = 0; i < 3; i ++) {
			int in = i;
			ItemStack is = new ItemStack(material);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(itemName + i + p.getName());
			is.setItemMeta(im);
			Item item = p.getWorld().dropItem(p.getEyeLocation(), is);
			item.setPickupDelay(Integer.MAX_VALUE);
			item.setGravity(false);
			items.add(item);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (item.isDead()) {this.cancel();}
					Location to = Geometry.getCirclePoint(p.getLocation().add(0, height,0), 1.5, (Math.PI*in*2/3) + (double) item.getTicksLived()/12);
					Location from = item.getLocation();
					item.setVelocity(to.subtract(from).toVector().multiply(0.2));
				}
			}.runTaskTimer(plugin, 1, 1);
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				for (Item item: items) {
					p.getWorld().spawnParticle(Particle.ITEM_CRACK, item.getLocation(), 20, 0, 0, 0, 0.1, new ItemStack(material));
					item.remove();
				}
			}
		},	(duration));

		return items;
	}
	
	public static void magicDamage(LivingEntity source, LivingEntity target, int amount, Plugin plugin) {
		if (target.hasMetadata("spellshield")) {
			Duelist.consumeShield((Player) target, plugin);
		}
		else {
			target.damage(amount, source);
		}
	}

	// Applies to LivingEntities besides ex.
	public static void applyNearby(Location l, LivingEntity ex, double x, double y, double z, applyNearbyOperator op) {
		for (Entity e:l.getWorld().getNearbyEntities(l, x, y, z)) {
			if (!e.equals(ex) && e instanceof LivingEntity) {
				op.applyNearby((LivingEntity) e);
			}
		}
	}

	public static void applyNearbyPlayers(Location l, int r, applyNearbyOperator op) {
		applyNearbyPlayers(l, r, r, r, op);
	}

	public static void applyNearbyPlayers(Location l, double x, double y, double z, applyNearbyOperator op) {
		for (Entity e:l.getWorld().getNearbyEntities(l, x, y, z)) {
			if (e instanceof Player) {
				op.applyNearby((LivingEntity) e);
			}
		}
	}

	public static LivingEntity getNearestEntity(Location l, ArrayList<LivingEntity> ex, double x, double y, double z) {
		for (Entity e:l.getWorld().getNearbyEntities(l, x, y, z)) {
			if (!ex.contains(e) && e instanceof LivingEntity) {
				return (LivingEntity) e;
			}
		}
		return null;
	}

	public static Player getNearestPlayer(Location l, int r) {
		return getNearestPlayer(l, r, r, r);
	}

	public static Player getNearestPlayer(Location l, double x, double y, double z) {
		for (Entity e:l.getWorld().getNearbyEntities(l, x, y, z)) {
			if (e instanceof Player) {
				return (Player) e;
			}
		}
		return null;
	}
}
