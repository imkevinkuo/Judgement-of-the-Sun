package com.gmail.kvkkuo.JotS.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.gmail.kvkkuo.JotS.classes.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
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
    	p.removeMetadata("spirit", plugin);
    	p.removeMetadata("kinetic", plugin);
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
	public static void applyNearby(Location l, LivingEntity ex, int x, int y, int z, applyNearbyOperator op) {
		for (Entity e:l.getWorld().getNearbyEntities(l, x, y, z)) {
			if (!e.equals(ex) && e instanceof LivingEntity) {
				op.applyNearby((LivingEntity) e);
			}
		}
	}

	public static void applyNearbyPlayers(Location l, int r, applyNearbyOperator op) {
		applyNearbyPlayers(l, r, r, r, op);
	}

	public static void applyNearbyPlayers(Location l, int x, int y, int z, applyNearbyOperator op) {
		for (Entity e:l.getWorld().getNearbyEntities(l, x, y, z)) {
			if (e instanceof Player) {
				op.applyNearby((LivingEntity) e);
			}
		}
	}

	public static LivingEntity getNearestEntity(Location l, ArrayList<LivingEntity> ex, int x, int y, int z) {
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

	public static Player getNearestPlayer(Location l, int x, int y, int z) {
		for (Entity e:l.getWorld().getNearbyEntities(l, x, y, z)) {
			if (e instanceof Player) {
				return (Player) e;
			}
		}
		return null;
	}
}
