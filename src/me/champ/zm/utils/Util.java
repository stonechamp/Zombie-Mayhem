package me.champ.zm.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.champ.zm.Core;
import me.champ.zm.objects.Game;
import net.md_5.bungee.chat.ComponentSerializer;

public class Util {
	
	static Core plugin = Core.getInstance();
	
	public static int getRandomNumberInRange(Random r, int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	public static void sendActionBar(Player player, String message) {
		PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
		chat.getBytes().write(0, (byte) 2);
		chat.getChatComponents().write(0, WrappedChatComponent.fromJson(message)); 
		try {
			plugin.getProtocolLib().sendServerPacket(player, chat);
		} catch (InvocationTargetException e) {
		
		}
	}

}
