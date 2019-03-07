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
    public final double threshold = 3;
    public final Portal plugin;
    public PortalPlayerMoveListener(Portal plugin, Location origin, Location destination) {
        this.plugin = plugin;
        this.origin = origin;
        this.destination = destination;
    }

    @EventHandler()
    public void onMove(PlayerMoveEvent e) {
        if (deactivated == true) {
            return;
        }

        double originDestDiff = Math.abs(Math.abs(destination.getX() - origin.getX()) + Math.abs(destination.getY() - origin.getY()) + Math.abs(destination.getZ() - origin.getZ()));
        if (originDestDiff <= threshold) {
            return; // too close!
        }

        Location playerLocation = e.getPlayer().getLocation();
        double diff = Math.abs(Math.abs(playerLocation.getX() - origin.getX()) + Math.abs(playerLocation.getY() - origin.getY()) + Math.abs(playerLocation.getZ() - origin.getZ()));

        if (diff <= threshold) {
            Player player = e.getPlayer();
            Location lookingLocation = player.getEyeLocation();

            float yaw = lookingLocation.getYaw();
            float pitch = lookingLocation.getPitch();

            destination.setYaw(yaw);
            destination.setPitch(pitch);

            player.teleport(destination);
            PortalGun.playSound(plugin, destination, PortalGun.SoundType.TELEPORTED);
        }
    }
}
