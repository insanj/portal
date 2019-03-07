package me.insanj.portal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ShapedRecipe;

public class Portal extends JavaPlugin {
    private SignMenuFactory signMenuFactory;

	@Override
	public void onEnable() {        
        // step 1: setup commands
		getCommand("portalgun").setExecutor(new PortalCommandExecutor());
        
        // step 2: setup recipe(s)
        getServer().addRecipe(PortalGun.getNetherStarRecipe());

        // step 3: setup interact listener for right click/left click with portal gun
        Bukkit.getServer().getPluginManager().registerEvents(new PortalGunClickListener(this), this); 
        this.signMenuFactory = new SignMenuFactory(this);
    }
    
    public SignMenuFactory getSignMenuFactory() {
        return this.signMenuFactory;
   }
}