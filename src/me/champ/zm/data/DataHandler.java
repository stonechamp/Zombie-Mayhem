package me.champ.zm.data;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.champ.zm.Core;

public class DataHandler {
	
	private static Core plugin = Core.getInstance();
	
	private static File file;
	private static FileConfiguration gameFile;
	
	public static void setup() {
		file = new File(plugin.getDataFolder(), "games.yml");
		
		if (!file.exists()) {
            try {
            	plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		gameFile = YamlConfiguration.loadConfiguration(file);
	}
	
	
	public static FileConfiguration getGameFile() {
		return gameFile;
	}
	
	public static void saveGameConfig() {
		try {
			gameFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
