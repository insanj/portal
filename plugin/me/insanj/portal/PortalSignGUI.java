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

public class PortalSignGUI {

    protected ProtocolManager protocolManager;
    protected PacketAdapter packetListener;
    protected Map<String, PortalSignGUIListener> listeners;
    protected Map<String, Vector> signLocations;
    
    public PortalSignGUI(Plugin plugin) {
        protocolManager = ProtocolLibrary.getProtocolManager();        
        packetListener = new PortalSignGUIPacketListener(plugin, this);
        protocolManager.addPacketListener(packetListener);
        listeners = new ConcurrentHashMap<String, PortalSignGUIListener>();
        signLocations = new ConcurrentHashMap<String, Vector>();
    }
    
    public void open(Player player, PortalSignGUIListener response) {
        open(player, (Location)null, response);
    }
    
    public void open(Player player, Location signLocation, PortalSignGUIListener response) {
        int x = 0, y = 0, z = 0;
        if (signLocation != null) {
            x = signLocation.getBlockX();
            y = signLocation.getBlockY();
            z = signLocation.getBlockZ();
        }
        
        PacketContainer packet = protocolManager.createPacket(133);
        packet.getIntegers().write(0, 0).write(1, x).write(2, y).write(3, z);
        
        try {
            protocolManager.sendServerPacket(player, packet);
            signLocations.put(player.getName(), new Vector(x, y, z));
            listeners.put(player.getName(), response);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public void open(Player player, String[] defaultText, PortalSignGUIListener response) {
        List<PacketContainer> packets = new ArrayList<PacketContainer>();
        
        int x = 0, y = 0, z = 0;
        if (defaultText != null) {
            x = player.getLocation().getBlockX();
            z = player.getLocation().getBlockZ();
            
            PacketContainer packet53 = protocolManager.createPacket(53);
            packet53.getIntegers().write(0, x).write(1, y).write(2, z).write(3, 63).write(4, 0);
            packets.add(packet53);
            
            PacketContainer packet130 = protocolManager.createPacket(130);
            packet130.getIntegers().write(0, x).write(1, y).write(2, z);
            packet130.getStringArrays().write(0, defaultText);
            packets.add(packet130);
        }
        
        PacketContainer packet133 = protocolManager.createPacket(133);
        packet133.getIntegers().write(0, 0).write(1, x).write(2, y).write(3, z);
        packets.add(packet133);
        
        if (defaultText != null) {
            PacketContainer packet53 = protocolManager.createPacket(53);
            packet53.getIntegers().write(0, x).write(1, y).write(2, z).write(3, 7).write(4, 0);
            packets.add(packet53);
        }
        
        try {
            for (PacketContainer packet : packets) {
                protocolManager.sendServerPacket(player, packet);
            }
            signLocations.put(player.getName(), new Vector(x, y, z));
            listeners.put(player.getName(), response);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public void destroy() {
        protocolManager.removePacketListener(packetListener);
        listeners.clear();
        signLocations.clear();
    }
}