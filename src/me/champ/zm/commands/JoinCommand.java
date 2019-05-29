package me.champ.zm.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import me.champ.zm.Core;
import me.champ.zm.data.DataHandler;
import me.champ.zm.objects.Game;

public class JoinCommand extends SubCommand {

	Core plugin = Core.getInstance();
	
	@Override
	public void onCommand(Player player, String[] args) {
		
		if (args.length < 2) {
			player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + " /zm join (game name)");
		}
		
		if (args.length > 2) {
			player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + " /zm join (game name)");
		}
		
		if (args.length == 2) {
			Game game = plugin.getGame(args[1]);
			
			if (game == null) {
				player.sendMessage(ChatColor.RED + "That is not a valid game.");
			} else {
				double x = DataHandler.getGameFile().getDouble("games." + args[1] + ".spawn.x");
				double y = DataHandler.getGameFile().getDouble("games." + args[1] + ".spawn.y");
				double z = DataHandler.getGameFile().getDouble("games." + args[1] + ".spawn.z");
				Location location = new Location(Bukkit.getWorld(game.getName()), x, y, z);
				
				player.teleport(location);
				
				
			}
		}

	}

	@Override
	public String name() {
		return plugin.getCommandHandler().join;
	}

}
