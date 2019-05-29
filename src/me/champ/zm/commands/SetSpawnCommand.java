package me.champ.zm.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.champ.zm.commands.SubCommand;
import me.champ.zm.Core;
import me.champ.zm.data.DataHandler;
import net.md_5.bungee.api.ChatColor;

public class SetSpawnCommand extends SubCommand {
	
	private Core plugin = Core.getInstance();
	
	@Override
	public void onCommand(Player player, String[] args) {
		
		double x = player.getLocation().getX();
		double y = player.getLocation().getY();
		double z = player.getLocation().getZ();
		
		if (args.length < 2) {
			player.sendMessage(plugin.getPrefix()+ ChatColor.YELLOW + " /zm setspawn (game name)");
		}
		
		if (args.length == 2) {
			
			
			if (args[1].equalsIgnoreCase("lobby")) {
				String worldName = player.getWorld().getName();
				
				if (!worldName.equals(plugin.getConfig().get("lobby-world.name"))) {
					player.sendMessage(ChatColor.RED + "Please go to your lobby world to set the spawn.");
					return;
				}
				
				if (plugin.getGame(worldName) != null) {
					player.sendMessage(ChatColor.RED + "You may not set the lobby spawn inside a game arena.");
					return;
				}
				
				DataHandler.getGameFile().set("lobby-world.name", worldName);
				DataHandler.getGameFile().set("lobby-world.spawn.x", x);
				DataHandler.getGameFile().set("lobby-world.spawn.y", y);
				DataHandler.getGameFile().set("lobby-world.spawn.z", z);
				DataHandler.saveGameConfig();
				player.sendMessage(plugin.getPrefix() + " " + ChatColor.GREEN + "Lobby spawn set.");
				return;
			} else {
				
				String gameName = args[1];
				File file = new File(Bukkit.getServer().getWorldContainer(), gameName);
				if (!file.exists()) {
					player.sendMessage(ChatColor.RED + "That game does not exist.");
					return;
				}
				
				if (!player.getWorld().getName().equals(gameName)) {
					player.sendMessage(ChatColor.RED + "You must be in the world " + gameName + " to set the spawn.");
					return;
				}
			
				DataHandler.getGameFile().set("games." + gameName + ".spawn.x", x);
				DataHandler.getGameFile().set("games." + gameName + ".spawn.y", y);
				DataHandler.getGameFile().set("games." + gameName + ".spawn.z", z);
				DataHandler.saveGameConfig();
				player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + " Spawn set for game: " + ChatColor.YELLOW + gameName);
				Location loc = new Location(Bukkit.getWorld(plugin.getLobbyWorld()), 100, 0, 0);
				player.teleport(loc);
			}
			
			
			

		}
		
		

	}

	@Override
	public String name() {
		return plugin.getCommandHandler().setspawn;
	}

}
