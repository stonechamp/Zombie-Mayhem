package me.champ.zm.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.champ.zm.Core;
import me.champ.zm.data.DataHandler;
import me.champ.zm.objects.Game;


public class CreateCommand extends SubCommand {

	private Core plugin = Core.getInstance();
	
	@Override
	public void onCommand(Player player, String[] args) {
		
		if (args.length < 2) {
			player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + " /zm create (game name)");
			return;
		}
		
		if (args.length > 2) {
			player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + " /zm create (game name)");
			return;
		}
		
		if (DataHandler.getGameFile().get("lobby-world.spawn.x") == null || DataHandler.getGameFile().get("lobby-world.spawn.y") == null || DataHandler.getGameFile().get("lobby-world.spawn.z") == null) {
			player.sendMessage(ChatColor.RED + "Please set the lobby spawn point.");
			return;
		}
		
		Game game = new Game(args[1]);
		if (game.exists() == false) {
			player.sendMessage(ChatColor.GREEN + "Creating game...");
			game.create();
			plugin.addGame(game);
			player.sendMessage(ChatColor.GREEN + "Game " + game.getName() + " has been created.");
			Location location = new Location(Bukkit.getWorld(game.getName()), 100, 0, 100);
			player.teleport(location);
		} else {
			player.sendMessage(plugin.getPrefix() + ChatColor.RED + " Game already exists.");
		}
		

	}

	@Override
	public String name() {
		return plugin.getCommandHandler().create;
	}

}
