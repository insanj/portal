package me.insanj.portal;

import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitScheduler;

public class PortalGun {
    static final String PORTAL_GUN_DISPLAY_NAME = ChatColor.RED + "Portal Gun";
    static final String PORTAL_GUN_DEFAULT_DESCRIPTION = ChatColor.GREEN + "Right click to set destination";

  	public static ShapedRecipe getNetherStarRecipe() {
        ItemStack netherStar = getNetherStarItemStack();
        ShapedRecipe shapedRecipe = new ShapedRecipe(netherStar);
        shapedRecipe.shape("*-*","*,/","..*");
        shapedRecipe.setIngredient('*', Material.AIR);
        shapedRecipe.setIngredient('-', Material.EMERALD_BLOCK);
        shapedRecipe.setIngredient(',', Material.DIAMOND_BLOCK);
        shapedRecipe.setIngredient('/', Material.REDSTONE_BLOCK);
        shapedRecipe.setIngredient('.', Material.IRON_BLOCK);
        return shapedRecipe;
    }

    public static ItemStack getNetherStarItemStack() {
        ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
        String displayName = PortalGun.PORTAL_GUN_DISPLAY_NAME;
        String itemDescription = PortalGun.PORTAL_GUN_DEFAULT_DESCRIPTION;

    	ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

    	ArrayList<String> lore = new ArrayList<String>();
    	lore.add(itemDescription);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getNetherStarItemStack(Location destination) {
        ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
        String displayName = PortalGun.PORTAL_GUN_DISPLAY_NAME;
        String itemDescription = PortalGun.stringFromLocation(destination);

    	ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

    	ArrayList<String> lore = new ArrayList<String>();
    	lore.add(itemDescription);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static String stringFromLocation(Location location) {
        return String.format("world=%s,x=%.2f,y=%.2f,z=%.2f", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static Location locationFromString(Server server, String string) {
        String[] components = string.split(",");
        try {
            World world = null;
            Double x = null, y = null, z = null;
            for (String component : components) {
                if (component.contains("x=")) {
                    x = Double.parseDouble(component.replace("x=", ""));
                } else if (component.contains("y=")) {
                    y = Double.parseDouble(component.replace("y=", ""));
                } else if (component.contains("z=")) {
                    z = Double.parseDouble(component.replace("z=", ""));
                } else if (component.contains("world=")) {
                    world = server.getWorld(component.replace("world=", ""));
                }
            }

            Location location = new Location(world, x, y, z);
            return location;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum SoundType {
        RECEIVED,
        CONFIGURED,
        ACTIVATED,
        TELEPORTED,
        PROJECTILE
    }

    public static void playSound(Plugin plugin, Location location, SoundType type) {
        World world = location.getWorld();
        switch (type) {
            case CONFIGURED:
                world.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 0);
            case RECEIVED:
                world.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                break;
            case ACTIVATED:
                world.playSound(location, Sound.BLOCK_CONDUIT_ACTIVATE, 5, 0);
                break;
            case TELEPORTED:
                world.playSound(location, Sound.ENTITY_SHULKER_TELEPORT, 5, 0);
                break;
            case PROJECTILE:
                world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 5, 0);
                break;
        }
    }

    private static void playSoundAfterDelay(Plugin plugin, Location location, Sound sound, int volume, int pitch, long ticks) {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                location.getWorld().playSound(location, sound, volume, pitch);
            }
        }, ticks); // 1 second = 20 ticks normally
    }
}
