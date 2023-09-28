# SkiesGUIs
<img height="50" src="https://camo.githubusercontent.com/a94064bebbf15dfed1fddf70437ea2ac3521ce55ac85650e35137db9de12979d/68747470733a2f2f692e696d6775722e636f6d2f6331444839564c2e706e67"/>

A Fabric (1.20.1) server sided GUI creation mod aimed to make creating basic GUIs easier! Creating a new GUI is as easy as creating a new file in the `guis` folder and copying the basic formatting found here.

More information on configuration can be found on the Wiki here!

## Installation
1. Download the latest version of the mod from the Releases tab.
2. Download all require dependencies for your version:
   - [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin) 
2. Install the mod into your server's `mods` folder.
3. Configure your GUIs in the `./config/skiesguis/guis/` folder.

## Commands/Permissions

| Command                 | Description                                                   | Permission               |
|-------------------------|---------------------------------------------------------------|--------------------------|
| /gui reload             | Reload the Mod                                                | skiesguis.command.reload |
| /gui open <id> [player] | Open a GUI specified by its ID, optionally for another player | skiesguis.command.open   |


## Planned Features
- Open/Close Actions
- Requirements (view and click)
- More Placeholders (support Placeholders mod?)
- Item Priority
- Inventory Update Ticking (optionally update GUI at an interval)
- Animations?? (oh god)
- Custom Commands?? (open up a GUI quickly via a command with custom permissions)