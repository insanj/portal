package me.insanj.portal;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class PortalGunRecipe {
	
	public static ItemStack star = new ItemStack(Material.NETHER_STAR);
	public static ShapedRecipe rstar = new ShapedRecipe(star);
	
	public static ShapedRecipe setUpRecipe() {
		rstar.shape("*-*","*,/","..*");
		rstar.setIngredient('*', Material.AIR);
		rstar.setIngredient('-', Material.EMERALD_BLOCK);
		rstar.setIngredient(',', Material.DIAMOND_BLOCK);
		rstar.setIngredient('/', Material.REDSTONE_BLOCK);
        rstar.setIngredient('.', Material.IRON_BLOCK);
        return rstar;
	}
}