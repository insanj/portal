package me.insanj.portal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ShapedRecipe;

public class Portal extends JavaPlugin {
	private static Plugin plugin;
	static PortalSignGUI signGui;
	
	@Override
	public void onEnable() {
		plugin = this;
		signGui = new PortalSignGUI(this);
		
		getCommand("portalgun").setExecutor(new PortalCommandExecutor());
		registerEvents(this, new PortalClickListener());
        
		ShapedRecipe portalGunRecipe = PortalGunRecipe.setUpRecipe();
		getServer().addRecipe(portalGunRecipe);
	}
	
	@Override
	public void onDisable() {
		plugin = null;
	}
	
	public static void Sign(Player player) {
        signGui.open(player, new String[] { "X", "Y", "Z", "WORLD" }, new PortalSignGUIListener() );
    }
	
	// Registers events for each listener
	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners)
	{
		for (Listener listener : listeners) { Bukkit.getServer().getPluginManager().registerEvents(listener, plugin); }
	}
	
    //To access the plugin variable from other classes
    public static Plugin getPlugin() { return plugin; } 
}