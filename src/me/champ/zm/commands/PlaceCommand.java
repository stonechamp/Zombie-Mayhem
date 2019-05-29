package me.champ.zm.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.champ.zm.Core;
import me.champ.zm.data.DataHandler;


public class PlaceCommand extends SubCommand {
	
	Core plugin = Core.getInstance();

	@Override
	public void onCommand(Player player, String[] args) {
		
		if (args.length < 2) {
			player.sendMessage(plugin.getPrefix() + ChatColor.RED + " /zm place (sign/shop)");
		}
		
		if (args[1].equalsIgnoreCase("sign")) {
			if (args.length < 3) {
				player.sendMessage(plugin.getPrefix() + ChatColor.RED + " /zm place sign (game name)");
			}
			
			if (!DataHandler.getGameFile().contains("games." + args[2])) {
				player.sendMessage(plugin.getPrefix() + " That game doesn't exist.");
			}
			
			//TODO: Place game sign.
			String gameName = args[2];
			int playerCount = Bukkit.getWorld(gameName).getPlayers().size();
			Location location = player.getLocation();
			location.getWorld().getBlockAt(location).setType(Material.SIGN_POST);
			Sign sign = (Sign) player.getWorld().getBlockAt(location).getState();
			sign.setLine(0, ChatColor.GREEN + "[JOIN]");
			sign.setLine(2, ChatColor.DARK_GRAY + "(" + ChatColor.AQUA + playerCount + ChatColor.DARK_GRAY + "/" + ChatColor.AQUA + plugin.getMaxPlayers() + ChatColor.DARK_GRAY + ")");
			sign.setLine(3, ChatColor.GOLD + "" + ChatColor.BOLD + "(Right Click)");
			sign.update();
		}

	}

	@Override
	public String name() {
		return plugin.getCommandHandler().place;
	}

}
