package me.unbandit.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class GUI extends JFrame{

    private final Color backgroundColor = new Color(0x121212);
    private final Color foregroundColor = new Color(0x181a1f);
    private final Color textColor = new Color(0xF5F5F5);

    private final ConfigReader configReader = new ConfigReader();

    private final ModInstaller modInstaller = new ModInstaller();

    public GUI() {
        this.setTitle("Mod Installer - Made By @TheSupremeBeing");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(500, 100, 500, 600);
        this.setResizable(false);
        this.getContentPane().setBackground(backgroundColor);

    }

    public void Render() {

        if (configReader.read() != true) {
            JOptionPane.showMessageDialog(null, "Unable to read "+configReader.file, "Couldn't Open Config File", JOptionPane.ERROR_MESSAGE);
        }

        Font titleFont = new Font("Consolas", Font.PLAIN, 30);

        JLabel title = new JLabel("Modrauder Mod Installer");
        title.setFont(titleFont);
        title.setForeground(new Color(0xf5f5f5));
        title.setBounds(50, 10, 400, 38);
        this.add(title);

        JTextArea log = new JTextArea();
        ArrayList<String> logText = new ArrayList<>();
        log.setBackground(this.foregroundColor);
        log.setBounds(0, 50, this.getWidth(), this.getHeight()-100-10-title.getHeight());
        log.setEnabled(false);
        this.add(log);

        JButton installBT = new JButton();
        installBT.setText("Install");
        installBT.setBackground(new Color(0x2c313a));
        installBT.setForeground(this.textColor);
        installBT.setBounds(10, 50+log.getHeight()+5, 492-20, 45-10);
        installBT.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (new ModLoader(configReader.modApi, configReader.gameVersion).Install()) {
                            logText.add(String.format("Found and Installed %s for %s \n", configReader.modApi, configReader.gameVersion));
                            log.setText(String.join("", logText));
                        } else {
                            logText.add(String.format("ERROR: Couldn't install %s for %s \n", configReader.modApi, configReader.gameVersion));
                            log.setText(String.join("", logText));
                        }
                        modInstaller.ClearAndOrMakeMods(configReader.clearMods, logText, log);
                        InstallAllMods(log, logText, configReader);

                        installBT.setEnabled(false);
                        installBT.setText("Done ;)");
                    }
                }
        );
        this.add(installBT);

        // Menubar
        JMenuBar menubar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem installAllItem = new JMenuItem("Install All Mods");
        installAllItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (new ModLoader(configReader.modApi, configReader.gameVersion).Install()) {
                            logText.add(String.format("Found and Installed %s for %s \n", configReader.modApi, configReader.gameVersion));
                            log.setText(String.join("", logText));
                        } else {
                            logText.add(String.format("ERROR: Couldn't install %s for %s \n", configReader.modApi, configReader.gameVersion));
                            log.setText(String.join("", logText));
                        }
                        modInstaller.ClearAndOrMakeMods(configReader.clearMods, logText, log);
                        InstallAllMods(log, logText, configReader);
                    }
                }
        );
        fileMenu.add(installAllItem);

        JMenuItem installCurseItem = new JMenuItem("Install Only Curseforge Mods");
        installCurseItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (new ModLoader(configReader.modApi, configReader.gameVersion).Install()) {
                            logText.add(String.format("Found and Installed %s for %s \n", configReader.modApi, configReader.gameVersion));
                            log.setText(String.join("", logText));
                        } else {
                            logText.add(String.format("ERROR: Couldn't install %s for %s \n", configReader.modApi, configReader.gameVersion));
                            log.setText(String.join("", logText));
                        }

                        modInstaller.ClearAndOrMakeMods(configReader.clearMods, logText, log);
                        for (String modId : configReader.modIdsCurseforge) {
                            for (String modName : configReader.modNamesCurseforge) {
                                if (modInstaller.InstallCurseforgeMod(modId, configReader.gameVersion, configReader.modApi) == true) {
                                    logText.add(String.format("Found and Installed %s for %s on %s \n", modName, configReader.gameVersion, configReader.modApi));
                                    log.setText(String.join("", logText));
                                } else {
                                    logText.add(String.format("ERROR: Couldn't Find %s for %s on %s \n", modName, configReader.gameVersion, configReader.modApi));
                                    log.setText(String.join("", logText));
                                }
                            }
                        }
                    }
                }
        );
        fileMenu.add(installCurseItem);

        JMenuItem installFabricItem = new JMenuItem("Install Only Modrinth Mods");
        installFabricItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (new ModLoader(configReader.modApi, configReader.gameVersion).Install()) {
                            logText.add(String.format("Found and Installed %s for %s \n", configReader.modApi, configReader.gameVersion));
                            log.setText(String.join("", logText));
                        } else {
                            logText.add(String.format("ERROR: Couldn't install %s for %s \n", configReader.modApi, configReader.gameVersion));
                            log.setText(String.join("", logText));
                        }

                        modInstaller.ClearAndOrMakeMods(configReader.clearMods, logText, log);
                        for (String modId : configReader.modIdsModrinth) {
                            for (String modName : configReader.modNamesModrinth) {
                                if (modInstaller.InstallModrinthMod(modId, configReader.gameVersion, configReader.modApi) == true) {
                                    logText.add(String.format("Found and Installed %s for %s on %s \n", modName, configReader.gameVersion, configReader.modApi));
                                    log.setText(String.join("", logText));
                                } else {
                                    logText.add(String.format("ERROR: Couldn't Find %s for %s on %s \n", modName, configReader.gameVersion, configReader.modApi));
                                    log.setText(String.join("", logText));
                                }
                            }
                        }
                    }
                }
        );
        fileMenu.add(installFabricItem);

        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        System.exit(0);
                    }
                }
        );
        fileMenu.add(quitItem);

        JMenuItem getSourceItem = new JMenuItem("Get Source");
        getSourceItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/Unbandit2006/Mod-Installer"));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (URISyntaxException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
        );
        helpMenu.add(getSourceItem);

