package me.insanj.portal;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PortalGunClickListener implements Listener {	
    public final Portal plugin;
    public PortalGunClickListener(Portal plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInteraction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        String heldItemDisplayName = player.getItemInHand().getItemMeta().getDisplayName();
        if (heldItemDisplayName.equals(Portal.PORTAL_GUN_DISPLAY_NAME)) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                openSignGUI(e, player);
            } 
            else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                openPortal(e, player);
            }
        }
    }

    public void openPortal(PlayerInteractEvent event, Player player) {
        String netherStarLocationString = player.getItemInHand().getItemMeta().getLore().get(0);
        Location destination = PortalGun.locationFromString(netherStarLocationString, player.getWorld());
        Location portalLocation = e.getClickedBlock().getLocation();

        // String loc = "X: " + .getBlockX() + " Y: " + e.getClickedBlock().getLocation().getBlockY() + " Z: " + e.getClickedBlock().getLocation().getBlockZ();
        player.spawnParticle(Particle.VILLAGER_HAPPY, portalLocation, 1);
        activatePortal(portalLocation);
    }

    public void activatePortal(Location location) {
        PortalPlayerMoveListener moveListener = new PortalPlayerMoveListener(location);
        Bukkit.getServer().getPluginManager.registerEvents(moveListener, plugin);

        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                HandlerList.unregisterAll(moveListener);
            }
        }, 100L); // 100 ticks = 5 seconds, because 1 seconds = 20 ticks normally
    }
    
    public void openSignGUI(PlayerInteractEvent event, Player player) {
        PortalSignGUI signGui = new PortalSignGUI(this);
        PortalGunClickListener listener = this;
        signGui.open(player, new String[] { "x", "y", "z", "world" }, new SignGUI.SignGUIListener() {
            @Override
            public void onSignDone(Player player, String[] lines) {
                listener.onSignDone(player, lines);
            }
        });
    }

    public void onSignDone(PlayerInteractEvent event, Player player, String[] lines) {
        Double x = Double.parseDouble(lines[0]);
        Double y = Double.parseDouble(lines[1]);
        Double z = Double.parseDouble(lines[2]);

        Location signLocation = new Location(player.getWorld(), x, y, z);
        ItemStack currentNetherStarItem = player.getItemInHand();
        ItemStack updatedNetherStarItem = PortalGun.getNetherStarItemStack(signLocation);
        player.setItemInHand(updatedNetherStarItem);
    }
}
