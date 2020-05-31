package com.gmail.kvkkuo.JotS.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.kvkkuo.JotS.classes.*;
import com.gmail.kvkkuo.JotS.utils.IconMenu;

public class UpgradeMenu {
	static ItemStack[] duelistItems = new ItemStack[]{
			new ItemStack(Material.IRON_SHOVEL),
			new ItemStack(Material.IRON_PICKAXE),
			new ItemStack(Material.IRON_AXE),
			new ItemStack(Material.IRON_HOE),
			new ItemStack(Material.IRON_INGOT),
			new ItemStack(Material.BRICK),
			new ItemStack(Material.GOLD_INGOT),
			new ItemStack(Material.NETHER_BRICK),
			new ItemStack(Material.POPPED_CHORUS_FRUIT),
			new ItemStack(Material.ROTTEN_FLESH),
			new ItemStack(Material.SHULKER_SHELL),
			new ItemStack(Material.BEETROOT_SOUP),
			new ItemStack(Material.CYAN_DYE),
			new ItemStack(Material.DIAMOND_SWORD),
			new ItemStack(Material.PRISMARINE_CRYSTALS),
			new ItemStack(Material.PRISMARINE_SHARD)
	};
	static ItemStack[] raiderItems = new ItemStack[]{
			new ItemStack(Material.FIRE_CHARGE),
			new ItemStack(Material.BLAZE_POWDER),
			new ItemStack(Material.COMPARATOR),
			new ItemStack(Material.ORANGE_DYE),
			Raider.coloredPotion("", Color.RED, Material.POTION),
			Raider.coloredPotion("", Color.BLACK, Material.POTION),
			Raider.coloredPotion("", Color.AQUA, Material.POTION),
			Raider.coloredPotion("", Color.GREEN, Material.POTION),
			new ItemStack(Material.LEATHER_BOOTS),
			new ItemStack(Material.RED_DYE),
			new ItemStack(Material.BEETROOT),
			new ItemStack(Material.FIREWORK_ROCKET),
			new ItemStack(Material.TRIPWIRE_HOOK),
			new ItemStack(Material.BLAZE_ROD),
			new ItemStack(Material.DEAD_BUSH),
			new ItemStack(Material.NAME_TAG),
	};
	static ItemStack[] assassinItems = new ItemStack[]{
			new ItemStack(Material.QUARTZ),
			new ItemStack(Material.STRING),
			new ItemStack(Material.ARROW),
			new ItemStack(Material.FIREWORK_STAR),
			new ItemStack(Material.FEATHER),
			new ItemStack(Material.LIGHT_GRAY_DYE),
			new ItemStack(Material.HOPPER),
			new ItemStack(Material.NETHER_STAR),
			new ItemStack(Material.PLAYER_HEAD),
			new ItemStack(Material.ZOMBIE_HEAD),
			new ItemStack(Material.SKELETON_SKULL),
			new ItemStack(Material.CREEPER_HEAD),
			new ItemStack(Material.ENDER_PEARL),
			new ItemStack(Material.COMPASS),
			new ItemStack(Material.LEAD),
			new ItemStack(Material.GLASS_BOTTLE),
	};
	static ItemStack[] guardianItems = new ItemStack[]{
			new ItemStack(Material.WHEAT_SEEDS),
			new ItemStack(Material.PUMPKIN_SEEDS),
			new ItemStack(Material.MELON_SEEDS),
			new ItemStack(Material.BEETROOT_SEEDS),
			new ItemStack(Material.GREEN_DYE),
			new ItemStack(Material.CARROTS),
			new ItemStack(Material.EMERALD),
			new ItemStack(Material.GREEN_STAINED_GLASS_PANE),
			new ItemStack(Material.RABBIT_HIDE),
			new ItemStack(Material.BLAZE_SPAWN_EGG),
			new ItemStack(Material.GOLDEN_HORSE_ARMOR),
			new ItemStack(Material.DIAMOND_HORSE_ARMOR),
			new ItemStack(Material.GLISTERING_MELON_SLICE),
			new ItemStack(Material.GUNPOWDER),
			new ItemStack(Material.WHEAT),
			new ItemStack(Material.ROSE_BUSH)
	};
	static ItemStack[] witherItems = new ItemStack[]{
			new ItemStack(Material.CHORUS_FRUIT),
			new ItemStack(Material.CHORUS_FLOWER),
			new ItemStack(Material.END_CRYSTAL),
			new ItemStack(Material.MUSIC_DISC_11),
			new ItemStack(Material.BLACK_SHULKER_BOX),
			new ItemStack(Material.INK_SAC),
			new ItemStack(Material.IRON_HORSE_ARMOR),
			new ItemStack(Material.WITHER_SKELETON_SPAWN_EGG),
			new ItemStack(Material.WOODEN_SHOVEL),
			new ItemStack(Material.WOODEN_SWORD),
			new ItemStack(Material.STONE_SWORD),
			new ItemStack(Material.STONE_SHOVEL),
			new ItemStack(Material.END_PORTAL_FRAME),
			new ItemStack(Material.NETHER_QUARTZ_ORE),
			new ItemStack(Material.IRON_BARS),
			new ItemStack(Material.OBSIDIAN),
	};
	static ItemStack[] paladinItems = new ItemStack[]{
			new ItemStack(Material.GOLDEN_BOOTS),
			new ItemStack(Material.GOLDEN_AXE),
			new ItemStack(Material.GOLDEN_APPLE),
			new ItemStack(Material.TOTEM_OF_UNDYING),
			new ItemStack(Material.GLOWSTONE_DUST),
			new ItemStack(Material.GOLDEN_CARROT),
			new ItemStack(Material.YELLOW_DYE),
			new ItemStack(Material.SUNFLOWER),
			new ItemStack(Material.SNOWBALL),
			new ItemStack(Material.ICE),
			new ItemStack(Material.LIGHT_BLUE_DYE),
			new ItemStack(Material.LAPIS_LAZULI),
			new ItemStack(Material.PINK_DYE),
			new ItemStack(Material.MAGENTA_DYE),
			new ItemStack(Material.MAGMA_CREAM),
			new ItemStack(Material.REDSTONE),
	};
	static ItemStack[][] items = new ItemStack[][]{duelistItems, raiderItems, assassinItems, guardianItems, witherItems, paladinItems};
	static String[][] skills = new String[][]{Duelist.SKILLS, Raider.SKILLS, Assassin.SKILLS, Guardian.SKILLS, Witherknight.SKILLS, Paladin.SKILLS};