//        JMenuItem version = new JMenuItem("Version");
//        version.addActionListener(
//                new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        JOptionPane versionPanel = new JOptionPane();
//                        versionPanel.
//                    }
//                }
//        );
//        helpMenu.add(version);

        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        menubar.add(fileMenu);
        menubar.add(helpMenu);

        this.setJMenuBar(menubar);

        this.setLayout(null);
        this.setVisible(true);
    }

    private void InstallAllMods(JTextArea log, ArrayList<String> logText, ConfigReader configReader) {
        for (String modId : configReader.modIdsCurseforge) {
            for (String modName : configReader.modNamesCurseforge) {
                if (modInstaller.InstallCurseforgeMod(modId, configReader.gameVersion, configReader.modApi) == true) {
                    logText.add(String.format("Found and Installed %s for %s on %s \n", modName, configReader.gameVersion, configReader.modApi));
                    log.setText(String.join("", logText));
                } else {
                    logText.add(String.format("ERROR: Couldn't Find %s for %s on %s \n", modName, configReader.gameVersion, configReader.modApi));
                    log.setText(String.join("", logText));
                }
            }
        }

        for (String modId : configReader.modIdsModrinth) {
            for (String modName : configReader.modNamesModrinth) {
                if (modInstaller.InstallModrinthMod(modId, configReader.gameVersion, configReader.modApi) == true) {
                    logText.add(String.format("Found and Installed %s for %s on %s \n", modName, configReader.gameVersion, configReader.modApi));
                    log.setText(String.join("", logText));
                } else {
                    logText.add(String.format("ERROR: Couldn't Find %s for %s on %s \n", modName, configReader.gameVersion, configReader.modApi));
                    log.setText(String.join("", logText));
                }
            }
        }

        logText.add("Done ;)");
        log.setText(String.join("", logText));

    }


}
