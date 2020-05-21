package com.gmail.kvkkuo.JotS.listeners;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.kvkkuo.JotS.JotS;
import com.gmail.kvkkuo.JotS.classes.Assassin;
import com.gmail.kvkkuo.JotS.classes.Duelist;
import com.gmail.kvkkuo.JotS.classes.Guardian;
import com.gmail.kvkkuo.JotS.classes.Paladin;
import com.gmail.kvkkuo.JotS.classes.Raider;
import com.gmail.kvkkuo.JotS.classes.Witherknight;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
 
public class CastListener implements Listener {
	
	public JotS plugin;
	
	public CastListener(JotS plugin){
		this.plugin = plugin;
	}
	
    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
    	Player p = event.getPlayer();
		UUID id = p.getUniqueId();
		Integer faction = plugin.factions.get(id);
		Integer spl = plugin.spell.get(id);
		Material m = p.getInventory().getItemInMainHand().getType();
		Integer[] cds = plugin.cooldowns.get(id);
		Integer[] ups = plugin.upgrades.get(id);
		// Casting Spells
	    if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
	    	if (faction == 0 && m.equals(Material.IRON_SWORD)) {
	    		if (p.hasMetadata("silenced")) {
	    			p.sendMessage("You cannot cast spells while Silenced!");
	    			return;
	    		}
				cds[spl] = Duelist.cast(p, spl, cds[spl], ups[spl], plugin);
	    	}
	    	if (faction == 1 && m.equals(Material.BLAZE_POWDER)) {
	    		if (p.hasMetadata("silenced")) {
	    			p.sendMessage("You cannot cast spells while Silenced!");
	    			return;
	    		}
				cds[spl] = Raider.cast(p, spl, cds[spl], ups[spl], plugin);
			}
	    	if (faction == 2 && m.equals(Material.FEATHER)) {
	    		if (p.hasMetadata("silenced")) {
	    			p.sendMessage("You cannot cast spells while Silenced!");
	    			return;
	    		}
				cds[spl] = Assassin.cast(p, spl, cds[spl], ups[spl], plugin);
	    	}
	    	if (faction == 3 && m.equals(Material.CLAY_BALL)) {
	    		if (p.hasMetadata("silenced")) {
	    			p.sendMessage("You cannot cast spells while Silenced!");
	    			return;
	    		}
				cds[spl] = Guardian.cast(p, spl, cds[spl], ups[spl], plugin);
			}
	    	if (faction == 4 && m.equals(Material.BONE)) {
	    		if (p.hasMetadata("silenced")) {
	    			p.sendMessage("You cannot cast spells while Silenced!");
	    			return;
	    		}
				cds[spl] = Witherknight.cast(p, spl, cds[spl], ups[spl], plugin);
			}
	    	if (faction == 5 && m.equals(Material.GLOWSTONE_DUST)) {
	    		if (p.hasMetadata("silenced")) {
	    			p.sendMessage("You cannot cast spells while Silenced!");
	    			return;
	    		}
				cds[spl] = Paladin.cast(p, spl, cds[spl], ups[spl], plugin);
			}
	    }
	    // CYCLE
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
	    	// Class Spell Cycling
	    	if (faction == 0 && m.equals(Material.IRON_SWORD)) {
	    		spl = (spl+1)%4;
			}
	    	if (faction == 1 && m.equals(Material.BLAZE_POWDER)) {
				spl = (spl+1)%4;
			}
	    	if (faction == 2 && m.equals(Material.FEATHER)) {
				spl = (spl+1)%4;
			}
	    	if (faction == 3 && m.equals(Material.CLAY_BALL)) {
				spl = (spl+1)%4;
			}
	    	if (faction == 4 && m.equals(Material.BONE)) {
				spl = (spl+1)%4;
			}
	    	if (faction == 5 && m.equals(Material.GLOWSTONE_DUST)) {
				spl = (spl+1)%4;
			}
		}
		plugin.spell.put(id, spl);
	    CastListener.updateCycler(p, plugin.factions.get(id), cds[spl], ups[spl], spl);
	    p.updateInventory();
    }
    
    public static void updateCycler(Player p, Integer faction, Integer cd, Integer upg, Integer spl) {
		ItemStack i = p.getInventory().getItemInMainHand();
		Material t = i.getType();
		if (!t.equals(Material.AIR)) {
			ItemMeta im = i.getItemMeta();
			ChatColor g = ChatColor.GREEN;
			String dispname = "";
			if (faction == 0 && t.equals(Material.IRON_SWORD)) {
				dispname = Duelist.SKILLS[spl*8 + upg*2];
			}
			if (faction == 1 && t.equals(Material.BLAZE_POWDER)) {
				dispname = Raider.SKILLS[spl*8 + upg*2];
			}
			if (faction == 2 && t.equals(Material.FEATHER)) {
				dispname = Assassin.SKILLS[spl*8 + upg*2];
			}
			if (faction == 3 && t.equals(Material.CLAY_BALL)) {
				dispname = Guardian.SKILLS[spl*8 + upg*2];
			}
			if (faction == 4 && i.getType().equals(Material.BONE)) {
				dispname = Witherknight.SKILLS[spl*8 + upg*2];
			}
			if (faction == 5 && t.equals(Material.GLOWSTONE_DUST)) {
				dispname = Paladin.SKILLS[spl*8 + upg*2];
			}
			if (!dispname.equals("")) {
				if (cd > 0) {
					g = ChatColor.RED;
					dispname = dispname + " " + cd;
				}
				im.setDisplayName(g + dispname);
				i.setItemMeta(im);
			}
		}
	}
}