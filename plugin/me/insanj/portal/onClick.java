package General;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class onClick implements Listener {
	
	public static Location loc = new Location(null, 0,0,0);
	
	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = e.getPlayer();
			
			if (player.getItemInHand().getItemMeta().getDisplayName() == "Portal Gun") {
				loc = new Location(e.getClickedBlock().getLocation().getWorld(), e.getClickedBlock().getLocation().getX(), e.getClickedBlock().getLocation().getY() + 1, e.getClickedBlock().getLocation().getZ());
				
				
				
			}
		}
	}
}
