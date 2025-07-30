package com.bsrushards;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class AFKPool {

    private final String name;
    private final String worldName;
    private final int time;
    private final int amount;

    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;

    public AFKPool(String name, World world, Location loc1, Location loc2, int time, int amount) {
        this.name = name;
        this.worldName = world.getName();
        this.time = time;
        this.amount = amount;

        this.minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        this.minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        this.minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        this.maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        this.maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        this.maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
    }

    private AFKPool(String name, String worldName, int time, int amount, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.name = name;
        this.worldName = worldName;
        this.time = time;
        this.amount = amount;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isInside(Location loc) {
        if (!loc.getWorld().getName().equals(this.worldName)) {
            return false;
        }
        return loc.getBlockX() >= minX && loc.getBlockX() <= maxX &&
                loc.getBlockY() >= minY && loc.getBlockY() <= maxY &&
                loc.getBlockZ() >= minZ && loc.getBlockZ() <= maxZ;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("world", worldName);
        map.put("time", time);
        map.put("amount", amount);
        map.put("pos1", minX + "," + minY + "," + minZ);
        map.put("pos2", maxX + "," + maxY + "," + maxZ);
        return map;
    }

    public static AFKPool deserialize(String name, ConfigurationSection section) {
        try {
            String world = section.getString("world");
            int time = section.getInt("time");
            int amount = section.getInt("amount");

            String[] pos1Parts = section.getString("pos1").split(",");
            String[] pos2Parts = section.getString("pos2").split(",");

            int minX = Integer.parseInt(pos1Parts[0]);
            int minY = Integer.parseInt(pos1Parts[1]);
            int minZ = Integer.parseInt(pos1Parts[2]);

            int maxX = Integer.parseInt(pos2Parts[0]);
            int maxY = Integer.parseInt(pos2Parts[1]);
            int maxZ = Integer.parseInt(pos2Parts[2]);

            return new AFKPool(name, world, time, amount, minX, minY, minZ, maxX, maxY, maxZ);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}