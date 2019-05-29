package me.champ.zm.commands;

import org.bukkit.entity.Player;

import me.champ.zm.Core;
import net.md_5.bungee.api.ChatColor;

public class HelpCommand extends SubCommand {
	
	private Core plugin = Core.getInstance();

	@Override
	public void onCommand(Player player, String[] args) {
		
		player.sendMessage(" ");
		player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Zombie Mayhem" + ChatColor.WHITE + "(v0.1)");
		player.sendMessage(ChatColor.DARK_GRAY + "------------------");
		player.sendMessage(ChatColor.GREEN + "/zm help");
		player.sendMessage(ChatColor.GREEN + "/zm setspawn (game name/lobby)");
		player.sendMessage(ChatColor.GREEN + "/zm place (sign/shop)");
		player.sendMessage(ChatColor.GREEN + "/zm create (game name)");
		player.sendMessage(ChatColor.GREEN + "/zm join (game name)");

	}

	@Override
	public String name() {
		return plugin.getCommandHandler().help;
	}

}
