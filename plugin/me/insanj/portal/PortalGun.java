package me.insanj.portal;

import java.util.ArrayList;

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
    static final String PORTAL_GUN_DEFAULT_DESCRIPTION = ChatColor.GREEN + "Right click to create a portal";

	public static ShapedRecipe getNetherStarRecipe() {
        ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
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
        return String.format("%.2f,%.2f,%.2f", location.getX(), location.getY(), location.getZ());
    }

    public static Location locationFromString(String string, World world) {
        String[] components = string.split(",");
        try {
            return new Location(world, Double.parseDouble(components[0]), Double.parseDouble(components[1]), Double.parseDouble(components[2]));
        } catch (Exception e) {
            return null;
        }
    }
}