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
		
		ShapedRecipe rstar = PortalGunRecipe.setUpRecipe();
		getServer().addRecipe(rstar);
	}
	
	@Override
	public void onDisable() {
		plugin = null;
	}
	
	public static void Sign(Player player) {
        signGui.open(player, new String[] { "X", "Y", "Z", "WORLD" }, new PortalSignGUI.SignGUIListener() {
            @Override
            public void onSignDone(Player player, String[] lines) {
                if (Bukkit.getWorld(lines[3]) != null) {
                	
                	
                	Bukkit.getScheduler().scheduleAsyncRepeatingTask(Portal.getPlugin(), new Runnable() {
    					public void run() {
    						player.spawnParticle(Particle.VILLAGER_HAPPY, PortalClickListener.loc, 1);
    					}
    				}, 3, 3);
                	
                	Bukkit.getScheduler().scheduleAsyncRepeatingTask(Portal.getPlugin(), new Runnable() {
    					public void run( ) {
    						for (Player p : Bukkit.getOnlinePlayers()) {
	    						if (p.getLocation() == PortalClickListener.loc) {
	    							player.teleport(new Location(Bukkit.getWorld(lines[3]), Integer.parseInt(lines[0]), Integer.parseInt(lines[1]), Integer.parseInt(lines[2])));
	    						}
    						}
    					}
    				}, 3, 3);
                } else if (lines[3] == null){

                	
                	Bukkit.getScheduler().scheduleAsyncRepeatingTask(Portal.getPlugin(), new Runnable() {
    					public void run() {
    						player.spawnParticle(Particle.VILLAGER_HAPPY, PortalClickListener.loc, 1);
    					}
    				}, 3, 3);
                	
                	Bukkit.getScheduler().scheduleAsyncRepeatingTask(Portal.getPlugin(), new Runnable() {
    					public void run( ) {
    						for (Player p : Bukkit.getOnlinePlayers()) {
	    						if (p.getLocation() == PortalClickListener.loc) {
	    							player.teleport(new Location(player.getLocation().getWorld(), Integer.parseInt(lines[0]), Integer.parseInt(lines[1]), Integer.parseInt(lines[2])));
	    						}
    						}
    					}
    				}, 3, 3);
                } else {
                	player.sendMessage("Invalid Arguments");
                }
            }
        });
    }
	
	// Registers events for each listener
	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners)
	{
		for (Listener listener : listeners) { Bukkit.getServer().getPluginManager().registerEvents(listener, plugin); }
	}
	
    //To access the plugin variable from other classes
    public static Plugin getPlugin() { return plugin; } 
}