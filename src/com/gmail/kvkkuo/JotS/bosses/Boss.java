package com.gmail.kvkkuo.JotS.bosses;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Boss {
	
	public static ItemStack[] hunterarmor = new ItemStack[4];
	public static ItemStack hunterbow;
	public static ItemStack huntersword;
	public static ItemStack[] warlockarmor = new ItemStack[4];
	public static ItemStack warlockbow;
	public static ItemStack minionbow;
	public static ItemStack[] ninjaarmor = new ItemStack[4];
	public static ItemStack ninjasword;
	public static ItemStack[] roguearmor = new ItemStack[4];
	public static ItemStack roguesword;
	public static ItemStack[] shamanarmor = new ItemStack[4];
	public static ItemStack shamanwand;
	public static ItemStack[] marauderarmor = new ItemStack[4];
	public static ItemStack marauderaxe;

	public static ItemStack attrItem(ItemStack item, Attribute[] attrs, double[] amounts, Operation[] ops, EquipmentSlot[] slots) {
		ItemMeta m  = item.getItemMeta();
		for (int i = 0; i < attrs.length; i++) {
	    	m.addAttributeModifier(attrs[i], new AttributeModifier(UUID.randomUUID(), "myAttr", amounts[i], ops[i], slots[i]));
		}
    	item.setItemMeta(m);
    	return item;
	}
	
	public static ItemStack namedItem(ItemStack item, String name, List<String> lore) {
    	ItemMeta im = item.getItemMeta();
    	im.setDisplayName(name);
    	im.setLore(lore);
    	item.setItemMeta(im);
    	return item;
	}
	
	public static ItemStack enchantedItem(ItemStack item, Enchantment[] enchants, int[] levels) {
		for (int i = 0; i < enchants.length; i++) {
			item.addUnsafeEnchantment(enchants[i], levels[i]);
		}
		return item;
	}
	
	// Use only with type = Leather armor
	public static ItemStack coloredLeather(Material type, Color c) {
		ItemStack i = new ItemStack(type);
    	LeatherArmorMeta m = (LeatherArmorMeta) i.getItemMeta();
    	m.setColor(c);
    	i.setItemMeta(m);
		return i;
	}
	
	public static void CreateItems() {
// Hunter -------------------------------------------------------------
    	List<String> hunterlore = Arrays.asList(ChatColor.RESET + "You looted this from a Hunter.");
    	// Set
    	hunterarmor[0] = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
    	hunterarmor[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
    	hunterarmor[2] = coloredLeather(Material.LEATHER_CHESTPLATE, Color.SILVER);
    	hunterarmor[3] = new ItemStack(Material.CHAINMAIL_HELMET, 1);
    	hunterbow = enchantedItem(
    		namedItem(new ItemStack(Material.BOW, 1),
    			ChatColor.DARK_RED + "Hunter's Bow", hunterlore),
    		new Enchantment[] {Enchantment.ARROW_DAMAGE}, 
    		new int[] {2});
    	huntersword = enchantedItem(
    		namedItem(new ItemStack(Material.IRON_SWORD, 1),
    			ChatColor.DARK_RED + "Hunter's Knife", hunterlore),
    		new Enchantment[] {Enchantment.DAMAGE_ALL, Enchantment.LOOT_BONUS_MOBS}, 
    		new int[] {2, 2});

// Warlock ---------------------------------------------------
    	List<String> warlocklore = Arrays.asList(ChatColor.RESET + "You looted this from a Warlock.");

    	// Set
    	warlockarmor[0] = coloredLeather(Material.LEATHER_BOOTS, Color.MAROON);
    	warlockarmor[1] = coloredLeather(Material.LEATHER_LEGGINGS, Color.MAROON);
    	warlockarmor[2] = coloredLeather(Material.LEATHER_CHESTPLATE, Color.MAROON);
    	warlockarmor[3] = enchantedItem(
    		namedItem(new ItemStack(Material.DIAMOND_HELMET, 1),
    			ChatColor.DARK_RED + "Warlock's Crown", warlocklore), 
    		new Enchantment[] {Enchantment.DURABILITY, Enchantment.PROTECTION_ENVIRONMENTAL},
    		new int[] {2, 2});
    	warlockbow = enchantedItem(
    		namedItem(new ItemStack(Material.BOW, 1),
    			ChatColor.DARK_RED + "Warlock's Bow", warlocklore),
    		new Enchantment[] {Enchantment.ARROW_DAMAGE},
    		new int[] {2});
    	minionbow = enchantedItem(
    		new ItemStack(Material.BOW, 1),
    		new Enchantment[] {Enchantment.ARROW_DAMAGE},
    		new int[] {2});
// Rogue ------------------------------------------------
    	List<String> roguelore = Arrays.asList(ChatColor.DARK_RED + "You looted this from a Rogue.");
    	roguearmor[0] = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
    	roguearmor[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
    	roguearmor[2] = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
    	roguearmor[3] = enchantedItem(
    		namedItem(new ItemStack(Material.CHAINMAIL_HELMET, 1),
    			ChatColor.DARK_RED + "Rogue's Sandals", roguelore),
    		new Enchantment[] {Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FALL, 
    							   Enchantment.PROTECTION_PROJECTILE, Enchantment.PROTECTION_EXPLOSIONS},
    		new int[] {1, 1, 1, 1});
    	roguesword = enchantedItem(
        	namedItem(new ItemStack(Material.GOLDEN_SWORD, 1), 
            	ChatColor.DARK_RED + "Rogue's Dirk", roguelore),
           	new Enchantment[] {Enchantment.DAMAGE_ALL, Enchantment.DURABILITY}, 
           	new int[] {5, 5});;
// Zombie Shaman ---------------------------------------------------
    	List<String> shamanlore = Arrays.asList(ChatColor.DARK_RED + "You looted this from a Shaman.");
    	shamanarmor[0] = new ItemStack(Material.LEATHER_BOOTS, 1);;
    	shamanarmor[1] = new ItemStack(Material.LEATHER_LEGGINGS, 1);
    	shamanarmor[2] = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
    	shamanarmor[3] = enchantedItem(
			namedItem(new ItemStack(Material.LEATHER_HELMET, 1),
					ChatColor.RED + "Shaman's Hood", shamanlore),
			new Enchantment[] {Enchantment.DURABILITY, Enchantment.PROTECTION_ENVIRONMENTAL},
			new int[] {3, 3});
    	shamanwand = enchantedItem(
			namedItem(new ItemStack(Material.STICK, 1),
				ChatColor.DARK_RED + "Shaman's Wand", shamanlore),
			new Enchantment[] {Enchantment.KNOCKBACK},
			new int[] {2});
// Ninja -------------------------------------------------------------
    	List<String> ninjalore = Arrays.asList(ChatColor.RESET + "You looted this from a Ninja.");
    	ninjaarmor[0] = new ItemStack(Material.IRON_BOOTS, 1);
    	ninjaarmor[1] = new ItemStack(Material.IRON_LEGGINGS, 1);
    	ninjaarmor[2] = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
    	ninjaarmor[3] = new ItemStack(Material.IRON_HELMET, 1);
    	ninjasword = attrItem(namedItem(new ItemStack(Material.SHEARS, 1), 
    			ChatColor.RED + "Ninja's Daggers", ninjalore),
    			new Attribute[] {Attribute.GENERIC_ATTACK_SPEED},
    			new double[] {0.3},
    			new Operation[] {Operation.ADD_NUMBER},
    			new EquipmentSlot[] {EquipmentSlot.OFF_HAND});
    	
// Marauder ----------------------------------------------------------
    	List<String> marauderlore = Arrays.asList(ChatColor.DARK_RED + "You looted this from a Marauder.");
    	// Set Armor
		marauderarmor[0] = new ItemStack(Material.LEATHER_BOOTS, 1);
		marauderarmor[1] = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		marauderarmor[2] = enchantedItem(
			namedItem(new ItemStack(Material.IRON_CHESTPLATE, 1),
				ChatColor.DARK_RED + "Marauder's Chestguard", marauderlore),
			new Enchantment[] {Enchantment.PROTECTION_PROJECTILE},
			new int[] {5});
		marauderarmor[3] = new ItemStack(Material.LEATHER_HELMET, 1);
		marauderaxe = enchantedItem(
			namedItem(new ItemStack(Material.DIAMOND_AXE, 1),
	    		ChatColor.DARK_RED + "Marauder's Axe", marauderlore),
	    	new Enchantment[] {Enchantment.DAMAGE_ALL, Enchantment.DIG_SPEED},
	    	new int[] {2, 4});
	}
	
	public static void updateHealth(LivingEntity boss, Plugin plugin) {
		String tag = getBossName(boss) + " [" + ChatColor.RED;
		int bars = (int) (20*(boss.getHealth() / boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
		for (int i = 0; i < bars; i++) {
			tag += '\u254F';
		}
		tag += ChatColor.GRAY;
		for (int i = bars; i < 20; i++) {
			tag += '\u254F';
		}
		tag += ChatColor.WHITE + "]";
		boss.setCustomName(tag);
	}

	public static void spawnActions(LivingEntity boss, String name, Plugin plugin) {
		boss.setMetadata("boss", new FixedMetadataValue(plugin, name));
		boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
    	boss.setHealth(100);
    	updateHealth(boss, plugin);
		boss.setCustomNameVisible(true);
		boss.setRemoveWhenFarAway(false);
		boss.setCanPickupItems(false);
		boss.getEquipment().setHelmetDropChance(0);
		boss.getEquipment().setChestplateDropChance(0);
		boss.getEquipment().setLeggingsDropChance(0);
		boss.getEquipment().setBootsDropChance(0);
		boss.getEquipment().setItemInMainHandDropChance(0);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
        	if (p.getLocation().distance(boss.getLocation()) <= 30) {
	        	p.sendMessage("The " + getBossName(boss) + " has awakened!");
        	}
		}
        new BukkitRunnable() {
			@Override
			public void run() {
				updateHealth(boss, plugin);
				if (boss.isDead()) {
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (boss.getKiller() != null) {
							p.sendMessage("The " + getBossName(boss) + " has been slain by " + boss.getKiller().getName() + "!");
						}
						else {
							p.sendMessage("The " + getBossName(boss) + " has been slain!");
						}
						Boss.Death(boss, plugin);
		    		}
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 10, 10);
    }
	
	public static void Death(LivingEntity boss, Plugin plugin) {
		if (getBossName(boss).contains("Warlock")) {
			Warlock.RemoveItems(boss, plugin);
		}
	}
	
	public static String getBossName(LivingEntity boss) {
		return boss.getMetadata("boss").get(0).asString();
	}

	public static String getRace(EntityType et) {
		if (et.equals(EntityType.SKELETON)) {
			return "Skeleton";
		}
		if (et.equals(EntityType.ZOMBIE)) {
			return "Zombie";
		}
		if (et.equals(EntityType.PIG_ZOMBIE)) {
			return "Pigman";
		}
		return "";
	}

	public static void Ready(LivingEntity boss, int time, Particle p, Plugin plugin) {
		boss.setMetadata("noknockback", new FixedMetadataValue(plugin, true));
		boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 10, 1);
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, 8));
		boss.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30, 8));
		boss.getWorld().spawnParticle(p, boss.getEyeLocation(), 10, 1, 0, 1);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				boss.getWorld().spawnParticle(p, boss.getEyeLocation(), 10, 1, 0, 1, 0);

			}
		},	(time/2));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				boss.getWorld().spawnParticle(p, boss.getEyeLocation(), 10, 1, 0, 1, 0);
				boss.removeMetadata("noknockback", plugin);
			}
		},	(time));
	}
	
	public static Location bodyLocation(LivingEntity le) {
		return le.getLocation().add(0, (le.getEyeHeight() / 2), 0);
	}
}
