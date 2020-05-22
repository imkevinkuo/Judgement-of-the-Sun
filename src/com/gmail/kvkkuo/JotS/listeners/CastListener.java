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
	    	for (int i = 0; i < 6; i++) {
				if (faction == i && m.equals(JotS.selectors[i])) {
					if (p.hasMetadata("silenced")) {
						p.sendMessage("You cannot cast spells while Silenced!");
						return;
					}
					switch (faction) {
						case 0: cds[spl] = Duelist.cast(p, spl, cds[spl], ups[spl], plugin);
								break;
						case 1: cds[spl] = Raider.cast(p, spl, cds[spl], ups[spl], plugin);
								break;
						case 2: cds[spl] = Assassin.cast(p, spl, cds[spl], ups[spl], plugin);
								break;
						case 3: cds[spl] = Guardian.cast(p, spl, cds[spl], ups[spl], plugin);
								break;
						case 4: cds[spl] = Witherknight.cast(p, spl, cds[spl], ups[spl], plugin);
								break;
						case 5: cds[spl] = Paladin.cast(p, spl, cds[spl], ups[spl], plugin);
								break;
					}
				}
			}
	    }
	    // CYCLE
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
	    	// Class Spell Cycling
			for (int i = 0; i < 6; i++) {
				if (faction == i && m.equals(JotS.selectors[i])) {
					spl = (spl+1)%4;
				}
			}
		}
		plugin.spell.put(id, spl);
	    CastListener.updateCycler(p, plugin.factions.get(id), cds[spl], ups[spl], spl);
	    p.updateInventory();
    }

	public static void updateCycler(Player p, Integer faction, Integer cd, Integer upg, Integer spl) {
		ItemStack item = p.getInventory().getItemInMainHand();
		Material m = item.getType();
		if (!m.equals(Material.AIR)) {
			ItemMeta im = item.getItemMeta();
			ChatColor g = ChatColor.GREEN;
			String dispname = "";
			for (int i = 0; i < 6; i++) {
				if (faction == i && m.equals(JotS.selectors[i])) {
					switch (faction) {
						case 0: dispname = Duelist.SKILLS[spl*8 + upg*2];
								break;
						case 1: dispname = Raider.SKILLS[spl*8 + upg*2];
								break;
						case 2: dispname = Assassin.SKILLS[spl*8 + upg*2];
								break;
						case 3: dispname = Guardian.SKILLS[spl*8 + upg*2];
								break;
						case 4: dispname = Witherknight.SKILLS[spl*8 + upg*2];
								break;
						case 5: dispname = Paladin.SKILLS[spl*8 + upg*2];
								break;
					}
				}
			}
			if (!dispname.equals("")) {
				if (cd > 0) {
					g = ChatColor.RED;
					dispname = dispname + " " + cd;
				}
				im.setDisplayName(g + dispname);
				item.setItemMeta(im);
			}
		}
	}
}