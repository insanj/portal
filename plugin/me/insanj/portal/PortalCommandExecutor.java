package me.insanj.portal;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Sound;

public class PortalCommandExecutor implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (args.length <= 0) {
            if (sender instanceof Player) {
                player = (Player)sender;
            } else {
                sender.sendMessage(ChatColor.RED + "No player argument found, and you are not a player yourself!");
            }
        } else {
            String playerName = args[0];
            try {
                player = Bukkit.getPlayer(playerName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "No player with name " + playerName + " found!");
            }
        }

        if (player == null) {
            return true;
        }

        else if (player.isOp() == false) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to spawn a Portal Gun.");
            return true;
        }
		
        Inventory inventory = player.getInventory();
        inventory.addItem(PortalGun.getNetherStarItemStack());
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);

		return true;
    }
}
