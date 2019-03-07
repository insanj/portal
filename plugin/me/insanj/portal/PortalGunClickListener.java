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
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Sound;

public class PortalGunClickListener implements Listener {	
    public final static String PORTAL_ERROR_NO_FUEL = ChatColor.RED + "Portal Gun out of fuel! Get more Ender Pearls to use.";
    public final Portal plugin;
    
    public PortalGunClickListener(Portal plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onPlayerInteraction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        String heldItemDisplayName = player.getItemInHand().getItemMeta().getDisplayName();
        if (heldItemDisplayName.equals(PortalGun.PORTAL_GUN_DISPLAY_NAME)) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                openSignGUI(e, player);
            } 
            else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (player.getInventory().contains(Material.ENDER_PEARL, 1) == false) {
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
        Location destination = PortalGun.locationFromString(netherStarLocationString, player.getWorld());
        Location portalLocation = event.getClickedBlock().getLocation();

        // particle effect
        player.spawnParticle(Particle.VILLAGER_HAPPY, portalLocation, 1);
        
        // activate for 5 seconds
        activatePortal(portalLocation);

        // play sound at completion
        player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, 1F, 1F);
    }

    public void activatePortal(Location location) {
        PortalPlayerMoveListener moveListener = new PortalPlayerMoveListener(location);
        plugin.getServer().getPluginManager().registerEvents(moveListener, plugin);

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                HandlerList.unregisterAll(moveListener);
            }
        }, 100L); // 100 ticks = 5 seconds, because 1 seconds = 20 ticks normally
    }
    
    public void openSignGUI(PlayerInteractEvent event, Player signPlayer) {
        PortalGunClickListener listener = this;
        List<String> defaultText = Arrays.asList("x", "y", "z");
        
        new AnvilGUI(plugin, signPlayer, "What is the meaning of life?", (player, reply) -> {
            if (reply.equalsIgnoreCase("you")) {
                player.sendMessage("You have magical powers!");
                return null;
            }
            return "Incorrect.";
        });
    }

    public void onSignDone(PlayerInteractEvent event, Player player, String[] lines) {
        try {
            Double x = Double.parseDouble(lines[0]);
            Double y = Double.parseDouble(lines[1]);
            Double z = Double.parseDouble(lines[2]);

            Location signLocation = new Location(player.getWorld(), x, y, z);
            ItemStack currentNetherStarItem = player.getItemInHand();
            ItemStack updatedNetherStarItem = PortalGun.getNetherStarItemStack(signLocation);
            player.setItemInHand(updatedNetherStarItem);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Unable to open save portal destination, please try again.");
        }
    }
}
