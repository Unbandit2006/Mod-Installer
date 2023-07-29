package me.unbandit.Tools;

import org.ini4j.Ini;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ConfigReader {

    public String gameVersion;
    public String modApi;
    public boolean clearMods;

    public ArrayList<String> modNamesCurseforge = new ArrayList<>();
    public ArrayList<String> modIdsCurseforge = new ArrayList<>();

    public ArrayList<String> modNamesModrinth = new ArrayList<>();
    public ArrayList<String> modIdsModrinth = new ArrayList<>();

    public Ini configReader;

    public InputStream file = this.getClass().getResourceAsStream("/Config.mc");

    public ConfigReader() {
        // Get Values from Config
        configReader = new Ini();
    }

    public boolean read() {

        try {
            configReader.load(new BufferedReader(new InputStreamReader(file)));

            gameVersion = configReader.get("Default", "GameVersion");
            modApi = configReader.get("Default", "ModdingAPI");
            clearMods = Boolean.parseBoolean(configReader.get("Default", "ClearPreviousMods"));

            // Get mod Ids from Config for Curseforge
            for (String modName : configReader.get("CurseforgeMods").keySet()) {
                modNamesCurseforge.add(modName);
                modIdsCurseforge.add(configReader.get("CurseforgeMods").get(modName));
            }

            // Get mod Ids from Config for Modrinth
            for (String modName : configReader.get("ModrinthMods").keySet()) {
                modNamesModrinth.add(modName);
                modIdsModrinth.add(configReader.get("ModrinthMods").get(modName));
            }

            return true;

        } catch (IOException error) {
            return false;
        }
    }
}
