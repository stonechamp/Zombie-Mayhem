package me.champ.zm.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

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
					p.sendMessage(ChatColor.GRAY + "[+] " + p.getName() + " joined the game " + ChatColor.YELLOW + "(" + ChatColor.AQUA + (event.getTo().getWorld().getPlayers().size() + 1) + ChatColor.YELLOW + "/" + ChatColor.AQUA + plugin.getMaxPlayers() + ChatColor.YELLOW + ")");
				}
				player.sendMessage(ChatColor.GRAY + "[+] " + player.getName() + " joined the game " + ChatColor.YELLOW + "(" + ChatColor.AQUA + (event.getTo().getWorld().getPlayers().size() + 1) + ChatColor.YELLOW + "/" + ChatColor.AQUA + plugin.getMaxPlayers() + ChatColor.YELLOW + ")");
				
				
				if (plugin.getGame(gameName) != null && Bukkit.getWorld(gameName).getPlayers().size() + 1  == plugin.getMinimumPlayers()) {
					Game game = plugin.getGame(gameName);
					game.startLobbyTimer();
				}
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (DataHandler.getGameFile().contains("games." + event.getEntity().getWorld().getName())){
			event.setDeathMessage("");
			event.getEntity().sendMessage(ChatColor.GRAY + "[*]" + ChatColor.YELLOW + " You have been escorted back to the lobby");
			Player player = event.getEntity();
			String worldName = plugin.getConfig().getString("lobby-world.name");
			World world = Bukkit.getWorld(worldName);
			double x = DataHandler.getGameFile().getDouble(worldName + ".spawn.x");
			double y = DataHandler.getGameFile().getDouble(worldName + ".spawn.y");
			double z = DataHandler.getGameFile().getDouble(worldName + ".spawn.z");
			Location location = new Location(world, x, y, z);
			player.teleport(location);
			
			if (event.getEntity().getKiller() instanceof Player) {
				if (world.getPlayers().size() == 1) {
					Game game = plugin.getGame(worldName);
					int minute = game.gameTimer / 60;
					int seconds = game.gameTimer % 60;
					Player killer = event.getEntity().getKiller();
					killer.sendMessage(" ");
					killer.sendMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + "You are the last player alive! ");
					killer.sendMessage(ChatColor.YELLOW + "Time remaining: " + ChatColor.GOLD + String.valueOf(minute) + ":" + String.valueOf(seconds));
				}
			}
		
			if (world.getPlayers().size() == 0) {
				Game game = plugin.getGame(player.getWorld().getName());
				game.setState(State.END);
			}
		}
		
		
	}
	
	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent event) {
		World world = event.getEntity().getWorld();
		Game game = plugin.getGame(world.getName());
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			if (event.getEntity() instanceof Player) {
				Player damagedPlayer = (Player) event.getEntity();
				if (game != null) {
					if (damager.getWorld() == Bukkit.getWorld(game.getName())) {
						if (game.isState(State.START)) {
							event.setCancelled(true);
						}
					}
				}
				
			} else if (event.getEntity() instanceof Zombie) {
				Zombie zombie = (Zombie) event.getEntity();
				if (zombie.hasMetadata("Level 1")){
					game.spawnBabyZombie(game, world, zombie.getLocation(), damager, ChatColor.RED + "Lvl 1");
				}
			} 
		} else if (event.getDamager() instanceof Zombie && event.getEntity() instanceof Player) {
			if (world.getName().equals(game.getName())) {
				if (event.getDamager().hasMetadata("Exploding Baby")) {
					world.createExplosion(event.getDamager().getLocation(), 1, true);
					event.getDamager().remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Game game = plugin.getGame(event.getEntity().getWorld().getName());
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (player.getWorld().getName().equalsIgnoreCase(plugin.getLobbyWorld())) {
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
	public void onCombust(EntityCombustEvent event) {
		Game game = plugin.getGame(event.getEntity().getWorld().getName());
		if (event.getEntity() instanceof Zombie){
			Zombie zombie = (Zombie) event.getEntity();
			if (zombie.hasMetadata("Level 1")) {
				if (zombie.getWorld().getName().equals(game.getName())) {
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
				Zombie newZom = (Zombie) zombie.getWorld().spawnEntity(zombie.getLocation(), EntityType.ZOMBIE);
				newZom.setMetadata("Level 1", new FixedMetadataValue(plugin, "lvl1"));
				
			}
		}
	}
	
	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) block.getState();
				if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "[Join]")) {
					String signName = sign.getLine(2);
					String array[] = signName.split("l");
					String gameName = array[1];
					double x = DataHandler.getGameFile().getDouble("games." + gameName + ".spawn.x");
					double y = DataHandler.getGameFile().getDouble("games." + gameName + ".spawn.y");
					double z = DataHandler.getGameFile().getDouble("games." + gameName + ".spawn.z");
					Location location = new Location(Bukkit.getWorld(gameName), x, y, z);

					player.teleport(location);
				}
			}
		}
	}
	
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event) {
		String worldName = event.getEntity().getWorld().getName();
		if (DataHandler.getGameFile().get("lobby-world.name").equals(worldName)) {
			event.setCancelled(true);
		} else if (DataHandler.getGameFile().contains("games." + worldName)) {
			if (event.getSpawnReason() != SpawnReason.CUSTOM) {
				event.setCancelled(true);
			}
		}
	}

}
