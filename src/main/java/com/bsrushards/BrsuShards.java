package com.bsrushards;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BrsuShards extends JavaPlugin implements TabExecutor, Listener {

    private final Map<UUID, Integer> shards = new HashMap<>();
    private final Map<Integer, ShopItem> shopItems = new HashMap<>();
    private File playerFile, shopFile, configFile;
    private YamlConfiguration playerConfig, shopConfig, config;
    private int intervalTask = -1;
    private final Map<UUID, Integer> confirmCache = new HashMap<>();

    @Override
    public void onEnable() {
        loadConfigs();

        getCommand("shards").setExecutor(this);
        getCommand("shardsshop").setExecutor(this);
        PluginCommand cmd = getCommand("bsrushards");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);

        Bukkit.getPluginManager().registerEvents(this, this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ShardsPlaceholder(this).register();
        }

        startShardAutoTask();
    }

    @Override
    public void onDisable() {
        savePlayerData();
        saveShopItems();
    }

    public void loadConfigs() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);

        playerFile = new File(getDataFolder(), "playerdata.yml");
        if (!playerFile.exists()) {
            try { playerFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        loadPlayerData();

        shopFile = new File(getDataFolder(), "shop.yml");
        if (!shopFile.exists()) saveResource("shop.yml", false);
        shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        loadShopItems();
    }

    private void startShardAutoTask() {
        if (intervalTask != -1) Bukkit.getScheduler().cancelTask(intervalTask);
        if (!config.getBoolean("settings.play-shards-enabled", true)) return;
        int interval = config.getInt("settings.interval-minutes", 10) * 60 * 20;
        int shardsPer = config.getInt("settings.shards-per-interval", 1);
        intervalTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers())
                addShards(p.getUniqueId(), shardsPer, false);
            savePlayerData();
        }, interval, interval);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("shards")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(getMsg("only-in-game"));
                return true;
            }
            Player p = (Player) sender;
            int value = getShards(p.getUniqueId());
            String formattedShards = formatNumberWithComma(value);
            p.sendMessage(getMsg("you-have-shards").replace("%shards%", formattedShards));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("bsrushards")) {
            if (args.length == 0) {
                sender.sendMessage(color("&e&m-----&6[ &bBrsuShards Plugin &6]&e&m-----"));
                sender.sendMessage(color("&bA Shards point system with GUI shop and PlaceholderAPI support."));
                sender.sendMessage(color("&bCreator: &fNattapat2871"));
                sender.sendMessage(color("&bGitHub: &f&nhttps://github.com/nattapat2871/bsrushards"));
                sender.sendMessage(color(""));
                sender.sendMessage(color("&eType /bsrushards help for a list of commands."));
                sender.sendMessage(color("&e&m------------------------------------"));
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(color("&6&lCommands for BrsuShards:"));
                sender.sendMessage(color("&e/bsrushards reload &7- Reloads the config file."));
                sender.sendMessage(color("&e/bsrushards give <player> <amount> &7- Gives Shards to a player."));
                sender.sendMessage(color("&e/bsrushards set <player> <amount> &7- Sets a player's Shards."));
                sender.sendMessage(color("&e/bsrushards additem <cost> <slot> &7- Adds an item to the shop."));
                sender.sendMessage(color("&e/bsrushards removeitem <slot> &7- Removes an item from the shop."));
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("bsrushards.admin")) {
                    sender.sendMessage(getMsg("no-permission"));
                    return true;
                }
                loadConfigs();
                startShardAutoTask();
                sender.sendMessage(getMsg("reload-success"));
                return true;
            }
            if (args[0].equalsIgnoreCase("additem")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(getMsg("only-in-game"));
                    return true;
                }
                if (!sender.hasPermission("bsrushards.shopadmin")) {
                    sender.sendMessage(getMsg("no-permission"));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(color("&cUsage: /bsrushards additem <cost> <slot>"));
                    return true;
                }
                Player p = (Player) sender;
                int cost, slot;
                try {
                    cost = Integer.parseInt(args[1]);
                    slot = Integer.parseInt(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(color("&cCost and slot must be numbers."));
                    return true;
                }
                ItemStack hand = p.getInventory().getItemInMainHand();
                if (hand.getType() == Material.AIR) {
                    sender.sendMessage(color("&cYou must be holding an item."));
                    return true;
                }
                shopItems.put(slot, new ShopItem(slot, hand.clone(), cost));
                saveShopItems();
                sender.sendMessage(getMsg("item-added"));
                return true;
            }
            if (args[0].equalsIgnoreCase("removeitem")) {
                if (!sender.hasPermission("bsrushards.shopadmin")) {
                    sender.sendMessage(getMsg("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(color("&cUsage: /bsrushards removeitem <slot>"));
                    return true;
                }
                int slot;
                try {
                    slot = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(color("&cThe slot must be a number."));
                    return true;
                }
                if (!shopItems.containsKey(slot)) {
                    sender.sendMessage(color("&cThere is no item in slot " + slot + "."));
                    return true;
                }
                shopItems.remove(slot);
                saveShopItems();
                sender.sendMessage(getMsg("item-removed").replace("%slot%", String.valueOf(slot)));
                return true;
            }
            if (args[0].equalsIgnoreCase("give")) {
                if (!sender.hasPermission("bsrushards.give")) {
                    sender.sendMessage(getMsg("no-permission"));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(color("&cUsage: /bsrushards give <player> <amount>"));
                    return true;
                }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) {
                    sender.sendMessage(color("&cPlayer not found: " + args[1]));
                    return true;
                }
                int amt;
                try {
                    amt = Integer.parseInt(args[2]);
                    if (amt < 0) {
                        sender.sendMessage(color("&cAmount cannot be negative."));
                        return true;
                    }
                }
                catch (Exception e) {
                    sender.sendMessage(color("&cThe amount must be a number."));
                    return true;
                }
                addShards(t.getUniqueId(), amt, true);
                sender.sendMessage(color("&aGave " + formatNumberWithComma(amt) + " Shards to " + t.getName() + "."));
                return true;
            }
            if (args[0].equalsIgnoreCase("set")) {
                if (!sender.hasPermission("bsrushards.set")) {
                    sender.sendMessage(getMsg("no-permission"));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(color("&cUsage: /bsrushards set <player> <amount>"));
                    return true;
                }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) {
                    sender.sendMessage(color("&cPlayer not found: " + args[1]));
                    return true;
                }
                int amt;
                try {
                    amt = Integer.parseInt(args[2]);
                    if (amt < 0) {
                        sender.sendMessage(color("&cAmount cannot be negative."));
                        return true;
                    }
                } catch (Exception e) {
                    sender.sendMessage(color("&cThe amount must be a number."));
                    return true;
                }
                setShards(t.getUniqueId(), amt, true);
                sender.sendMessage(getMsg("set-success")
                        .replace("%player%", t.getName())
                        .replace("%amount%", formatNumberWithComma(amt))
                );
                return true;
            }

            sender.sendMessage(color("&cUnknown command. Type /bsrushards help for a list of commands."));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("shardsshop")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(getMsg("only-in-game"));
                return true;
            }
            openShop((Player) sender);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("bsrushards")) return null;

        List<String> base = Arrays.asList("additem", "give", "help", "reload", "removeitem", "set");
        Collections.sort(base);

        if (args.length == 1)
            return filter(base, args[0]);
        if (args.length == 2 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("set")))
            return null;
        if (args.length == 2 && args[0].equalsIgnoreCase("additem"))
            return Collections.singletonList("<cost>");
        if (args.length == 3 && args[0].equalsIgnoreCase("additem"))
            return Collections.singletonList("<slot>");
        if (args.length == 2 && args[0].equalsIgnoreCase("removeitem")) {
            return shopItems.keySet().stream()
                    .map(String::valueOf)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("set")))
            return Collections.singletonList("<amount>");
        return Collections.emptyList();
    }

    private List<String> filter(List<String> src, String start) {
        return src.stream()
                .filter(s -> s.toLowerCase().startsWith(start.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, color(config.getString("messages.shop-title", "&6Shard Shop")));

        List<Integer> cantBuySlots = config.getIntegerList("shop-ui.cannotbuy.slots");
        if (cantBuySlots != null && !cantBuySlots.isEmpty()) {
            ItemStack cantBuy = getCannotBuyPane();
            for (int slot : cantBuySlots) {
                if (slot >= 0 && slot < inv.getSize())
                    inv.setItem(slot, cantBuy);
            }
        }
        for (ShopItem shopItem : shopItems.values()) {
            if (shopItem.slot >= 0 && shopItem.slot < inv.getSize())
                inv.setItem(shopItem.slot, formatShopItem(shopItem));
        }
        Bukkit.getScheduler().runTask(this, () -> player.openInventory(inv));
        playSound(player, "open-shop");
    }

    public void openConfirm(Player player, ShopItem shopItem) {
        Inventory confirm = Bukkit.createInventory(null, 27, color(config.getString("messages.confirm-title", "&eConfirm Purchase")));

        List<Integer> blankSlots = config.getIntegerList("shop-ui.blank.slots");
        ItemStack blankPane = getPaneFromConfig("shop-ui.blank");
        if (blankSlots != null) for (int slot : blankSlots) confirm.setItem(slot, blankPane);

        int cancelSlot = config.getInt("shop-ui.cancel.slot", 12);
        confirm.setItem(cancelSlot, getPaneFromConfig("shop-ui.cancel"));

        int confirmSlot = config.getInt("shop-ui.confirm.slot", 16);
        confirm.setItem(confirmSlot, getPaneFromConfig("shop-ui.confirm"));

        int itemSlot = config.getInt("shop-ui.item-slot", 14);
        confirm.setItem(itemSlot, formatShopItem(shopItem));

        player.openInventory(confirm);
        confirmCache.put(player.getUniqueId(), shopItem.slot);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();

        if (title.equalsIgnoreCase(color(config.getString("messages.confirm-title", "&eConfirm Purchase")))) {
            e.setCancelled(true);
            int slot = e.getSlot();
            int cancelSlot = config.getInt("shop-ui.cancel.slot", 12);
            int confirmSlot = config.getInt("shop-ui.confirm.slot", 16);
            if (slot == cancelSlot) {
                playSound(p, "buy-cancel");
                p.sendMessage(getMsg("buy-cancelled"));
                openShop(p);
                return;
            }
            if (slot == confirmSlot && confirmCache.containsKey(p.getUniqueId())) {
                int shopSlot = confirmCache.get(p.getUniqueId());
                ShopItem item = shopItems.get(shopSlot);
                if (item == null) {
                    playSound(p, "buy-fail");
                    p.closeInventory();
                    return;
                }
                if (getShards(p.getUniqueId()) < item.cost) {
                    playSound(p, "buy-fail");
                    p.sendMessage(getMsg("not-enough-shards"));
                    return;
                }
                playSound(p, "buy-success");
                removeShards(p.getUniqueId(), item.cost, true);
                p.getInventory().addItem(item.item.clone());
                String showName = getDisplayName(item.item);
                p.sendMessage(getMsg("buy-success")
                        .replace("%item%", showName)
                        .replace("%cost%", formatNumberWithComma(item.cost)));

                // แก้บั๊ก: ลบ cache และปิด inventory หลังซื้อสำเร็จ
                confirmCache.remove(p.getUniqueId());
                p.closeInventory();
            }
            return;
        }

        if (title.equalsIgnoreCase(color(config.getString("messages.shop-title", "&6Shard Shop")))) {
            e.setCancelled(true);
            int slot = e.getSlot();
            ShopItem item = shopItems.get(slot);
            if (item != null) {
                playSound(p, "click-buy");
                openConfirm(p, item);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player p = (Player) e.getPlayer();
        String title = e.getView().getTitle();
        if (title.equalsIgnoreCase(color(config.getString("messages.confirm-title", "&eConfirm Purchase")))) {
            confirmCache.remove(p.getUniqueId());
        }
    }

    private ItemStack getPaneFromConfig(String path) {
        Material type = Material.valueOf(config.getString(path + ".type", "GRAY_STAINED_GLASS_PANE"));
        ItemStack pane = new ItemStack(type);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(config.getString(path + ".display", " ")));
            pane.setItemMeta(meta);
        }
        return pane;
    }

    private ItemStack getCannotBuyPane() {
        Material type = Material.valueOf(config.getString("shop-ui.cannotbuy.type", "BLACK_STAINED_GLASS_PANE"));
        ItemStack pane = new ItemStack(type);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(config.getString("shop-ui.cannotbuy.display", "&7Not for Sale")));
            List<String> lore = config.getStringList("shop-ui.cannotbuy.lore");
            if (!lore.isEmpty()) meta.setLore(lore.stream().map(this::color).collect(Collectors.toList()));
            pane.setItemMeta(meta);
        }
        return pane;
    }

    private String getDisplayName(ItemStack item) {
        if (item == null) return "";
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName())
            return meta.getDisplayName();
        String typeName = item.getType().name().toLowerCase().replace("_", " ");
        String[] arr = typeName.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s : arr) sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        return sb.toString().trim();
    }

    private ItemStack formatShopItem(ShopItem shopItem) {
        ItemStack item = shopItem.item.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.removeIf(l -> l.contains("Shards"));
        String priceFmt = config.getString("shop-ui.price-lore", "&ePrice: &f%cost% Shards");
        lore.add(color(priceFmt.replace("%cost%", formatNumber(shopItem.cost))));
        meta.setLore(lore);
        if (!meta.hasDisplayName())
            meta.setDisplayName(getDisplayName(item));
        item.setItemMeta(meta);
        return item;
    }

    private String formatNumberWithComma(int n) { return String.format("%,d", n); }
    private String formatNumber(int n) {
        if (n < 1000) return String.valueOf(n);
        if (n < 1000000) return String.format("%.1fk", n / 1000.0).replace(".0k", "k");
        if (n < 1000000000) return String.format("%.1fM", n / 1000000.0).replace(".0M", "M");
        return String.format("%.1fB", n / 1000000000.0).replace(".0B", "B");
    }

    private String color(String msg) { return msg == null ? "" : msg.replace("&", "§"); }
    private String getMsg(String key) { return color(config.getString("messages." + key, "&cMessage '" + key + "' not found in config.yml")); }

    private void playSound(Player p, String path) {
        String soundName = config.getString("sounds." + path);
        if (soundName == null || soundName.isEmpty()) return;
        try { p.playSound(p.getLocation(), org.bukkit.Sound.valueOf(soundName.toUpperCase().replace("MINECRAFT:", "")), 1, 1);
        } catch (IllegalArgumentException e) { getLogger().warning("Invalid sound name in config.yml: " + soundName); }
    }

    private void sendActionBar(Player p, String msg) { p.sendActionBar(color(msg)); }

    public void notifyReceiveShards(Player p, int amount) {
        String chatMsg = getMsg("shards-receive").replace("%amount%", formatNumberWithComma(amount));
        String barMsg = getMsg("shards-receive-bar").replace("%amount%", formatNumberWithComma(amount));
        if (config.getBoolean("notifications.receive.chat", true) && !chatMsg.isEmpty()) p.sendMessage(chatMsg);
        if (config.getBoolean("notifications.receive.actionbar", true) && !barMsg.isEmpty()) sendActionBar(p, barMsg);
        playSound(p, "shards-receive");
    }

    public int getShards(UUID uuid) { return shards.getOrDefault(uuid, 0); }
    public void addShards(UUID uuid, int amount, boolean save) {
        shards.put(uuid, getShards(uuid) + amount);
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline() && amount > 0) notifyReceiveShards(p, amount);
        if (save) savePlayerData();
    }

    public void setShards(UUID uuid, int amount, boolean save) {
        shards.put(uuid, amount);
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) {
            p.sendMessage(getMsg("shards-set").replace("%amount%", formatNumberWithComma(amount)));
        }
        if (save) savePlayerData();
    }

    public boolean removeShards(UUID uuid, int amount, boolean save) {
        int current = getShards(uuid);
        if (current < amount) return false;
        shards.put(uuid, current - amount);
        if (save) savePlayerData();
        return true;
    }

    public void loadPlayerData() {
        shards.clear();
        if (playerConfig.isConfigurationSection("players")) {
            for (String uuidStr : playerConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    int value = playerConfig.getInt("players." + uuidStr + ".shards", 0);
                    shards.put(uuid, value);
                } catch (Exception e) {
                    getLogger().warning("Failed to load player data for: " + uuidStr);
                    e.printStackTrace();
                }
            }
        }
    }
    public void savePlayerData() {
        playerConfig.set("players", null);
        for (Map.Entry<UUID, Integer> entry : shards.entrySet()) {
            playerConfig.set("players." + entry.getKey().toString() + ".shards", entry.getValue());
        }
        try { playerConfig.save(playerFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void loadShopItems() {
        shopItems.clear();
        if (shopConfig.isConfigurationSection("shop")) {
            for (String key : shopConfig.getConfigurationSection("shop").getKeys(false)) {
                try {
                    int slot = Integer.parseInt(key);
                    int cost = shopConfig.getInt("shop." + key + ".cost");
                    ItemStack item = shopConfig.getItemStack("shop." + key + ".item");
                    if (item != null)
                        shopItems.put(slot, new ShopItem(slot, item, cost));
                } catch (Exception e) {
                    getLogger().warning("Failed to load shop item for key: " + key);
                    e.printStackTrace();
                }
            }
        }
    }
    public void saveShopItems() {
        shopConfig.set("shop", null);
        for (Map.Entry<Integer, ShopItem> entry : shopItems.entrySet()) {
            ShopItem item = entry.getValue();
            shopConfig.set("shop." + item.slot + ".cost", item.cost);
            shopConfig.set("shop." + item.slot + ".item", item.item);
        }
        try { shopConfig.save(shopFile); } catch (IOException e) { e.printStackTrace(); }
    }

    private static class ShopItem {
        int slot;
        ItemStack item;
        int cost;
        ShopItem(int slot, ItemStack item, int cost) {
            this.slot = slot;
            this.item = item;
            this.cost = cost;
        }
    }

    public class ShardsPlaceholder extends PlaceholderExpansion {
        private final BrsuShards plugin;
        public ShardsPlaceholder(BrsuShards plugin) { this.plugin = plugin; }
        @Override public String getIdentifier() { return "bsrushards"; }
        @Override public String getAuthor() { return "Nattapat2871"; }
        @Override public String getVersion() { return "1.4";
        }
        @Override public boolean persist() { return true; }
        @Override
        public String onPlaceholderRequest(Player player, String params) {
            if (player == null) return "";
            if (params.equalsIgnoreCase("shards")) {
                return plugin.formatNumberWithComma(plugin.getShards(player.getUniqueId()));
            }
            if(params.equalsIgnoreCase("shards_formatted")) {
                return plugin.formatNumber(plugin.getShards(player.getUniqueId()));
            }
            return "";
        }
    }
}