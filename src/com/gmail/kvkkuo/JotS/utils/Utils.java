package com.gmail.kvkkuo.JotS.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;

import com.gmail.kvkkuo.JotS.classes.Duelist;
import org.bukkit.util.Vector;

public class Utils {
	
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
	
	public static LivingEntity getCrosshair(Player player) {
        BlockIterator iterator = new BlockIterator(player.getWorld(), player
                .getLocation().toVector(), player.getEyeLocation()
                .getDirection(), 0, 100);
        LivingEntity target = null;
        while (iterator.hasNext()) {
            Block block = iterator.next();
            for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
            	if (entity instanceof LivingEntity) {
	                int acc = 2;
	                for (int x = -acc; x < acc; x++)
	                for (int z = -acc; z < acc; z++)
	                for (int y = -acc; y < acc; y++)
                    if (entity.getLocation().getBlock().getRelative(x, y, z).equals(block)) {
                    	return target = (LivingEntity) entity;
                    }
	            }
            }
        }
        return target;
    }

	// rotate vector "dir" "angleD" degree on the x-z-(2D)-plane
	public static Vector rotateYAxis(Vector dir, double angleD) {
		double angleR = Math.toRadians(angleD);
		double x = dir.getX();
		double z = dir.getZ();
		double cos = Math.cos(angleR);
		double sin = Math.sin(angleR);
		return (new Vector(x*cos+z*(-sin), 0.0, x*sin+z*cos)).normalize();
	}

	public static Location getBlockRelative(Location lc, int f, int s, int u) {
		Location l = lc;
		int forward = (int) ((Number) f).doubleValue();
		int sideward = (int) ((Number) s).doubleValue();
		int upward = (int) ((Number) u).doubleValue();
		int x;
		int y = upward;
		int z;
		Block b = l.getBlock();
		Vector v = l.getDirection();
		if (Math.abs(v.getX()) > Math.abs(v.getZ())) {
			if (v.getX() > 0) {
				x = forward;
			}
			else {
				x = -forward;
			}
			if (v.getZ() > 0) {
				z = sideward;
			}
			else {
				z = -sideward;
			}
		}
		else {
			if (v.getZ() > 0) {
				z = forward;
			}
			else {
				z = -forward;
			}
			if (v.getX() > 0) {
				x = sideward;
			}
			else {
				x = -sideward;
			}
		}
		Location loc = (b.getRelative(x, y, z).getLocation());
		return loc;
	}

	public static Location getSquareLocation(Location playerloc, Integer guardnumber, Integer tickslived, Integer radius) {
		Integer pos = tickslived%8;
		Integer re = pos + (guardnumber*2);
		while (re >= 8) {
			re = re - 8;
		}
		if (re == 0) {
			playerloc.add(new Vector(0, 0, radius));
		}
		if (re == 1) {
			playerloc.add(new Vector(radius, 0, radius));
		}
		if (re == 2) {
			playerloc.add(new Vector(radius, 0, 0));
		}
		if (re == 3) {
			playerloc.add(new Vector(radius, 0, -radius));
		}
		if (re == 4) {
			playerloc.add(new Vector(0, 0, -radius));
		}
		if (re == 5) {
			playerloc.add(new Vector(-radius, 0, -radius));
		}
		if (re == 6) {
			playerloc.add(new Vector(-radius, 0, 0));
		}
		if (re == 7) {
			playerloc.add(new Vector(-radius, 0, radius));
		}
		Location back = playerloc;
		return back;
	}
	
	/**
	 * Gets the points on the circle around the position in a specific plane.
	 * @param position
	 * @param range
	 * @return points
	 */
	public static List<Location> getCirclePoints(Location position, Plane plane, float range) {
		return getCirclePoints(position, plane, range, 360);
	}
 
	/**
	 * Gets the points on the circle around the position in a specific plane.
	 * @param position
	 * @param range
	 * @param amount
	 * @return points
	 */
	public static List<Location> getCirclePoints(Location position, Plane plane, float range, int amount) {
		return getCirclePoints0(position, plane, range, amount, 0);
	}
 
	/**
	 * Gets the points on the circle around the position in a specific plane.
	 * @param position
	 * @param range
	 * @param amount
	 * @param startrotation
	 * @return points
	 */
	private static List<Location> getCirclePoints0(Location position, Plane plane, float range, int amount, int startrotation) {
		List<Location> points = new ArrayList<Location>();
		int j = 360 / amount;
 
		for (int i = 0; i < (360 / j); i++) {
			int d = (i * j) + startrotation;
 
			double x = range * Math.cos(Math.toRadians(d));
			double y = range * Math.sin(Math.toRadians(d));
 
			switch (plane) {
				case XZ:
					points.add(new Location(position.getWorld(), position.getX() + x, position.getY(), position.getZ() + y));
					break;
				case YZ:
					points.add(new Location(position.getWorld(), position.getX(), position.getY() + y, position.getZ() + x));
					break;
				case XY:
				default:
					points.add(new Location(position.getWorld(), position.getX() + x, position.getY() + y, position.getZ()));
			}
		}
 
		return points;
	}
	
	// Gets a single circle point with offset in XZ plane
	public static Location getCirclePoint(Location center, double d, double time) {
		Location l = center;
		double dx = d*Math.cos(time);
		double dz = d*Math.sin(time);
		l.add(dx, 0, dz);
		return l;
	}

	public static List<Location> getSpherePoints(Location location, float rho, int phiSteps, double phiMin, double phiMax, int thetaSteps, double thetaMin, double thetaMax) {
		List<Location> points = new ArrayList<Location>();
		if (phiSteps > 0 && thetaSteps > 0) {
			double phiInterval = (phiMax-phiMin)/phiSteps;
			double thetaInterval = (thetaMax-thetaMin)/thetaSteps; 
			for (double phi = phiMin; phi <= phiMax; phi += phiInterval) {
				for (double theta = thetaMin; theta < thetaMax; theta += thetaInterval) {
					double dx = rho*Math.sin(phi)*Math.cos(theta);
					double dz = rho*Math.sin(phi)*Math.sin(theta);
					double dy = rho*Math.cos(phi);
					points.add(new Location(location.getWorld(), location.getX()+dx, location.getY()+dy, location.getZ()+dz));
				}
			}
		}
		return points;
	}
	
	/**
	 * Gets points of a sphere around the center.
	 * @param location - center of the sphere
	 * @param rho - radius of the sphere
	 * @param phiSteps - the number of points in an up-down circle trace
	 * @param thetaSteps - number of points in a flat circle trace
	 * @return points
	 */
	public static List<Location> getSpherePoints(Location location, float rho, int phiSteps, int thetaSteps) {
		return getSpherePoints(location, rho, phiSteps, 0, Math.PI, thetaSteps, 0, 2*Math.PI);
	}
 
	/**
	 * Gets the points of a line bewteen two points.
	 * @param start
	 * @param end
	 * @param amount
	 * @return points
	 */
	public static List<Location> getLinePoints(Location start, Location end, int amount) {
		List<Location> points = new ArrayList<Location>();
 
		double k = start.getX();
		double j = start.getY();
		double n = start.getZ();
 
		double l = end.getX() - k;
		double h = end.getY() - j;
		double w = end.getZ() - n;
 
		double f1 = l / amount;
		double f2 = h / amount;
		double f3 = w / amount;
 
		for (int i = 0; i < amount; i++) {
			points.add(new Location(start.getWorld(), k + f1 * i, j + f2 * i, n + f3 * i));
		}
 
		return points;
	}
 
	/**
	 * Gets the points of a shape around the position in the specific plane.
	 * @param position
	 * @param plane
	 * @param range
	 * @param corners
	 * @param amount
	 * @return points
	 */
	public static List<Location> getShapePoints(Location position, Plane plane, float range, int corners, int amount) {
		return getShapePoints(position, plane, range, corners, amount, 0);
	}
 
	/**
	 * Gets the points of a shape around the position in the specific plane.
	 * @param position
	 * @param plane
	 * @param range
	 * @param corners
	 * @param amount
	 * @param startrotation
	 * @return points
	 */
	public static List<Location> getShapePoints(Location position, Plane plane, float range, int corners, int amount, int startrotation) {
		List<Location> points = new ArrayList<Location>();
		List<Location> points2 = getCirclePoints0(position, plane, range, corners, startrotation);
		points.addAll(points2);
 
		int a = (amount - corners) / corners;
 
		Location start = null;
		Location last = null;
		for (int i = 0; i < points2.size(); i++) {
			if (start == null) {
				start = points2.get(i);
				last = start;
				continue;
			}
 
			Location point = points2.get(i);
			points.addAll(getLinePoints(last, point, a));
			last = point;
		}
 
		if (start != null && last != null) {
			points.addAll(getLinePoints(last, start, a));
		}
 
		return points;
	}
 
	/**
	 * Gets the points of a star around the position in the specific plane.
	 * @param position
	 * @param plane
	 * @param range
	 * @param amount
	 * @return points
	 */
	public static List<Location> getStarPoints(Location position, Plane plane, float range, int amount) {
		return getStarPoints(position, plane, range, amount, 0);
	}
 
	/**
	 * Gets the points of a star around the position in the specific plane.
	 * @param position
	 * @param plane
	 * @param range
	 * @param amount
	 * @param startrotation
	 * @return points
	 */
	public static List<Location> getStarPoints(Location position, Plane plane, float range, int amount, int startrotation) {
		List<Location> points = getCirclePoints0(position, plane, range, 5, startrotation);
		int a = (amount - 5) / 5;
 
		Location p1 = points.get(0);
		Location p2 = points.get(1);
		Location p3 = points.get(2);
		Location p4 = points.get(3);
		Location p5 = points.get(4);
 
		points.addAll(getLinePoints(p1, p3, a));
		points.addAll(getLinePoints(p1, p4, a));
		points.addAll(getLinePoints(p2, p4, a));
		points.addAll(getLinePoints(p2, p5, a));
		points.addAll(getLinePoints(p3, p5, a));
 
		return points;
	}
 
	/**
	 * Gets the points of a octahedron around the position in the specific plane.
	 * @param position
	 * @param plane
	 * @param range
	 * @param height
	 * @param amount
	 * @return points
	 */
	public static List<Location> getOctahedronPoints(Location position, Plane plane, float range, float height, int amount) {
		return getOctahedronPoints(position, plane, range, height, amount, 0);
	}
 
	/**
	 * Gets the points of a octahedron around the position in the specific plane.
	 * @param position
	 * @param plane
	 * @param range
	 * @param height
	 * @param amount
	 * @param startrotation
	 * @return points
	 */
	public static List<Location> getOctahedronPoints(Location position, Plane plane, float range, float height, int amount, int startrotation) {
		List<Location> points = getCirclePoints0(position, plane, range, 4, startrotation);
		int a = (amount - 4) / 12;
 
		Location p1 = points.get(0);
		Location p2 = points.get(1);
		Location p3 = points.get(2);
		Location p4 = points.get(3);
 
		Location p5 = null;
		Location p6 = null;
 
		switch (plane) {
			case XZ:
				p5 = position.add(0.0F, height / 2, 0.0F);
				p6 = position.add(0.0F, -(height / 2), 0.0F);
				break;
			case YZ:
				p5 = position.add(height / 2, 0.0F, 0.0F);
				p6 = position.add(-(height / 2), 0.0F, 0.0F);
				break;
			case XY:
			default:
				p5 = position.add(0.0F, 0.0F, height / 2);
				p6 = position.add(0.0F, 0.0F, -(height / 2));
				break;
		}
 
		points.addAll(getLinePoints(p1, p2, a));
		points.addAll(getLinePoints(p2, p3, a));
		points.addAll(getLinePoints(p3, p4, a));
		points.addAll(getLinePoints(p4, p1, a));
 
		points.addAll(getLinePoints(p1, p5, a));
		points.addAll(getLinePoints(p2, p5, a));
		points.addAll(getLinePoints(p3, p5, a));
		points.addAll(getLinePoints(p4, p5, a));
 
		points.addAll(getLinePoints(p1, p6, a));
		points.addAll(getLinePoints(p2, p6, a));
		points.addAll(getLinePoints(p3, p6, a));
		points.addAll(getLinePoints(p4, p6, a));
 
		return points;
	}
 
	/**
	 * Gets the points of a pyramid around the position in the specific plane.
	 * @param position
	 * @param plane
	 * @param range
	 * @param height
	 * @param amount
	 * @return points
	 */
	public static List<Location> getPyramidPoints(Location position, Plane plane, float range, float height, int amount) {
		return getOctahedronPoints(position, plane, range, height, amount, 0);
	}
 
	/**
	 * Gets the points of a pyramid around the position in the specific plane.
	 * @param position
	 * @param plane
	 * @param range
	 * @param height
	 * @param amount
	 * @param startrotation
	 * @return points
	 */
	public static List<Location> getPyramidPoints(Location position, Plane plane, float range, float height, int amount, int startrotation) {
		List<Location> points = getCirclePoints0(position, plane, range, 4, startrotation);
		int a = (amount - 4) / 12;
 
		Location p1 = points.get(0);
		Location p2 = points.get(1);
		Location p3 = points.get(2);
		Location p4 = points.get(3);
 
		Location p5 = null;
 
		switch (plane) {
			case XZ:
				p5 = position.add(0.0F, height / 2, 0.0F);
				break;
			case YZ:
				p5 = position.add(height / 2, 0.0F, 0.0F);
				break;
			case XY:
			default:
				p5 = position.add(0.0F, 0.0F, height / 2);
				break;
		}
 
		points.addAll(getLinePoints(p1, p2, a));
		points.addAll(getLinePoints(p2, p3, a));
		points.addAll(getLinePoints(p3, p4, a));
		points.addAll(getLinePoints(p4, p1, a));
 
		points.addAll(getLinePoints(p1, p5, a));
		points.addAll(getLinePoints(p2, p5, a));
		points.addAll(getLinePoints(p3, p5, a));
		points.addAll(getLinePoints(p4, p5, a));
 
		return points;
	}
 
	public enum Plane {
		XY,
		XZ,
		YZ;
	}
}
