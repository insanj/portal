package me.insanj.portal;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PortalPlayerMoveListener implements Listener {	
    public final Location destination;
    public final double threshold = 1;
    public PortalPlayerMoveListener(Location destination) {
        this.destination = destination;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onMove(PlayerMoveEvent e) {
        Location playerLocation = e.getPlayer().getLocation();
        double diff = Math.abs(Math.abs(playerLocation.getX() - destination.getX()) + Math.abs(playerLocation.getY() - destination.getY()) + Math.abs(playerLocation.getZ() - destination.getZ()));
        if (diff >= threshold) {
            e.getPlayer().teleport(destination);
        }
    }
}
