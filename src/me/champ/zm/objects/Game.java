package me.champ.zm.objects;

import java.io.File;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import me.champ.zm.Core;
import me.champ.zm.data.DataHandler;
import me.champ.zm.objects.Game.State;
import me.champ.zm.utils.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R1.PacketPlayOutChat;

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
		removeEntities();
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
		Game game = this;
		BossBar bossBar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', plugin.getBossBarMessage()), BarColor.WHITE, BarStyle.SOLID, BarFlag.CREATE_FOG);
		for (Player player : Bukkit.getWorld(getName()).getPlayers()) {
			bossBar.addPlayer(player);
		}
		new BukkitRunnable() {
			String[] colors = {"WHITE", "PINK"};
			int i = 0;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				game.gameTimer -= 1;
				int minute = game.gameTimer / 60;
				int seconds = game.gameTimer % 60;
				if (i >= colors.length) {
					i = 0;
				}
				bossBar.setColor(BarColor.valueOf(colors[i]));
				i++;
				
				String message = ChatColor.GOLD + String.valueOf(minute) + ":" + String.valueOf(seconds);
				
				if (seconds == 0) {
					message = ChatColor.GOLD + String.valueOf(minute) + ":" + String.valueOf(seconds) + "0";
				} else if (seconds < 10) {
					message = ChatColor.GOLD + String.valueOf(minute) + ":" + "0" + String.valueOf(seconds);
				}
				String jsonMesg = "{\"text\":\"" + message + "\"}";
				
				for (Player player : Bukkit.getWorld(getName()).getPlayers()) {
					Util.sendActionBar(player, jsonMesg);
				}
				
				if (minute == 0 && seconds == 0) {
					this.cancel();
					Bukkit.getWorld(getName()).getPlayers().stream().forEach((Player player) -> player.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Game Over", ChatColor.GRAY + "Teleporting to lobby.."));
					setState(state.END);
					Location location = new Location(Bukkit.getWorld(plugin.getLobbyWorld()), DataHandler.getGameFile().getDouble("lobby-world.spawn.x"), DataHandler.getGameFile().getDouble("lobby-world.spawn.y"), DataHandler.getGameFile().getDouble("lobby-world.spawn.z"));
					Bukkit.getWorld(getName()).getPlayers().stream().forEach((Player player) -> player.teleport(location));
					removeEntities();
					bossBar.removeAll();
					gameTimer = plugin.getGameTimer();
				}
				if (Bukkit.getWorld(getName()).getPlayers().size() == 0) {
					this.cancel();
					setState(state.END);
					removeEntities();
					bossBar.removeAll();
					gameTimer = plugin.getGameTimer();
				}	
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void setupEntities(Player player) {
		Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
		Zombie zombie = (Zombie) player.getWorld().spawnEntity(location.add(10, 0, 10), EntityType.ZOMBIE);
		zombie.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "[Level 1]");
		zombie.setCustomNameVisible(true);
		zombie.setBaby(false);
		zombie.setMetadata("Level 1", new FixedMetadataValue(plugin, "lvl1"));
	}
	
	public void removeEntities() {
		for (Entity e : Bukkit.getWorld(getName()).getEntities()) {
			if (!(e instanceof Player)){
				e.remove();
			}
		}
	}
	
	public void spawnBabyZombie(Game game, World world, Location location, Player damager, String name) {
		if (world.getName().equals(game.getName())) {
			if (game.isState(State.ACTIVE)) {
				Zombie babyZombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
				babyZombie.setBaby(true);
				babyZombie.setCustomName(name);
				babyZombie.setCustomNameVisible(true);
				babyZombie.setVillager(false);
				babyZombie.setTarget(damager);
				babyZombie.getEquipment().setHelmet(new ItemStack(Material.TNT));
				babyZombie.setMetadata("Exploding Baby", new FixedMetadataValue(plugin, "baby"));
			}
		}
	}

}
