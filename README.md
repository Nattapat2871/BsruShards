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
* **🛒 GUI Shop:** Players can use `/shardsshop` to open a shop and exchange Shards for items.
* **✅ Purchase Confirmation:** Includes a confirmation UI before buying to prevent accidental clicks.
* **👨‍💻 Admin Commands:** Easily manage players and the shop with the `/bsrushards` command.
* **📊 PlaceholderAPI Support:** Fetch player Shards data for use in other plugins like scoreboards, tabs, etc.
* **🔧 Easy Configuration:** Almost all messages, sounds, and UI elements are customizable via `config.yml`.

---

## ⚙️ Dependencies

* **Server:** Spigot, Paper, or their forks (1.20+ recommended).
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
* `shop.yml`: Shop data file. This is managed automatically via the `/bsrushards additem` command.
* `playerdata.yml`: Player data file. This is managed automatically.

### config.yml 
```yaml
# --------------------------------------------------- #
#             BrsuShards Plugin Settings              #
# --------------------------------------------------- #

settings:
  # Enable or disable the automatic shard distribution for online players.
  # Set to 'true' to enable, 'false' to disable.
  play-shards-enabled: true
  
  # The time in minutes between each automatic shard distribution.
  interval-minutes: 10
  
  # The number of shards to give to each player during the interval.
  shards-per-interval: 1

# Sound effects for various actions.
# You can find a list of sound names from the Minecraft Wiki.
sounds:
  # Sound when a player opens the shop GUI.
  open-shop: "minecraft:block.note_block.harp"
  
  # Sound when a player clicks an item in the shop.
  click-buy: "minecraft:ui.button.click"
  
  # Sound for a successful purchase.
  buy-success: "minecraft:block.note_block.pling"
  
  # Sound when a purchase is cancelled.
  buy-cancel: "minecraft:entity.villager.no"
  
  # Sound for a failed purchase (e.g., not enough shards).
  buy-fail: "minecraft:entity.villager.no"
  
  # Sound when a player receives shards.
  shards-receive: "minecraft:entity.experience_orb.pickup"

# All configurable messages for the plugin.
# You can use Minecraft color codes with the '&' symbol.
messages:
  only-in-game: "&cThis command can only be used in-game."
  no-permission: "&cYou do not have permission to use this command!"
  not-enough-shards: "&cYou do not have enough Shards!"
  buy-success: "&aSuccessfully purchased %item%!"
  buy-cancelled: "&cPurchase cancelled."
  reload-success: "&aConfiguration reloaded successfully!"
  item-added: "&aItem successfully added to the shop!"
  
  # %shards% is replaced by the player's shard count (e.g., 1,000).
  you-have-shards: "&eYou have: &f%shards% Shards"
  
  # The title of the main shop GUI.
  shop-title: "&6Shard Shop"
  
  # The title of the purchase confirmation GUI.
  confirm-title: "&eConfirm Purchase"
  
  # Chat message when receiving shards. %amount% is the number of shards received.
  shards-receive: "&aYou have received &e+%amount% &aShards!"
  
  # Action bar message when receiving shards.
  shards-receive-bar: "&a+%amount% Shards!"

# Configuration for the graphical user interfaces (GUI).
shop-ui:
  # The lore line added to shop items to show their price.
  # %cost% is replaced with the item's cost.
  price-lore: "&ePrice: &f%cost% Shards"
  
  # The item used as a decorative filler in the GUI backgrounds.
  blank:
    type: GRAY_STAINED_GLASS_PANE
    # The slots to fill with this item.
    slots: [0,1,2,3,4,5,6,7,8,9,10,11,13,15,17,18,19,20,21,22,23,24,25,26]
    display: " "
    
  # The item used for slots that are purely decorative and not for sale.
  cannotbuy:
    # A list of slots to fill with this item in the main shop.
    slots: [1,2,3,4,5,6,7,8,9]
    type: BLACK_STAINED_GLASS_PANE
    display: "&7Not for Sale"
    lore:
      - "&8This item cannot be purchased."
      
  # The 'cancel' button in the confirmation GUI.
  cancel:
    type: RED_STAINED_GLASS_PANE
    # The slot for the cancel button.
    slot: 12
    display: "&cCancel"
    
  # The 'confirm' button in the confirmation GUI.
  confirm:
    type: GREEN_STAINED_GLASS_PANE
    # The slot for the confirm button.
    slot: 16
    display: "&aConfirm"
    
  # The slot where the item being purchased is displayed in the confirmation GUI.
  item-slot: 14 
  ```

---

## ✍️ Author

Developed by: **Nattapat2871**
[GitHub Project](https://github.com/nattapat2871/bsrushards)