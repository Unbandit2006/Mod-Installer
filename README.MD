# Mod-Installer
A simple mod installer for Minecraft. Any value between double quotes or "" has 
to be exactly as it is in between the quotes. 

## Config.mc
This is the main configuration file, it's similar to an INI file. It's similar
to an INI file because it works based on key=value pairs.

### Default Section
```ini
GameVersion = 1.20.1
ModdingAPI = Fabric
ModsLocation = Modrinth
ClearPreviousMods = true
```
<b>GameVersion</b></br>
Any valid Minecraft game version to install to

<b>ModdingAPI</b></br>
Modding api used to load mods into minecraft</br>
Currently only valid value is "Fabric"

<b>ModsLocation</b></br>
Location used to download mod files from</br> 
Valid values = "Modrinth" or "Curseforge"

<b>ClearPreviousMods</b></br>
Weather or not to delete all previous mods in the mods folder

### Mods Sections

#### CurseforgeMods
```ini
Wormhole = 396803
Botany Trees = 411357
Bamboo = 331723
Max Health Fix = 492246
Mouse Tweaks = 60089
Fabric API = 306612
```
Anything = ProjectID</br>
So its very simple what I mean. On the left side of the equals sign you must
provide the Mod name, preferably but the program will run if it's not. On the
right side of the ProjectID.</br>

#### ModrinthMods
```ini
Fabric API = P7dR8mSH
Dynamic Lights = yBW8D80W
Craftify = nrJ2NpD0
Create = Xbc0uyRg
Trains = XKPAmI6u
Ad Astra = 3ufwT9JF
```
Anything = ProjectID</br>
So its very simple what I mean. On the left side of the equals sign you must
provide the Mod name, preferably but the program will run if it's not. On the
right/left(Modrinth) side of the ProjectID.</br>