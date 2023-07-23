import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.util.ArrayList;

import org.ini4j.*;
import org.ini4j.Profile.Section;
import com.google.gson.*;


public class ModrauderFrame extends JFrame {

	JButton installBT;
    String gameVersion;
    int modApi;

    File modsFolder = new File(System.getenv("APPDATA") + "\\.minecraft\\mods");
    boolean delete = false;

    ArrayList<String> log = new ArrayList<>();

    public ModrauderFrame() {

        try {
            Ini config = new Ini(new File("Config.mc"));

            Section defaults = config.get("Default");
            
            gameVersion = defaults.get("GameVersion", "1.20.1");

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
            log.add("Counldn't access config file");
        }


    }

    public void Render() {
        this.setSize(500, 250);
        this.setLocation(500, 500);
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
        area.setBounds(0, 40, 484, 110);
        area.setText(String.join("\n", log));
        this.add(area);

        JProgressBar bar = new JProgressBar();
        bar.setFont(barFont);
        bar.setValue(0);
        bar.setBounds(0, 150, 484, 20);
        bar.setForeground(new Color(0x181a1f));
        bar.setStringPainted(true);
        bar.setVisible(false);
        this.add(bar);
        
        JButton installBT = new JButton("Install");
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

                if (delete == true) {
                    if (modsFolder.list() != null) {
                        for (File fileName : modsFolder.listFiles()) {
                            fileName.delete();
                        }
                    }
                    modsFolder.delete();
                    modsFolder.mkdir();
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

                    if (downloadModFiles(modId, gameVersion, modApi, modsFolder)) {
                        bar.setValue(bar.getValue()+(int)growth);
                        log.add(String.format("Found and Installed %s for %s", modName, gameVersion));
                        area.setText(String.join("\n", log));
                    } else {
                        log.add(String.format("Couldn't find %s for %s", modName, gameVersion));
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
        myName.setBounds(5, 170, 400, 40);
        this.add(myName);

        this.setLayout(null);
        this.setVisible(true);
    }

    public static void installForge() {

    }

    public static boolean installFabric(String gameVersion, String minecraftFolder) {
        String downloadLink = "https://maven.fabricmc.net/net/fabricmc/fabric-installer/0.11.2/fabric-installer-0.11.2.jar";
        try {
            HttpRequest fileGetRequest = HttpRequest.newBuilder()
                .uri(new URI(downloadLink))
                .GET()
                .build();

            HttpClient client = HttpClient.newHttpClient();
            client.send(fileGetRequest, BodyHandlers.ofFile(Path.of("fabric_latest.jar")));

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

            return true;
        
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println(e);
            return false;
        }

    }

    public static ArrayList<String> getModIds() {
        ArrayList<String> modIds = new ArrayList<>();

        try {
            Ini config = new Ini(new File("Config.mc"));

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

    public static ArrayList<String> getModNames() {
        ArrayList<String> modNames = new ArrayList<>();

        try {
            Ini config = new Ini(new File("Config.mc"));

            Section mods = config.get("Mods");

            for (String modName : mods.keySet()) {
                modNames.add(modName);
            }

        } catch (IOException error){
            System.err.println(error);
        }

        return modNames;
    }    

    public static boolean downloadModFiles(String modId, String gameVersion, int modApi, File modFolder) {
        HttpRequest getRequest;
        HttpResponse<String> getResponse = null;
        try {
            getRequest = HttpRequest.newBuilder()
                .uri(new URI(String.format("https://api.curseforge.com/v1/mods/%s/files?gameVersion=%s&modLoaderType=%s&index=0&pageSize=1", modId, gameVersion, modApi)))
                .header("Accept", "application/json")
                .header("x-api-key", "$2a$10$cxwv/oii490ru.VvB5Tp2OEU2WOO0li0cEVdhEOpIwfAI950iWg/q")
                .GET()
                .build();
            
            HttpClient client = HttpClient.newHttpClient();
        
            getResponse = client.send(getRequest, BodyHandlers.ofString());

            String downloadLink = JsonParser.parseString(getResponse.body().toString()).getAsJsonObject().get("data")
                                    .getAsJsonArray().get(0)
                                    .getAsJsonObject().get("downloadUrl").getAsString();
            
            String fileName = JsonParser.parseString(getResponse.body().toString()).getAsJsonObject().get("data")
                                .getAsJsonArray().get(0)
                                .getAsJsonObject().get("fileName").getAsString();

            HttpRequest fileGetRequest = HttpRequest.newBuilder()
                .uri(new URI(downloadLink))
                .GET()
                .build();
            
            HttpResponse<String> fileGetResponse = client.send(fileGetRequest, BodyHandlers.ofString());
            
            URI newFileLink = new URI(fileGetResponse.headers().allValues("location").get(0));
            HttpRequest newFileRequest = HttpRequest.newBuilder()
                .uri(newFileLink)
                .GET()
                .build();
            
            client.send(newFileRequest, BodyHandlers.ofFile(Path.of(fileName)));

            File mod = new File(fileName);
            mod.renameTo(new File(modFolder+"\\"+mod.getName()));

            return true;

        } catch (URISyntaxException | IOException | InterruptedException  error) {
           return false;
        }
    }

}
