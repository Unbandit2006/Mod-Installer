package me.unbandit.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModLoader {

    private final String modLoader;
    private final String gameVersion;
    private final File minecraftFolder;

    public ModLoader(String whatKind, String gameVersion) {
        this.modLoader = whatKind;
        this.gameVersion = gameVersion;
        this.minecraftFolder = new File(System.getenv("APPDATA") + "\\.minecraft");
    }

    public boolean Install() {
        if (this.modLoader.equals("Fabric")) {

            return InstallFabric(this.gameVersion, this.minecraftFolder);

        } else {
            return false;
        }
    }

    private static boolean InstallFabric(String gameVersion, File minecraftFolder) {
        try {
            // Download Fabric Installer
            URL fabricDownload = new URL("https://maven.fabricmc.net/net/fabricmc/fabric-installer/0.11.2/fabric-installer-0.11.2.jar");
            HttpURLConnection connection = (HttpURLConnection) fabricDownload.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            FileOutputStream file = new FileOutputStream("Fabric.jar");

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                file.write(buffer, 0, bytesRead);
            }

            file.close();
            inputStream.close();
            connection.disconnect();

            // Run Fabric Installer
            String[] cmd = new String[8];
            cmd[0] = "java";
            cmd[1] = "-jar";
            cmd[2] = "Fabric.jar";
            cmd[3] = "client";
            cmd[4] = "-mcversion";
            cmd[5] = gameVersion;
            cmd[6] = "-dir";
            cmd[7] = minecraftFolder.getAbsolutePath();

            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.directory(new File("."));
            Process process = builder.start();

            process.waitFor();

            new File("Fabric.jar").delete();

            return true;

        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

}
