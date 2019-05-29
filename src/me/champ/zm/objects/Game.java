package me.champ.zm.objects;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import me.champ.zm.Core;

public class Game {
	
	private Core plugin = Core.getInstance();
	
	public World world;
	public String name;
	public int lobbyTimer = 11;
	public int gameTimer = plugin.getGameTimer();
	
	private State state = State.LOBBY;
	
	public enum State {
		LOBBY, START, ACTIVE, END;
	}
	
	public Game(String name) {
		this.name = name;
		world = Bukkit.getWorld(name);
	}
	
	public void create() {
		WorldCreator wc = new WorldCreator(name);
		wc.type(WorldType.FLAT);
		wc.createWorld();
	}
	
	public boolean exists() {
		File file = new File(plugin.getServer().getWorldContainer(), name);
		if (!(file.exists())) {
			return false;
		} else {
			return true;
		}
		
	}
	
	public boolean isState(State state) {
		if (this.getState() == state) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public State getState() {
		return state;
	}
	
	public String getName() {
		return name;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void startLobbyTimer() {
		setState(state.START);
		new BukkitRunnable() {

			@Override
			public void run() {
				lobbyTimer -= 1;
				if (lobbyTimer == 10) {
					for (Player player : Bukkit.getWorld(name).getPlayers()) {
						player.sendMessage(ChatColor.GRAY + "[*]" + ChatColor.GREEN + " The game will be starting in " + ChatColor.YELLOW + lobbyTimer + ChatColor.GREEN + " seconds.");
					}
				}
				
				if (lobbyTimer <= 5) {
					for (Player player : Bukkit.getWorld(name).getPlayers()) {
						player.sendMessage(ChatColor.GRAY + "[*]" + ChatColor.GREEN + " The game will be starting in " + ChatColor.YELLOW + lobbyTimer + ChatColor.GREEN + " seconds.");
					}
				}
				
				if (lobbyTimer == 0) {
					setState(state.ACTIVE);
					
					for (Player player : Bukkit.getWorld(name).getPlayers()) {
						player.sendMessage(ChatColor.GREEN + " ");
						player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The game has started!");
						setupEntities(player);
						startGameTimer();
					}
					this.cancel();
					lobbyTimer = 11;
				}
				
			}
			
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void startGameTimer() {
		new BukkitRunnable() {

			@Override
			public void run() {
				gameTimer -= 1;
				
				if (Bukkit.getWorld(getName()).getPlayers().size() == 0) {
					setState(state.END);
					this.cancel();
				}
				
			}
			
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void setupEntities(Player player) {
		Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
		Zombie zombie = (Zombie) player.getWorld().spawnEntity(location.add(10, 0, 10), EntityType.ZOMBIE);
		zombie.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "[Level 1]");
		zombie.setCustomNameVisible(true);
		zombie.setMetadata("Level 1", new FixedMetadataValue(plugin, "lvl1"));
	}

}
