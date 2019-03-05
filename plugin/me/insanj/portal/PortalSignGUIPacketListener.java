package me.insanj.portal;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

class PortalSignGUIPacketListener extends PacketAdapter {
    
    Plugin plugin;
    PortalSignGUI signGUI;
    
    public PortalSignGUIPacketListener(Plugin plugin, PortalSignGUI signGUI) {
        super(plugin, ConnectionSide.CLIENT_SIDE, ListenerPriority.NORMAL, 0x82);
        this.plugin = plugin;
        this.signGUI = signGUI;
    }
    
    @Override
    public void onPacketReceiving(PacketEvent event) {
        final Player player = event.getPlayer();
        Vector v = signGUI.signLocations.remove(player.getName());
        if (v == null) return;
        List<Integer> list = event.getPacket().getIntegers().getValues();
        if (list.get(0) != v.getBlockX()) return;
        if (list.get(1) != v.getBlockY()) return;
        if (list.get(2) != v.getBlockZ()) return;
        
        final String[] lines = event.getPacket().getStringArrays().getValues().get(0);
        final PortalSignGUIListener response = signGUI.listeners.remove(event.getPlayer().getName());
        if (response != null) {
            event.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    response.onSignDone(player, lines);
                }
            });
        }
    }
    
}