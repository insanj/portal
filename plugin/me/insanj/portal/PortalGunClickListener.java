package me.insanj.portal;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.function.BiFunction;

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
            else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
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

        // String logMessage = String.format("activated portal from %s to %s", portalLocation.toString(), destination.toString());
        // Bukkit.getServer().getLogger().info(logMessage);

        // play sound at completion
        PortalGun.playSound(plugin, portalLocation, PortalGun.SoundType.ACTIVATED);
    }

    public void activatePortal(Location origin, Location destination) {
        PortalPlayerMoveListener moveListener = new PortalPlayerMoveListener(plugin, origin, destination);
        plugin.getServer().getPluginManager().registerEvents(moveListener, plugin);

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                // String logMessage = String.format("deactivated portal from %s to %s", origin.toString(), destination.toString());
                // Bukkit.getServer().getLogger().info(logMessage);

                HandlerList.unregisterAll(moveListener);
                moveListener.deactivated = true;
            }
        }, 100L); // 100 ticks = 5 seconds, because 1 seconds = 20 ticks normally
    }
    
    enum AnvilGUIStep {
        XCOORDINATE,
        YCOORDINATE,
        ZCOORDINATE,
        WORLDNAME
    }

    public void openSignGUI(PlayerInteractEvent event, Player signPlayer, ItemStack heldItem) {
        Location existingLocation = null;
        String existingDescription = heldItem.getItemMeta().getLore().get(0); // if existing coords, use those
        if (existingDescription != null && !existingDescription.equals(PortalGun.PORTAL_GUN_DEFAULT_DESCRIPTION)) {
            existingLocation = PortalGun.locationFromString(plugin.getServer(), existingDescription);
        }

        beginSignGUIStep(AnvilGUIStep.XCOORDINATE, null, plugin, signPlayer, existingLocation);
    }

    public String beginSignGUIStep(AnvilGUIStep step, String[] replies, Plugin plugin, Player signPlayer, Location existingLocation) {
        PortalGunClickListener listener = this;

        if (step == AnvilGUIStep.XCOORDINATE) {
            String displayString = "x="; //  existingLocation != null ? ("x=" + Double.toString(existingLocation.getX())) : 
            buildAnvilGUI(true, plugin, signPlayer, "X Coordinate", displayString, (player, reply) -> {
                if (listener.validateSignGUIStep(step, plugin, reply) == false) {
                    return "Type in an x coordinate.";
                } else {
                    String[] additionalReplies = new String[]{reply};
                    beginSignGUIStep(AnvilGUIStep.YCOORDINATE, additionalReplies, plugin, signPlayer, existingLocation);
                    return null;
                }
            });
        }
        
        else if (step == AnvilGUIStep.YCOORDINATE) {
            String displayString = "y="; // existingLocation != null ? ("y=" + Double.toString(existingLocation.getY())) : 
            buildAnvilGUI(plugin, signPlayer, "Y Coordinate", displayString, (player, reply) -> {
                if (listener.validateSignGUIStep(step, plugin, reply) == false) {
                    return "Type in a y coordinate.";
                } else {
                    String[] additionalReplies = new String[]{replies[0], reply};
                    beginSignGUIStep(AnvilGUIStep.ZCOORDINATE, additionalReplies, plugin, signPlayer, existingLocation);
                    return null;
                }
            });
        }

        else if (step == AnvilGUIStep.ZCOORDINATE) {
            String displayString = "z="; // existingLocation != null ? ("z=" + Double.toString(existingLocation.getZ())) :
            buildAnvilGUI(plugin, signPlayer, "Z Coordinate", displayString, (player, reply) -> {
                if (listener.validateSignGUIStep(step, plugin, reply) == false) {
                    return "Type in a z coordinate.";
                } else {
                    String[] additionalReplies = new String[]{replies[0], replies[1], reply};
                    beginSignGUIStep(AnvilGUIStep.WORLDNAME, additionalReplies, plugin, signPlayer, existingLocation);
                    return null;
                }
            });
        }

        else if (step == AnvilGUIStep.WORLDNAME) {
            String displayString = "world="; // existingLocation != null ? ("world=" + existingLocation.getWorld().getName()) : "world=";// ("world=" + signPlayer.getWorld().getName());
            buildAnvilGUI(plugin, signPlayer, "World Name", displayString, (player, reply) -> {
                if (listener.validateSignGUIStep(step, plugin, reply) == false) {
                    return "Type in a world name.";
                } else {
                    String[] additionalReplies = new String[]{replies[0], replies[1], replies[2], reply};
                    finishAnvilGUISteps(additionalReplies, plugin, signPlayer);
                    return null;
                }
            });
        }

        return null; // should never get here
    }

    public boolean validateSignGUIStep(AnvilGUIStep step, Plugin plugin, String reply) {
        switch (step) {
            case XCOORDINATE:
                return reply.contains("x=") && reply.length() >= 3;
            case YCOORDINATE:
                return reply.contains("y=") && reply.length() >= 3;
            case ZCOORDINATE:
                return reply.contains("z=") && reply.length() >= 3;
            case WORLDNAME:
                return reply.contains("world=");
            default:
                return false;
        }
    }

    public String finishAnvilGUISteps(String[] replies, Plugin plugin, Player player) {
        try {
            String locationString = String.format("%s,%s,%s,%s", replies[0], replies[1], replies[2], replies[3]);
            Location inputLocation = PortalGun.locationFromString(plugin.getServer(), locationString);
            ItemStack updatedNetherStarItem = PortalGun.getNetherStarItemStack(inputLocation);
            player.setItemInHand(updatedNetherStarItem);
            PortalGun.playSound(plugin, player.getLocation(), PortalGun.SoundType.CONFIGURED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void buildAnvilGUI(Plugin plugin, Player player, String title, String text,  BiFunction<Player, String, String> biFunction) {
        buildAnvilGUI(false, plugin, player, title, text, biFunction);
    }

    public void buildAnvilGUI(boolean skip, Plugin plugin, Player player, String title, String text,  BiFunction<Player, String, String> biFunction) {
        if (skip == true) {
            new AnvilGUI(plugin, player, title, text, biFunction);
        } else {
            BukkitScheduler scheduler = plugin.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    new AnvilGUI(plugin, player, title, text, biFunction);
                }
            }, 1L);
        }
    }
}
