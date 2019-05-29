package me.champ.zm.commands;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.champ.zm.Core;

public class CommandHandler implements CommandExecutor {
	
	public ArrayList<SubCommand> commands = new ArrayList<SubCommand>();
	private Core plugin = Core.getInstance();
	
	public String base = "zm";
	public String help = "help";
	public String setspawn = "setspawn";
	public String create = "create";
	public String place = "place";
	public String join = "join";
	
	
	public CommandHandler() {
		setup();
	}
	
	public void setup() {
		plugin.getCommand(base).setExecutor(this);
		
		this.commands.add(new HelpCommand());
		this.commands.add(new SetSpawnCommand());
		this.commands.add(new CreateCommand());
		this.commands.add(new PlaceCommand());
		this.commands.add(new JoinCommand());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can run this command.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (command.getName().equalsIgnoreCase(base)) {
			
			if (args.length == 0) {
				player.performCommand("zm help");
				return true;
			}
			
			SubCommand sub = this.get(args[0]);
			
			if (sub == null) {
				player.sendMessage(ChatColor.RED + "Invalid command. Please use /zm for a list of valid commands.");
			}
			
            try {
            	sub.onCommand(player,args);
            } catch (Exception e){
            	//sender.sendMessage(ChatColor.RED + "Please use /zm for a list of valid commands.");
            }
            
            
		}	
		return true;
	}
	
	public SubCommand get(String name) {
		Iterator<SubCommand> subcommands = this.commands.iterator();
		
		while(subcommands.hasNext()) {
			SubCommand subCmd = subcommands.next();
			
			if (subCmd.name().equalsIgnoreCase(name)) {
				return subCmd;
			}
		}
		return null;
	}

}
