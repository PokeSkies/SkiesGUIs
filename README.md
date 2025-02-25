# SkiesGUIs
<img height="50" src="https://camo.githubusercontent.com/a94064bebbf15dfed1fddf70437ea2ac3521ce55ac85650e35137db9de12979d/68747470733a2f2f692e696d6775722e636f6d2f6331444839564c2e706e67" alt="Requires Fabric Kotlin"/>

A Fabric server-sided GUI creation mod aimed to make creating basic GUIs easier! Creating a new GUI is as easy as creating a new file in the `guis` folder and copying the basic formatting found here.

More information on configuration can be found on the [Wiki](https://github.com/PokeSkies/SkiesGUIs/wiki)!

## Features
- Create practically infinite GUIs *(idk, haven't tested that)*
- Customize everything about items in the inventory
  - Multi slot definitions *(one item definition, multiple slots)*
  - Name/Lore customization with [MiniMessage formatting](https://docs.advntr.dev/minimessage/format.html)
  - Full access to NBT *(custom model data, Pokemon, etc.)*
- Item view requirements/conditionals *(with success/deny actions)*
- Item click actions
- Item click requirements/conditionals *(with success/deny actions)*
- GUI open and close actions
- GUI open requirements/conditionals *(with success/deny actions)*
- 14 action types *(for now)*
- 7 requirement types *(for now)*
- Create alias commands to access GUIs
- Economy Integrations (Impactor, Pebbles Economy)
- Placeholder Integrations (Impactor, PlaceholderAPI)

## Installation
1. Download the latest version of the mod from [Modrinth](https://modrinth.com/mod/skiesguis).
2. Download all required dependencies:
   - [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)
3. Download any optional dependencies:
   - [Impactor](https://modrinth.com/mod/impactor) **_(Economy, Placeholders)_**
   - Pebbles Economy **_(Economy)_**
   - [MiniPlaceholders](https://modrinth.com/plugin/miniplaceholders) **_(Placeholders)_**
   - [PlaceholderAPI]() **_(Placeholders)_**
   - [Plan]() **_(Additional Requirements)_**
4. Install the mod and dependencies into your server's `mods` folder.
5. Configure your GUIs in the `./config/skiesguis/guis/` folder.

## Commands/Permissions
| Command                     | Description                                                   | Permission                 |
|-----------------------------|---------------------------------------------------------------|----------------------------|
| /gui reload                 | Reload the Mod                                                | skiesguis.command.reload   |
| /gui open <gui_id> [player] | Open a GUI specified by its ID, optionally for another player | skiesguis.command.open     |
| /gui printnbt               | Print the NBT data of the item in your hand, if present       | skiesguis.command.printnbt |
| /gui debug                  | Toggle the debug mode for more insight into errors            | skiesguis.command.deug     |

| Permission              | Description                                          |
|-------------------------|------------------------------------------------------|
| skiesguis.open.<gui_id> | Permission to open a GUI when using an Alias Command |


## Planned Features
- Better/more debugging and error handling
- In-game GUI editor
- More Action Types
    - Mod Integrations (Cobblemon, etc.)
  - **Please submit suggestions!**
- More Requirement Types
    - Mod Integrations (Cobblemon, etc.)
    - Regex?
    - Location?
    - **Please submit suggestions!**
- Requirements Updates
    - Minimum Requirements with Optionals?
- More Inventory Types
    - CHEST
    - DISPENSER
    - HOPPER
    - etc.
- Inventory Update Ticking (optionally update GUI at an interval)
- Animations?? (oh god)

## Support
A community support Discord has been opened up for all Skies Development related projects! Feel free to join and ask questions or leave suggestions :)

<a class="discord-widget" href="https://discord.gg/cgBww275Fg" title="Join us on Discord"><img src="https://discordapp.com/api/guilds/1158447623989116980/embed.png?style=banner2"></a>