	public static IconMenu DisplaySkills(IconMenu inv, Integer faction, Integer[] upgrades) {
		String[] skilld = skills[faction];
		ItemStack[] itemd = items[faction];
		for (int skill = 0; skill < 4; skill++) {
			int up = upgrades[skill];
			String[] lore = new String[16];
			for (int u = 0; u < 4; u++) {
				ChatColor color = ChatColor.GRAY;
				if (u == up) {
					color = ChatColor.GOLD;
				}
				lore[u*4] = color + skilld[skill*8 + u*2]; // Name
				String[] skilldesc = splitLine(skilld[skill*8 + u*2 + 1]);
				lore[u*4 + 1] = color + skilldesc[0];
				lore[u*4 + 2] = color + skilldesc[1];
				lore[u*4 + 3] = ""; // Blank line
			}
			inv.setOption(skill*2, itemd[(skill*4)+up], " ", lore);
		}
		inv.setOption(8, new ItemStack(Material.MINECART), ChatColor.WHITE + "Unequip Shards", ChatColor.GOLD + "Unequips all shards.");
		return inv;
	}
	
	public static String[] splitLine(String s) {
		if (s.length() < 35) {
			return new String[] {s, ""};
		}
		
		int middle = (int)s.length()/2;
		int left = 0, right = 0;
		while (s.charAt(middle - left) != ' ') {
			left++;
		}
		while (s.charAt(middle + right) != ' ') {
			right++;
		}
		if (left < right) {
			middle -= left;
		}
		else {
			middle += right;
		}
		return new String[] {s.substring(0, middle), s.substring(middle+1, s.length())};
	}
	public static ItemStack getShard(Integer level) {
		ItemStack is = new ItemStack(Material.DIAMOND);
		ItemMeta im = is.getItemMeta();
		if (level == 1) {
			im.setDisplayName("Alpha Shard");
    	}
    	if (level == 2) {
    		im.setDisplayName("Gamma Shard");
    	}
    	if (level == 3) {
    		im.setDisplayName("Delta Shard");
    	}
    	is.setItemMeta(im);
		return is;
	}
}
