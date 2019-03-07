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

public class PortalGun {
    static final String PORTAL_GUN_DISPLAY_NAME = ChatColor.LIGHT_PURPLE + "Portal Gun";
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
            World world = server.getWorld(components[0].replace("world=", ""));
            Double x = Double.parseDouble(components[1].replace("x=", ""));
            Double y = Double.parseDouble(components[2].replace("y=", ""));
            Double z = Double.parseDouble(components[3].replace("z=", ""));
            Location location = new Location(world, x, y, z);
            return location;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}