package me.champ.zm.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import me.champ.zm.Core;
import me.champ.zm.data.DataHandler;
import me.champ.zm.objects.Game;
import me.champ.zm.objects.Game.State;
import me.champ.zm.utils.Util;

public class GameListener implements Listener {
	
	Core plugin = Core.getInstance();
	
	@EventHandler
	public void onJoinGameWorld(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		
		if (DataHandler.getGameFile().contains("games." + event.getTo().getWorld().getName())) {
			if (event.getTo().getWorld().getPlayers().size() + 1 == plugin.getMaxPlayers()) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.GRAY + "[!] " + ChatColor.RED + "The game is full!");
			} else if (event.getTo().getWorld().getPlayers().size() + 1 >= plugin.getMinimumPlayers() && event.getTo().getWorld().getPlayers().size() + 1 <= plugin.getMaxPlayers()) {
				String gameName = event.getTo().getWorld().getName();
				for (Player p : event.getTo().getWorld().getPlayers()) {
					p.sendMessage(ChatColor.GRAY + "[+] " + p.getName() + " joined the game " + ChatColor.YELLOW + "(" + ChatColor.AQUA + event.getTo().getWorld().getPlayers().size() + ChatColor.YELLOW + "/" + ChatColor.AQUA + plugin.getMaxPlayers() + ChatColor.YELLOW + ")");
				}
				player.sendMessage(ChatColor.GRAY + "[+] " + player.getName() + " joined the game " + ChatColor.YELLOW + "(" + ChatColor.AQUA + event.getTo().getWorld().getPlayers().size() + ChatColor.YELLOW + "/" + ChatColor.AQUA + plugin.getMaxPlayers() + ChatColor.YELLOW + ")");
				if (plugin.getGame(gameName) != null && Bukkit.getWorld(gameName).getPlayers().size() + 1  == plugin.getMinimumPlayers()) {
					Game game = plugin.getGame(gameName);
					game.startLobbyTimer();
				}
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		event.setDeathMessage("");
		event.getEntity().sendMessage(ChatColor.GRAY + "[*]" + ChatColor.YELLOW + " You have been escorted back to the lobby");
		Player player = event.getEntity();
		String worldName = plugin.getConfig().getString("lobby-world.name");
		World world = Bukkit.getWorld(worldName);
		double x = plugin.getConfig().getDouble(worldName + ".spawn.x");
		double y = plugin.getConfig().getDouble(worldName + ".spawn.y");
		double z = plugin.getConfig().getDouble(worldName + ".spawn.z");
		Location location = new Location(world, x, y, z);
		player.teleport(location);
		
		if (event.getEntity().getKiller() instanceof Player) {
			if (world.getPlayers().size() == 1) {
				Player killer = event.getEntity().getKiller();
				killer.sendMessage(" ");
				killer.sendMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + "You are the last player alive! ");
				killer.sendMessage(ChatColor.YELLOW + "Time remaining: " + ChatColor.GOLD + "");
			}
		}
	
		if (world.getPlayers().size() == 0) {
			Game game = plugin.getGame(player.getWorld().getName());
			game.setState(State.END);
		}
		
	}
	
	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			if (event.getEntity() instanceof Player) {
				Player damagedPlayer = (Player) event.getEntity();
				Game game = plugin.getGame(damager.getWorld().getName());
				if (game != null) {
					if (damager.getWorld() == Bukkit.getWorld(game.getName())) {
						if (game.isState(State.START)) {
							event.setCancelled(true);
						}
					}
				}
				
			}
		} 
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			World world = player.getWorld();
			Game game = plugin.getGame(world.getName());
			if (world.getName().equalsIgnoreCase(plugin.getLobbyWorld())) {
				event.setCancelled(true);
			}
			
			if (game != null) {
				if (game.isState(State.START)) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Zombie) {
			Zombie zombie = (Zombie) event.getEntity();
			if (zombie.hasMetadata("Level 1")) {
				Random random = new Random();
				ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
				ItemStack coin = new ItemStack(Material.EYE_OF_ENDER);
				for (int i = 0; i < Util.getRandomNumberInRange(random, 3, 6); i++) {
					drops.add(coin);
				}
				event.getDrops().clear();
				event.getDrops().addAll(drops);
				drops.clear();
				
			}
		}
	}

}
