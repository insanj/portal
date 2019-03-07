package me.insanj.portal;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Sound;

public class PortalGunClickListener implements Listener {	
    public final static String PORTAL_ERROR_NO_FUEL = ChatColor.RED + "Portal Gun out of fuel! Get more Ender Pearls to use.";
    public final static String PORTAL_ERROR_NEED_TO_SETUP = ChatColor.RED + "Portal Gun has no destination yet. Right click with it in hand to set one up first!";
    public final Portal plugin;
    
    public PortalGunClickListener(Portal plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onPlayerInteraction(PlayerInteractEvent e) {
        EquipmentSlot equipmentSlot = e.getHand();
        if (!equipmentSlot.equals(EquipmentSlot.HAND)) {
            return;
        }

        Player player = e.getPlayer();
        ItemStack heldItem = player.getItemInHand();
        if (heldItem == null || heldItem.getItemMeta() == null || heldItem.getItemMeta().getDisplayName() == null) {
            return; // nothing to see here, no item in hand!
        }
        
        String heldItemDisplayName = heldItem.getItemMeta().getDisplayName();
        if (heldItemDisplayName.equals(PortalGun.PORTAL_GUN_DISPLAY_NAME)) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                openSignGUI(e, player, heldItem);
            } 
            else if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
                if (heldItem.getItemMeta().getLore().get(0).equals(PortalGun.PORTAL_GUN_DEFAULT_DESCRIPTION)) {
                    player.sendMessage(PortalGunClickListener.PORTAL_ERROR_NEED_TO_SETUP);
                } else if (player.getInventory().contains(Material.ENDER_PEARL, 1) == false) {
                    player.sendMessage(PortalGunClickListener.PORTAL_ERROR_NO_FUEL);
                } else {
                    removePortalFuelFromInventory(player);
                    openPortal(e, player);
                }
            }
        }
    }

    public void removePortalFuelFromInventory(Player player) {
        ItemStack enderPearl = new ItemStack(Material.ENDER_PEARL);
        player.getInventory().removeItem​(enderPearl);
    }

    public void openPortal(PlayerInteractEvent event, Player player) {
        String netherStarLocationString = player.getItemInHand().getItemMeta().getLore().get(0);
        Location destination = PortalGun.locationFromString(plugin.getServer(), netherStarLocationString);
        Location portalLocation = event.getClickedBlock().getLocation();

        // particle effect
        int particleCount = 10;
        int particleSize = 4;
        for (int x = 0; x < particleSize; x++) {
            for (int y = 0; y < particleSize; y++) {
                for (int z = 0; z < particleSize; z++) {
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, portalLocation, particleCount, x, y, z);
                }
            }
        }

        // activate for 5 seconds
        activatePortal(portalLocation, destination);

        String logMessage = String.format("activated portal from %s to %s", portalLocation.toString(), destination.toString());
        Bukkit.getServer().getLogger().info(logMessage);

        // play sound at completion
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 5, 1);
    }

    public void activatePortal(Location origin, Location destination) {
        PortalPlayerMoveListener moveListener = new PortalPlayerMoveListener(origin, destination);
        plugin.getServer().getPluginManager().registerEvents(moveListener, plugin);

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                String logMessage = String.format("deactivated portal from %s to %s", origin.toString(), destination.toString());
                Bukkit.getServer().getLogger().info(logMessage);

                HandlerList.unregisterAll(moveListener);
                moveListener.deactivated = true;
            }
        }, 100L); // 100 ticks = 5 seconds, because 1 seconds = 20 ticks normally
    }
    
    public void openSignGUI(PlayerInteractEvent event, Player signPlayer, ItemStack heldItem) {
        PortalGunClickListener listener = this;

        String defaultText = heldItem.getItemMeta().getLore().get(0); // if existing coords, use those
        if (defaultText.equals(PortalGun.PORTAL_GUN_DEFAULT_DESCRIPTION)) {
            defaultText = "world=";
            defaultText += signPlayer.getWorld().getName();
            defaultText += ",x=,y=,z=";
        }

        String portalUITitle = "Set Portal Destination";
        new AnvilGUI(plugin, signPlayer, portalUITitle, defaultText, (player, reply) -> {
            if (listener.onSignDone(event, player, reply) == true) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                return null;
            }
            return "Incomplete destination coordinates.";
        });
    }

    public Boolean onSignDone(PlayerInteractEvent event, Player player, String reply) {
        try {
            Location inputLocation = PortalGun.locationFromString(plugin.getServer(), reply);
            ItemStack updatedNetherStarItem = PortalGun.getNetherStarItemStack(inputLocation);
            player.setItemInHand(updatedNetherStarItem);
            return true;
        } catch (Exception e) {
           // e.printStackTrace();
        }
        return false;
    }
}
