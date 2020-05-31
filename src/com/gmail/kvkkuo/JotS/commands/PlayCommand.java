package com.gmail.kvkkuo.JotS.commands;

import com.gmail.kvkkuo.JotS.JotS;
import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.Geometry;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayCommand implements CommandExecutor {

	public JotS plugin;

	public PlayCommand(JotS plugin){
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("play")) {
			if (args.length > 0) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (args.length == 1) {
						p.sendMessage("/play firework <0/1> <0/1> <0/1>");
						p.sendMessage("/play particle type count x y z speed");
						p.sendMessage("/play sound sound");
					}
					if (args[0].equals("firework")) {
						boolean trail = false;
						boolean fade = false;
						boolean flicker = false;
						if (args[1].equals("1")) {
							trail = true;
						}
						if (args[2].equals("1")) {
							fade = true;
						}
						if (args[3].equals("1")) {
							flicker = true;
						}
						FireworkPlayer.fire(p.getLocation(),
								FireworkEffect.Type.BALL_LARGE,
								Color.WHITE,
								trail,
								fade,
								flicker);
					}
					else if (args[0].equals("particle")){
						Particle particle = Particle.valueOf(args[1]);
						Location location = p.getEyeLocation().add(p.getLocation().getDirection().normalize());
						int count = Integer.parseInt(args[2]);
						double offsetX = Double.parseDouble(args[3]);
						double offsetY = Double.parseDouble(args[4]);
						double offsetZ = Double.parseDouble(args[5]);
						double speed = Double.parseDouble(args[6]);
						if (args.length == 7) {
							p.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
						}
					}
					else if (args[0].equals("sound")) {
						Sound sound = Sound.valueOf(args[1]);
						p.getWorld().playSound(p.getLocation(), sound, 1, 1);
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