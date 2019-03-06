package me.insanj.portal;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class PortalGun {
    static final String PORTAL_GUN_DISPLAY_NAME = ChatColor.LIGHT_PURPLE + "Portal Gun";
    static final String PORTAL_GUN_DEFAULT_DESCRIPTION = ChatColor.GREEN + "Right click to create a portal";

	public static ShapedRecipe getNetherStarRecipe() {
        public static ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
        public static ShapedRecipe shapedRecipe = new ShapedRecipe(netherStar);
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
            return new Location(world, components[0], components[1], components[2]);
        } catch (Exception e) {
            return null;
        }
    }
}