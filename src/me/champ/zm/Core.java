package me.champ.zm;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;

import org.bukkit.plugin.java.JavaPlugin;

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
	
	public Set<Game> games = new HashSet<Game>();
	
	@Override
	public void onEnable() {
		plugin = this;
		
		ch = new CommandHandler();
		
		getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveDefaultConfig();
        
        DataHandler.setup();
        loadGames();
        this.registerEvents();
        
        System.out.println("`````` Zombie Mayhem ``````");
        System.out.println(" ");
        System.out.println("          Enabled      ");
        System.out.println(" ");
        System.out.println("```````````````````````````");
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
	
	public void loadGames() {
		if (DataHandler.getGameFile().getConfigurationSection("games") != null) {
			for (String gameName : DataHandler.getGameFile().getConfigurationSection("games").getKeys(false)) {
				Game game = new Game(gameName);
				addGame(game);
				
			}
			System.out.println(ChatColor.translateAlternateColorCodes('&', "&eLoaded games succesfully!")  );
		} else {
			System.out.println("No games to load.");
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

}
