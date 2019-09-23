package com.gmail.kvkkuo.Elementals.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RayTrace {

	Vector direction;
	Location currentloc;
	int maxtimes, times;
	
	public RayTrace(Location start, Vector direction, double maxdistance, double magnitude) {
		/* magnitude: every X units */
        this.currentloc = start;
        this.direction = direction.normalize().multiply(magnitude);
        this.maxtimes = (int) ((int) maxdistance/magnitude);
        this.times = 0;
    }
	
	public RayTrace(Player p, double maxdistance, double magnitude) {
		this(p.getEyeLocation(), p.getLocation().getDirection(), maxdistance, magnitude);
	}
	
	public Location next() {
    	if (this.hasNext()) {
    		times++;
    		currentloc.add(direction);
        	return currentloc;
    	}
    	else {
    		return null;
    	}
    }
    
    public boolean hasNext() {
		return times < maxtimes;
    }
}
