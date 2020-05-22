package com.gmail.kvkkuo.JotS.commands;

import com.gmail.kvkkuo.JotS.JotS;
import com.gmail.kvkkuo.JotS.bosses.*;
import com.gmail.kvkkuo.JotS.utils.Geometry;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ParticleCommand implements CommandExecutor {

	public JotS plugin;

	public ParticleCommand(JotS plugin){
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("particle")) {
			if (args.length > 0) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					Particle particle = Particle.valueOf(args[0]);
					Location location = Geometry.getBlockRelative(p.getEyeLocation(), 2, 0, 0);
					int count = Integer.parseInt(args[1]);
					double offsetX = Double.parseDouble(args[2]);
					double offsetY = Double.parseDouble(args[3]);
					double offsetZ = Double.parseDouble(args[4]);
					double speed = Double.parseDouble(args[5]);
					if (args.length == 6) {
						p.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
					}
				} else {
					sender.sendMessage("Cannot execute from console!");
					return false;
				}
				return true;
			}
		}
		return false; 
	}
}