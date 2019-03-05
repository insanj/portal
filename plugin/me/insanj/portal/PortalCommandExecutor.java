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

public class PortalCommandExecutor implements CommandExecutor {
	
	// Item Creation
	public static void createDisplay(ItemStack Material, Inventory inv, String name, String lore)
	{
		ItemStack item = Material;
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		ArrayList<String> Lore = new ArrayList<String>();
		Lore.add(lore);
		meta.setLore(Lore);
		item.setItemMeta(meta);
				 
		inv.addItem(item);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args[0] != null && Bukkit.getPlayer(args[0]) != null && sender.isOp()) {
			Player target = Bukkit.getPlayer(args[0]);
			createDisplay(new ItemStack(Material.NETHER_STAR, 1), target.getInventory(), ChatColor.LIGHT_PURPLE + "Portal Gun", "Right click to create a portal");
		}
		
		return false;
	}
}
