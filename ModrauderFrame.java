import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.ini4j.*;
import org.ini4j.Profile.Section;
import com.google.gson.*;


public class ModrauderFrame extends JFrame {

	JButton installBT;
    String gameVersion;
    int modApi;
    String modsProvider;

    File modsFolder = new File(System.getenv("APPDATA") + "\\.minecraft\\mods");
    boolean delete = false;

    ArrayList<String> log = new ArrayList<>();

    public ModrauderFrame() {

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("Config.mc");
            Ini config = new Ini(is);

            Section defaults = config.get("Default");
            
            gameVersion = defaults.get("GameVersion", "1.20.1");

            modsProvider = defaults.get("ModsProvider", "Curseforge");

            if (defaults.get("ModdingAPI").equals("Forge")) {
                modApi = 1;

            } else if (defaults.get("ModdingAPI").equals("Fabric")) {
                modApi = 4;
            } else {
                log.add("Couldn't install ModdingAPI. REASON: No proper key found in config");
            }

            if (defaults.get("ClearPreviousMods").equals("true")) {
                delete = true;
            }

        } catch (IOException e) {
            log.add("Couldn't access config file\n"+e.getMessage());
        }


    }

    public void Render() {
        this.setSize(500, 250);
        this.setLocation(100, 100);
        this.setTitle("Modrauder Mod Installer - Made By @The Supreme Being");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.getContentPane().setBackground(new Color(0x121212));

        Font titleFont = new Font("Consolas", Font.PLAIN, 30);

        JLabel title = new JLabel("Modrauder Mod Installer");
        title.setFont(titleFont);
        title.setForeground(new Color(0xf5f5f5));
        title.setBounds(50, 10, 400, 38);
        this.add(title);

        Font barFont = new Font("Consolas", Font.PLAIN, 13);

        JTextArea area = new JTextArea();
        area.setFont(barFont);
        area.setEnabled(false);
        area.setBackground(new Color(0x181a1f));
        area.setBounds(0, 45, 500, 110);
        area.setText(String.join("\n", log));
        this.add(area);

        JProgressBar bar = new JProgressBar();
        bar.setFont(barFont);
        bar.setValue(0);
        bar.setBounds(0, 160, 500, 20);
        bar.setForeground(new Color(0x181a1f));
        bar.setStringPainted(true);
        bar.setVisible(false);
        this.add(bar);
        
        installBT = new JButton("Install");
        installBT.setFocusable(false);
        installBT.setBackground(Color.GRAY);
        installBT.setForeground(new Color(0xf5f5f5));
        installBT.setBounds(410, 170, 75, 40);
        installBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){

                if (modApi == 1) {
                    installForge();
                } else if (modApi == 4) {
                    
                    if (installFabric(gameVersion, System.getenv("APPDATA") + "\\.minecraft")) {
                        log.add(String.format("Installed Fabric for %s", gameVersion));
                        area.setText(String.join("\n", log));
                        bar.setValue(1);
                    
                    } else {
                        log.add(String.format("Error installing Fabric for %s", gameVersion));
                        area.setText(String.join("\n", log));                        
                    }

                } else {
                    log.add("Couldn't install ModdingAPI. REASON: No proper key found in config");
                    area.setText(String.join("\n", log));
                }

                bar.setVisible(true);

                if (modsFolder.exists() == false) {
                    modsFolder.mkdir();
                }

                if (delete == true) {
                    if (modsFolder.list() != null) {
                        for (File fileName : modsFolder.listFiles()) {
                            fileName.delete();
                        }
                    }
                    log.add("Deleted all previous mods.");
                    area.setText(String.join("\n", log));
                }

                bar.setValue(2);

                ArrayList<String> modIds = getModIds();                
                ArrayList<String> modNames = getModNames();

                double growth = 98/modIds.size();
                
                for (int i=0; i<modIds.size(); i++) {
                    String modId = modIds.get(i);
                    String modName = modNames.get(i);

                    if (downloadModFiles(modId, gameVersion, modApi, modsFolder, modsProvider)) {
                        bar.setValue(bar.getValue()+(int)growth);
                        log.add(String.format("Found and Installed %s for %s in %s", modName, gameVersion, modsProvider));
                        area.setText(String.join("\n", log));
                    } else {
                        log.add(String.format("Couldn't find %s for %s in %s", modName, gameVersion, modsProvider));
                        area.setText(String.join("\n", log));
                    }
                }

                bar.setValue(100);
                bar.setString("Done ;)");
                installBT.setText("Done ;)");
                installBT.setEnabled(false);

                log.add("All mods were installed");
                area.setText(String.join("\n", log));

            }
        });
        this.add(installBT);

        Font nameFont = new Font("Consolas", Font.PLAIN, 15);
        JLabel myName = new JLabel("Mod Installer Made By: @The Supreme Being");
        myName.setFont(nameFont);
        myName.setForeground(Color.GREEN);
        myName.setBounds(5, 170, 500, 40);
        this.add(myName);

        this.setLayout(null);
        this.setVisible(true);
    }

    public static void installForge() {

    }

    public static boolean installFabric(String gameVersion, String minecraftFolder) {
        String downloadLink = "https://maven.fabricmc.net/net/fabricmc/fabric-installer/0.11.2/fabric-installer-0.11.2.jar";
        try {

            URL downloadUrl = new URL(downloadLink);
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();

            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();

            FileOutputStream outputStream = new FileOutputStream("fabric_latest.jar");
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            connection.disconnect();

            String[] commandLine = new String[8];
            commandLine[0] = "java";
            commandLine[1] = "-jar";
            commandLine[2] = "fabric_latest.jar";
            commandLine[3] = "client";
            commandLine[4] = "-mcversion";
            commandLine[5] = gameVersion;
            commandLine[6] = "-dir";
            commandLine[7] = minecraftFolder;

            ProcessBuilder builder = new ProcessBuilder(commandLine);
            builder = builder.directory(new File("."));
            Process process = builder.start();

            process.waitFor();

            new File("fabric_latest.jar").delete();

            return true;

        } catch (IOException | InterruptedException e) {
            System.out.println(e);
            return false;
        }

    }

    public ArrayList<String> getModIds() {
        ArrayList<String> modIds = new ArrayList<>();

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("Config.mc");
            Ini config = new Ini(is);

            Section mods = config.get("Mods");

            for (String modName : mods.keySet()) {
                String modId = mods.get(modName, String.class);
                modIds.add(modId);
            }

        } catch (IOException error){
            System.err.println(error);
        }

        return modIds;
    }

    public ArrayList<String> getModNames() {
        ArrayList<String> modNames = new ArrayList<>();

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("Config.mc");
            Ini config = new Ini(is);

            Section mods = config.get("Mods");

            for (String modName : mods.keySet()) {
                modNames.add(modName);
            }

        } catch (IOException error){
            System.err.println(error);
        }

        return modNames;
    }    

    public static boolean downloadModFiles(String modId, String gameVersion, int modApi, File modFolder, String modsProvider) {
            URL downloadUrl = null;
            if (modsProvider.equals("Curseforge")) {
                try {
                    downloadUrl = new URL(String.format("https://api.curseforge.com/v1/mods/%s/files?gameVersion=%s&modLoaderType=%s&index=0&pageSize=1", modId, gameVersion, modApi));

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

                    String downloadLink = JsonParser.parseString(response.toString()).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("downloadUrl").getAsString();
                    String filename = JsonParser.parseString(response.toString()).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("fileName").getAsString();

                    inputStream.close();


                    // // pt2
                    URL fileLink = new URL(downloadLink);
                    connection = (HttpURLConnection) fileLink.openConnection();
                    connection.setRequestMethod("GET");

                    FileOutputStream outputStream = new FileOutputStream(modFolder.getAbsolutePath() + "\\" + filename);
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = connection.getInputStream().read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    connection.disconnect();

                    return true;

                } catch (IOException error) {
                    System.err.println(error);
                    return false;
                }
            } else if (modsProvider.equals("Modrinth")) {
                try {
                    if (modApi == 1) {
                        // forge
                    } else if (modApi == 4) {
                        // fabric
                        String baseUrl = "https://api.modrinth.com/v2/project/"+modId+"/version";
                        String query = "game_versions=[\""+gameVersion+"\"]";
                        String query1 = "loaders=[\"fabric\"]";

                        String encodedQuery = URLEncoder.encode(query, "UTF-8");
                        String encodedQuery1 = URLEncoder.encode(query1, "UTF-8");

                        String url = baseUrl+"?"+query+"&"+query1;
                        downloadUrl = new URL(url);

                        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("User-Agent", "unbandit2006/mod_installer");
                        connection.setRequestProperty("Accept", "application/json");

                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder response = new StringBuilder();

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        System.out.println(JsonParser.parseString(response.toString()));

                        connection.disconnect();
                    }
                    // TODO fixme

//                    HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
//
//                    connection.setRequestMethod("GET");
//                    connection.setRequestProperty("User-Agent", "unbandit2006/mod_installer");
//
//                    InputStream inputStream = connection.getInputStream();
//
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                    String line;
//                    StringBuilder response = new StringBuilder();
//
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
//                    reader.close();
//
//                    String downloadLink = JsonParser.parseString(response.toString()).getAsJsonArray().get(0).getAsJsonObject().get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
//                    String filename = JsonParser.parseString(response.toString()).getAsJsonArray().get(0).getAsJsonObject().get("files").getAsJsonArray().get(0).getAsJsonObject().get("filename").getAsString();
//
//                    inputStream.close();
//
//                    // pt2
//                    URL fileLink = new URL(downloadLink);
//                    connection = (HttpURLConnection) fileLink.openConnection();
//                    connection.setRequestMethod("GET");
//
//                    System.out.println(connection.getOutputStream());

//                    FileOutputStream outputStream = new FileOutputStream(modFolder.getAbsolutePath() + "\\" + filename);
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//
//                    while ((bytesRead = connection.getInputStream().read(buffer)) != -1) {
//                        outputStream.write(buffer, 0, bytesRead);
//                    }
//
//                    outputStream.close();

                    return true;

                } catch (IOException error) {
                    System.err.println(error);
                    return false;
                }
            } else {
                return false;
            }

    }

}
