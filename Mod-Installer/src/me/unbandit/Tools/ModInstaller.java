package me.unbandit.Tools;

import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ModInstaller {

    private String modPath;

    public boolean ClearAndOrMakeMods(boolean decider, ArrayList<String> logText, JTextArea log) {
        boolean returnValue = false;

        File mods = new File(System.getenv("APPDATA") + "\\.minecraft\\mods");

        if (mods.exists() == true) {
            if (decider) {
                for (File mod : mods.listFiles()) {
                    mod.delete();
                    logText.add(String.format("Deleted %s from mods folder\n", mod));
                    log.setText(String.join("", logText));
                }
                returnValue = true;
            }
        } else {
            mods.mkdir();
            logText.add("Made mods folder\n");
            log.setText(String.join("", logText));
            returnValue = true;
        }

        return returnValue;
    }

    public boolean InstallCurseforgeMod(String modId, String gameVersion, String modApi) {
        try {
            URL downloadUrl = new URL(String.format("https://api.curseforge.com/v1/mods/%s/files?gameVersion=%s&modLoaderType=%s&index=0&pageSize=1", modId, gameVersion, modApi));
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("x-api-key", "$2a$10$cxwv/oii490ru.VvB5Tp2OEU2WOO0li0cEVdhEOpIwfAI950iWg/q");

            InputStream inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println(JsonParser.parseString(response.toString()));
            System.out.println(modId);

            String downloadLink = JsonParser.parseString(response.toString()).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("downloadUrl").getAsString();
            String filename = JsonParser.parseString(response.toString()).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("fileName").getAsString();

            inputStream.close();

            // // pt2
            URL fileLink = new URL(downloadLink);
            connection = (HttpURLConnection) fileLink.openConnection();
            connection.setRequestMethod("GET");

            File modFolder = new File(System.getenv("APPDATA") + "\\.minecraft\\mods");
            FileOutputStream outputStream = new FileOutputStream(modFolder.getAbsolutePath()+"\\"+filename);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = connection.getInputStream().read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            connection.disconnect();

            return true;

        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean InstallModrinthMod(String modId, String gameVersion, String modApi) {
        try {
            String baseUrl = "https://api.modrinth.com/v2/project/"+modId+"/version";

            String gameVersionQuery = "\""+gameVersion+"\"";
            String gameVersionEncoded = URLEncoder.encode(gameVersionQuery, "UTF-8");

            String loadersQuery = "\""+modApi.toLowerCase()+"\"";
            String loadersEncoded = URLEncoder.encode(loadersQuery, "UTF-8");

            URL downloadUrl = new URL(baseUrl+"?game_versions=["+gameVersionEncoded+"]&loaders=["+loadersEncoded+"]");
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "unbandit2006/mod_installer");
            connection.setRequestProperty("Accept", "application/json");

            InputStream inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            inputStream.close();

            System.out.println(JsonParser.parseString(response.toString()));
            System.out.println(modId);

            String downloadLink = JsonParser.parseString(response.toString()).getAsJsonArray().get(0).getAsJsonObject().get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
            String filename = JsonParser.parseString(response.toString()).getAsJsonArray().get(0).getAsJsonObject().get("files").getAsJsonArray().get(0).getAsJsonObject().get("filename").getAsString();

            // pt 2
            URL modFileUrl = new URL(downloadLink);
            connection = (HttpURLConnection) modFileUrl.openConnection();

            connection.setRequestMethod("GET");

            InputStream inputStream1 = connection.getInputStream();

            byte[] buffer = new byte[4096];
            FileOutputStream modFile = new FileOutputStream(System.getenv("APPDATA") + "\\.minecraft\\mods"+"\\"+filename);
            int bytesRead;

            while ((bytesRead = inputStream1.read(buffer)) != -1) {
                modFile.write(buffer, 0, bytesRead);
            }
            modFile.close();
            inputStream1.close();

            connection.disconnect();

            return true;

        }  catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
