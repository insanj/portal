package me.insanj.portal;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Sound;

public class PortalPlayerMoveListener implements Listener {	
    public Boolean deactivated = false; // TODO remove
    public final Location origin;
    public final Location destination;
    public final double threshold = 1;
    public ArrayList<String> alreadyTeleportedPlayers = new ArrayList<String>();
    public PortalPlayerMoveListener(Location origin, Location destination) {
        this.origin = origin;
        this.destination = destination;
    }

    @EventHandler()
    public void onMove(PlayerMoveEvent e) {
        if (deactivated == true) {
            return;
        }
        else if (alreadyTeleportedPlayers.contains(e.getPlayer().getUniqueId()) == true) {
            return;
        }
        Location playerLocation = e.getPlayer().getLocation();
        double diff = Math.abs(Math.abs(playerLocation.getX() - origin.getX()) + Math.abs(playerLocation.getY() - origin.getY()) + Math.abs(playerLocation.getZ() - origin.getZ()));

        if (diff <= threshold) {
            Player player = e.getPlayer();
            player.teleport(destination);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, 10, 1);
        }
    }
}
