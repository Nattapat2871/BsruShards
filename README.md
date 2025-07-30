
# 💎 BrsuShards Plugin

<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![GitHub Repo stars](https://img.shields.io/github/stars/Nattapat2871/BsruShards?style=flat-square)](https://github.com/Nattapat2871/BsruShards/stargazers)
![Visitor Badge](https://api.visitorbadge.io/api/VisitorHit?user=Nattapat2871&repo=BsruShards&countColor=%237B1E7A&style=flat-square)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/Nattapat2871)

</div>

<p align= "center">
        <b>English</b>　<a href="/README_TH.md">ภาษาไทย</a>

A plugin for managing a "Shards" point system, specifically designed for the BSRU Minecraft server. It comes with an automatic point distribution system, a GUI shop, and PlaceholderAPI support.

---

## ✨ Features

* **🪙 Auto Shards Distribution:** Automatically gives Shards to online players at configured intervals.
* **🏝️ AFK Reward Zones:** Create special areas where players can stand to continuously earn Shards over time.
* **🛒 GUI Shop:** Players can use `/shardsshop` to open a shop and exchange Shards for items.
* **✅ Purchase Confirmation:** Includes a confirmation UI before buying to prevent accidental clicks.
* **👨‍💻 Admin Commands:** Easily manage players, the shop, and AFK zones with the `/bsrushards` command.
* **📊 PlaceholderAPI Support:** Fetch player Shards data for use in other plugins like scoreboards, tabs, etc.
* **🔧 Easy Configuration:** Almost all messages, sounds, and UI elements are customizable via `config.yml`.

---

## ⚙️ Dependencies

* **Server:** Spigot, Paper, or their forks (1.16+ recommended).
* **Optional:** [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) - Required to use placeholders.

---

## 🚀 Installation

1.  Download the latest `.jar` file of the BrsuShards plugin.
2.  Place the downloaded file into your server's `plugins` folder.
3.  Restart the server to generate the configuration files.
4.  (Recommended) Install PlaceholderAPI for full functionality.
5.  Customize settings in `plugins/BrsuShards/config.yml`.

---

## 📝 Commands & Permissions

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/shards` | `bsrushards.user` | Checks your own Shard balance. |
| `/shardsshop` | `bsrushards.user` | Opens the Shard shop. |
| `/bsrushards help` | (for all players) | Shows the list of admin commands. |
| `/bsrushards reload` | `bsrushards.admin` | Reloads the `config.yml` file. |
| `/bsrushards give <player> <amount>`| `bsrushards.give` | Adds Shards to a player's balance. |
| `/bsrushards set <player> <amount>` | `bsrushards.set` | Sets a player's Shard balance to a specific amount. |
| `/bsrushards additem <cost> <slot>` | `bsrushards.shopadmin` | Adds the item you are holding to the shop. |
| `/bsrushards removeitem <slot>` | `bsrushards.shopadmin` | Removes an item from the specified shop slot. |
| `/bsrushards afkpool <...>` | `bsrushards.afkpool.admin` | Manage AFK reward zones (use `wand`, `create`, `delete`, `list`). |


---

## 🌐 Placeholders

If PlaceholderAPI is installed, you can use the following placeholders:

| Placeholder | Description | Example |
| :--- | :--- | :--- |
| `%bsrushards_shards%` | Displays the player's Shards as a full number with commas. | `1,250` |
| `%bsrushards_shards_formatted%` | Displays the player's Shards in a short format (k, M, B). | `1.3k` |

---

## 🔧 Configuration

* `config.yml`: The main configuration file. Used to customize messages, sounds, auto-distribution, and the GUI.
* `shop.yml`: Shop data file. This is managed automatically via in-game commands.
* `playerdata.yml`: Player data file. This is managed automatically.
* `afkpool.yml`: Data file for AFK zones, managed automatically via commands.

### config.yml
```yaml
# --------------------------------------------------- #
#             BrsuShards Plugin Settings              #
# --------------------------------------------------- #

settings:
  # Enable or disable the automatic shard distribution for online players.
  play-shards-enabled: true
  # The time in minutes between each automatic shard distribution.
  interval-minutes: 10
  # The number of shards to give to each player during the interval.
  shards-per-interval: 1

# Title and Subtitle settings for when a player enters an AFK Pool.
afk-pool-entry:
  enabled: true
  title: "&b&lEntering AFK Zone"
  subtitle: "&eYou will now earn rewards for staying here"
  # Display timings (in ticks, 20 ticks = 1 second)
  fade-in-ticks: 10
  stay-ticks: 40
  fade-out-ticks: 20

# Sound effects for various actions.
sounds:
  open-shop: "minecraft:block.note_block.harp"
  click-buy: "minecraft:ui.button.click"
  buy-success: "minecraft:block.note_block.pling"
  buy-cancel: "minecraft:entity.villager.no"
  buy-fail: "minecraft:entity.villager.no"
  shards-receive: "minecraft:entity.experience_orb.pickup"

# All configurable messages for the plugin.
messages:
  only-in-game: "&cThis command can only be used in-game."
  no-permission: "&cYou do not have permission to use this command!"
  not-enough-shards: "&cYou do not have enough Shards!"
  buy-success: "&aSuccessfully purchased %item%!"
  buy-cancelled: "&cPurchase cancelled."
  reload-success: "&aConfiguration reloaded successfully!"
  item-added: "&aItem successfully added to the shop!"
  set-success: "&aSuccessfully set %player%'s Shards to %amount%."
  shards-set: "&eYour Shards have been set to &f%amount% &eby an admin."
  you-have-shards: "&eYou have: &f%shards% Shards"
  shop-title: "&6Shard Shop"
  confirm-title: "&eConfirm Purchase"
  shards-receive: "&aYou have received &e+%amount% &aShards!"
  shards-receive-bar: "&a+%amount% Shards!"
  # Messages for AFK Pool system
  afkpool-wand-given: "&aYou have received the AFK Pool Wand!"
  afkpool-pos1-set: "&aPosition 1 set to: &e%location%"
  afkpool-pos2-set: "&aPosition 2 set to: &e%location%"
  afkpool-created: "&aSuccessfully created AFK Pool: &e%name%"
  afkpool-deleted: "&cSuccessfully deleted AFK Pool: &e%name%"
  afkpool-list-header: "&6--- List of AFK Pools ---"
  afkpool-list-item: "&e- %name% &7(gives &f%shards% &7Shards every &f%time% &7seconds)"
  afkpool-list-empty: "&cNo AFK Pools have been created yet."
  afkpool-not-exist: "&cAn AFK Pool with the name &e%name% &cdoes not exist."
  afkpool-name-exists: "&cAn AFK Pool with the name &e%name% &calready exists."
  afkpool-no-selection: "&cYou must set both positions with the wand first!"
  afkpool-invalid-number: "&cTime and amount must be numbers."
  afkpool-reward-message: "&aYou received &e+%shards% &aShards for being AFK!"
  afkpool-countdown-bar: "&aReceiving &e+%shards% Shards &afrom &b%pool% &ain &f%time%s"

# Configuration for the graphical user interfaces (GUI).
shop-ui:
  price-lore: "&ePrice: &f%cost% Shards"
  blank:
    type: GRAY_STAINED_GLASS_PANE
    slots: [0,1,2,3,4,5,6,7,8,9,10,11,13,15,17,18,19,20,21,22,23,24,25,26]
    display: " "
  cannotbuy:
    slots: [1,2,3,4,5,6,7,8,9]
    type: BLACK_STAINED_GLASS_PANE
    display: "&7Not for Sale"
    lore:
      - "&8This item cannot be purchased."
  cancel:
    type: RED_STAINED_GLASS_PANE
    slot: 12
    display: "&cCancel"
  confirm:
    type: GREEN_STAINED_GLASS_PANE
    slot: 16
    display: "&aConfirm"
  item-slot: 14
  ```
# ✍️ Author
Developed by: Nattapat2871
