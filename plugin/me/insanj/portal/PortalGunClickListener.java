package me.insanj.portal;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PortalGunClickListener implements Listener {
	
	public static Location loc = new Location(null, 0,0,0);
	
	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            String currentHeldItemDisplayName = player.getItemInHand().getItemMeta().getDisplayName();
			
			if (currentHeldItemDisplayName.equals(Portal.PORTAL_GUN_DISPLAY_NAME)) {
				loc = new Location(e.getClickedBlock().getLocation().getWorld(), e.getClickedBlock().getLocation().getX(), e.getClickedBlock().getLocation().getY() + 1, e.getClickedBlock().getLocation().getZ());
			}
		}
	}
}
