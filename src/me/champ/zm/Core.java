package me.champ.zm;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import me.champ.zm.commands.CommandHandler;
import me.champ.zm.data.DataHandler;
import me.champ.zm.listeners.GameListener;
import me.champ.zm.objects.Game;

public class Core extends JavaPlugin{
	
	public static Core plugin;
	public CommandHandler ch;
	
	public String prefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("message-prefix"));
	
	public int minPlayers = this.getConfig().getInt("minimum-players");
	public int maxPlayers = this.getConfig().getInt("maximum-players");
	public int gameTimer = this.getConfig().getInt("game-timer");
	public String lobbyWorld = this.getConfig().getString("lobby-world.name");
	public String bossBarMessage = this.getConfig().getString("boss-bar-message");
	
	public Set<Game> games = new HashSet<Game>();
	
	private ProtocolManager protocolManager;
	
	@Override
	public void onEnable() {
		plugin = this;
		protocolManager = ProtocolLibrary.getProtocolManager();
		ch = new CommandHandler();
		
		getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveDefaultConfig();
        
        DataHandler.setup();
        
        this.registerEvents();
        
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        
        loadGames(console);
        console.sendMessage(ChatColor.YELLOW + "``````" + ChatColor.RED + " Zombie Mayhem" + ChatColor.YELLOW + "``````");
        console.sendMessage(" ");
        console.sendMessage(ChatColor.GREEN + "          Enabled      ");
        console.sendMessage(" ");
        console.sendMessage(ChatColor.YELLOW + "```````````````````````````");
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void registerEvents() {
		this.getServer().getPluginManager().registerEvents(new GameListener(), this);
	}
	
	public static Core getInstance() {
		return plugin;
	}
	
	public void loadGames(ConsoleCommandSender console) {
		if (DataHandler.getGameFile().getConfigurationSection("games") != null) {
			for (String gameName : DataHandler.getGameFile().getConfigurationSection("games").getKeys(false)) {
				Game game = new Game(gameName);
				addGame(game);
				
				if (Bukkit.getWorld(game.getName()) == null){
					Bukkit.createWorld(new WorldCreator(game.getName()));
				}
				
			}
			console.sendMessage(ChatColor.GREEN + "Loaded games successfully");  
		} else {
			console.sendMessage(ChatColor.YELLOW + "No games to load.");
		}
	}
	
	public void addGame(Game game) {
		games.add(game);
	}
	
	public Set<Game> getGames(){
		return games;
	}
	
	public Game getGame(String name) {
		for (Game game : games) {
			if (game.getName().equals(name)) {
				return game;
			}
		}
		return null;
	}
	
	public String getBossBarMessage() {
		return bossBarMessage;
	}
	
	public String getLobbyWorld() {
		return lobbyWorld;
	}
	
	public CommandHandler getCommandHandler() {
		return ch;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public int getMinimumPlayers() {
		return minPlayers;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public int getGameTimer() {
		return gameTimer;
	}
	
	public ProtocolManager getProtocolLib() {
		return protocolManager;
	}

}
