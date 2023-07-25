package me.unbandit.Tools;

import org.ini4j.Ini;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigReader {

    public String gameVersion;
    public String modApi;
    public String modsProvider;
    public String clearMods;

    public ArrayList<String> modIds = new ArrayList<>();
    public ArrayList<String> modNames = new ArrayList<>();

    public ConfigReader() throws IOException{
        // Get Values from Config
        Ini configReader = new Ini();
        configReader.load(new FileReader("Mod-Installer\\src\\Config.mc"));

        gameVersion = configReader.get("Default", "GameVersion");
        modApi = configReader.get("Default", "ModdingAPI");
        modsProvider = configReader.get("Default", "ModsProvider");
        clearMods = configReader.get("Default", "ClearPreviousMods");

        // Get mod Ids from Config
        for (String modName : configReader.get("Mods").keySet()) {
            modNames.add(modName);
            modIds.add(configReader.get("Mods").get(modName));
        }
    }
}
