package com.bsrushards;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BrsuShards extends JavaPlugin implements TabExecutor, Listener {

    // Core System
    private final Map<UUID, Integer> shards = new HashMap<>();
    private final Map<Integer, ShopItem> shopItems = new HashMap<>();
    private File playerFile, shopFile, configFile, afkpoolFile;
    private YamlConfiguration playerConfig, shopConfig, config, afkpoolConfig;
    private int intervalTask = -1;
    private final Map<UUID, Integer> confirmCache = new HashMap<>();

    // AFK Pool System
    private final Map<String, AFKPool> afkPools = new HashMap<>();
    private final Map<UUID, Location> pos1Selections = new HashMap<>();
    private final Map<UUID, Location> pos2Selections = new HashMap<>();
    private final Map<UUID, Integer> playerAfkTime = new HashMap<>();
    private final Map<UUID, String> playerInPool = new HashMap<>();
    private int afkPoolTask = -1;
    private final String WAND_DISPLAY_NAME = color("&b&lAFK Pool Wand");

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
        startAfkPoolTask();
        getLogger().info("BrsuShards has been enabled with AFK Pool system.");
    }

    @Override
    public void onDisable() {
        savePlayerData();
        saveShopItems();
        saveAfkPools();
    }

    public void loadConfigs() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);

        playerFile = new File(getDataFolder(), "playerdata.yml");
        if (!playerFile.exists()) saveResource("playerdata.yml", false);
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        loadPlayerData();

        shopFile = new File(getDataFolder(), "shop.yml");
        if (!shopFile.exists()) saveResource("shop.yml", false);
        shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        loadShopItems();

        afkpoolFile = new File(getDataFolder(), "afkpool.yml");
        if (!afkpoolFile.exists()) saveResource("afkpool.yml", false);
        afkpoolConfig = YamlConfiguration.loadConfiguration(afkpoolFile);
        loadAfkPools();
    }

    private void startShardAutoTask() {
        if (intervalTask != -1) Bukkit.getScheduler().cancelTask(intervalTask);
        if (!config.getBoolean("settings.play-shards-enabled", true)) return;
        int interval = config.getInt("settings.interval-minutes", 10) * 60 * 20;
        int shardsPer = config.getInt("settings.shards-per-interval", 1);
        intervalTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers())
                addShards(p.getUniqueId(), shardsPer, false, false);
            savePlayerData();
        }, interval, interval);
    }

    private void startAfkPoolTask() {
        if (afkPoolTask != -1) Bukkit.getScheduler().cancelTask(afkPoolTask);

        afkPoolTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                AFKPool currentPool = getPoolAt(p.getLocation());
                String previousPoolName = playerInPool.get(p.getUniqueId());

                if (currentPool != null) {
                    if (!currentPool.getName().equals(previousPoolName)) {
                        playerInPool.put(p.getUniqueId(), currentPool.getName());
                        playerAfkTime.put(p.getUniqueId(), currentPool.getTime());

                        if (config.getBoolean("afk-pool-entry.enabled", true)) {
                            String title = getMsg("afk-pool-entry.title");
                            String subtitle = getMsg("afk-pool-entry.subtitle");
                            int fadeIn = config.getInt("afk-pool-entry.fade-in-ticks", 10);
                            int stay = config.getInt("afk-pool-entry.stay-ticks", 40);
                            int fadeOut = config.getInt("afk-pool-entry.fade-out-ticks", 20);

                            if (!title.isEmpty() || !subtitle.isEmpty()) {
                                p.sendTitle(title.replace("%pool%", currentPool.getName()), subtitle.replace("%pool%", currentPool.getName()), fadeIn, stay, fadeOut);
                            }
                        }
                    }

                    int timeRemaining = playerAfkTime.getOrDefault(p.getUniqueId(), currentPool.getTime());
                    timeRemaining--;

                    if (timeRemaining <= 0) {
                        addShards(p.getUniqueId(), currentPool.getAmount(), true, true);
                        String rewardMsg = getMsg("afkpool-reward-message")
                                .replace("%shards%", formatNumberWithComma(currentPool.getAmount()))
                                .replace("%pool%", currentPool.getName());
                        sendOptionalMessage(p, rewardMsg);
                        timeRemaining = currentPool.getTime();
                    }

                    playerAfkTime.put(p.getUniqueId(), timeRemaining);
                    String actionBarMsg = getMsg("afkpool-countdown-bar")
                            .replace("%time%", String.valueOf(timeRemaining))
                            .replace("%shards%", String.valueOf(currentPool.getAmount()))
                            .replace("%pool%", currentPool.getName());
                    if (!actionBarMsg.isEmpty()) {
                        sendActionBar(p, actionBarMsg);
                    }

                } else if (previousPoolName != null) {
                    playerInPool.remove(p.getUniqueId());
                    playerAfkTime.remove(p.getUniqueId());
                }
            }
        }, 0L, 20L);
    }

    private AFKPool getPoolAt(Location loc) {
        for (AFKPool pool : afkPools.values()) {
            if (pool.isInside(loc)) {
                return pool;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("shards")) {
            if (!(sender instanceof Player)) {
                sendOptionalMessage(sender, getMsg("only-in-game"));
                return true;
            }
            Player p = (Player) sender;
            int value = getShards(p.getUniqueId());
            String formattedShards = formatNumberWithComma(value);
            sendOptionalMessage(p, getMsg("you-have-shards").replace("%shards%", formattedShards));
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
            if (args[0].equalsIgnoreCase("afkpool")) {
                if (!sender.hasPermission("bsrushards.afkpool.admin")) {
                    sendOptionalMessage(sender, getMsg("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(color("&cUsage: /bsrushards afkpool <wand|create|delete|list>"));
                    return true;
                }
                if (args[1].equalsIgnoreCase("wand")) {
                    if (!(sender instanceof Player)) {
                        sendOptionalMessage(sender, getMsg("only-in-game"));
                        return true;
                    }
                    Player p = (Player) sender;
                    ItemStack wand = new ItemStack(Material.BLAZE_ROD);
                    ItemMeta meta = wand.getItemMeta();
                    meta.setDisplayName(WAND_DISPLAY_NAME);
                    meta.setLore(Arrays.asList(
                            color("&7คลิกซ้ายเพื่อกำหนดจุดที่ 1"),
                            color("&7คลิกขวาเพื่อกำหนดจุดที่ 2")
                    ));
                    wand.setItemMeta(meta);
                    p.getInventory().addItem(wand);
                    sendOptionalMessage(p, getMsg("afkpool-wand-given"));
                    return true;
                }
                if (args[1].equalsIgnoreCase("create")) {
                    if (!(sender instanceof Player)) {
                        sendOptionalMessage(sender, getMsg("only-in-game"));
                        return true;
                    }
                    if (args.length < 5) {
                        sender.sendMessage(color("&cUsage: /bsrushards afkpool create <name> <time> <amount>"));
                        return true;
                    }
                    Player p = (Player) sender;
                    String poolName = args[2];
                    if (afkPools.containsKey(poolName.toLowerCase())) {
                        sendOptionalMessage(sender, getMsg("afkpool-name-exists").replace("%name%", poolName));
                        return true;
                    }
                    Location pos1 = pos1Selections.get(p.getUniqueId());
                    Location pos2 = pos2Selections.get(p.getUniqueId());
                    if (pos1 == null || pos2 == null) {
                        sendOptionalMessage(sender, getMsg("afkpool-no-selection"));
                        return true;
                    }
                    int time, amount;
                    try {
                        time = Integer.parseInt(args[3]);
                        amount = Integer.parseInt(args[4]);
                    } catch (NumberFormatException e) {
                        sendOptionalMessage(sender, getMsg("afkpool-invalid-number"));
                        return true;
                    }
                    AFKPool newPool = new AFKPool(poolName, p.getWorld(), pos1, pos2, time, amount);
                    afkPools.put(poolName.toLowerCase(), newPool);
                    saveAfkPools();
                    sendOptionalMessage(sender, getMsg("afkpool-created").replace("%name%", poolName));
                    return true;
                }
                if (args[1].equalsIgnoreCase("delete")) {
                    if (args.length < 3) {
                        sender.sendMessage(color("&cUsage: /bsrushards afkpool delete <name>"));
                        return true;
                    }
                    String poolName = args[2].toLowerCase();
                    if (!afkPools.containsKey(poolName)) {
                        sendOptionalMessage(sender, getMsg("afkpool-not-exist").replace("%name%", args[2]));
                        return true;
                    }
                    afkPools.remove(poolName);
                    saveAfkPools();
                    sendOptionalMessage(sender, getMsg("afkpool-deleted").replace("%name%", args[2]));
                    return true;
                }
                if (args[1].equalsIgnoreCase("list")) {
                    if (afkPools.isEmpty()) {
                        sendOptionalMessage(sender, getMsg("afkpool-list-empty"));
                        return true;
                    }
                    sendOptionalMessage(sender, getMsg("afkpool-list-header"));
                    String itemFormat = getMsg("afkpool-list-item");
                    if (!itemFormat.isEmpty()) {
                        for (AFKPool pool : afkPools.values()) {
                            sender.sendMessage(itemFormat
                                    .replace("%name%", pool.getName())
                                    .replace("%time%", String.valueOf(pool.getTime()))
                                    .replace("%shards%", String.valueOf(pool.getAmount()))
                            );
                        }
                    }
                    return true;
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(color("&6&lCommands for BrsuShards:"));
                sender.sendMessage(color("&e/shards &7- Check your Shards."));
                sender.sendMessage(color("&e/shardsshop &7- Open the shop."));
                sender.sendMessage(color("&e/bsrushards reload &7- Reloads the config file."));
                sender.sendMessage(color("&e/bsrushards give <player> <amount> &7- Gives Shards to a player."));
                sender.sendMessage(color("&e/bsrushards set <player> <amount> &7- Sets a player's Shards."));
                sender.sendMessage(color("&e/bsrushards additem <cost> <slot> &7- Adds an item to the shop."));
                sender.sendMessage(color("&e/bsrushards removeitem <slot> &7- Removes an item from the shop."));
                sender.sendMessage(color("&e/bsrushards afkpool <wand|create|delete|list> &7- Manage AFK Pools."));
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("bsrushards.admin")) {
                    sendOptionalMessage(sender, getMsg("no-permission"));
                    return true;
                }
                loadConfigs();
                startShardAutoTask();
                startAfkPoolTask();
                sendOptionalMessage(sender, getMsg("reload-success"));
                return true;
            }
            if (args[0].equalsIgnoreCase("additem")) {
                if (!(sender instanceof Player)) {
                    sendOptionalMessage(sender, getMsg("only-in-game"));
                    return true;
                }
                if (!sender.hasPermission("bsrushards.shopadmin")) {
                    sendOptionalMessage(sender, getMsg("no-permission"));
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
                sendOptionalMessage(sender, getMsg("item-added"));
                return true;
            }
            if (args[0].equalsIgnoreCase("removeitem")) {
                if (!sender.hasPermission("bsrushards.shopadmin")) {
                    sendOptionalMessage(sender, getMsg("no-permission"));
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
                sendOptionalMessage(sender, getMsg("item-removed").replace("%slot%", String.valueOf(slot)));
                return true;
            }
            if (args[0].equalsIgnoreCase("give")) {
                if (!sender.hasPermission("bsrushards.give")) {
                    sendOptionalMessage(sender, getMsg("no-permission"));
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
                addShards(t.getUniqueId(), amt, true, false);
                sender.sendMessage(color("&aGave " + formatNumberWithComma(amt) + " Shards to " + t.getName() + "."));
                return true;
            }
            if (args[0].equalsIgnoreCase("set")) {
                if (!sender.hasPermission("bsrushards.set")) {
                    sendOptionalMessage(sender, getMsg("no-permission"));
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
                sendOptionalMessage(sender, getMsg("set-success")
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
                sendOptionalMessage(sender, getMsg("only-in-game"));
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

        if (args.length == 1) {
            List<String> base = Arrays.asList("additem", "give", "help", "reload", "removeitem", "set", "afkpool");
            Collections.sort(base);
            return filter(base, args[0]);
        }

        if (args[0].equalsIgnoreCase("afkpool")) {
            if (!sender.hasPermission("bsrushards.afkpool.admin")) return Collections.emptyList();
            if (args.length == 2) {
                return filter(Arrays.asList("wand", "create", "delete", "list"), args[1]);
            }
            if (args.length == 3 && args[1].equalsIgnoreCase("delete")) {
                return filter(new ArrayList<>(afkPools.keySet()), args[2]);
            }
        }

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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;
        if (!p.hasPermission("bsrushards.afkpool.admin")) return;

        if (!item.getItemMeta().getDisplayName().equals(WAND_DISPLAY_NAME)) return;

        e.setCancelled(true);
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Location loc = e.getClickedBlock().getLocation();
            pos1Selections.put(p.getUniqueId(), loc);
            sendOptionalMessage(p, getMsg("afkpool-pos1-set").replace("%location%", formatLocation(loc)));
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location loc = e.getClickedBlock().getLocation();
            pos2Selections.put(p.getUniqueId(), loc);
            sendOptionalMessage(p, getMsg("afkpool-pos2-set").replace("%location%", formatLocation(loc)));
        }
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
                sendOptionalMessage(p, getMsg("buy-cancelled"));
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
                    sendOptionalMessage(p, getMsg("not-enough-shards"));
                    return; // แก้ไข: ไม่ปิด GUI เมื่อเงินไม่พอ
                }
                playSound(p, "buy-success");
                removeShards(p.getUniqueId(), item.cost, true);
                p.getInventory().addItem(item.item.clone());
                String showName = getDisplayName(item.item);
                sendOptionalMessage(p, getMsg("buy-success")
                        .replace("%item%", showName)
                        .replace("%cost%", formatNumberWithComma(item.cost)));
                // **START: โค้ดที่แก้ไข**
                // ไม่ต้องปิด Inventory และไม่ต้องลบ Cache เพื่อให้ซื้อซ้ำได้
                // confirmCache.remove(p.getUniqueId());
                // p.closeInventory();
                // **END: โค้ดที่แก้ไข**
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

    public void loadAfkPools() {
        afkPools.clear();
        if (afkpoolConfig.isConfigurationSection("pools")) {
            for (String key : afkpoolConfig.getConfigurationSection("pools").getKeys(false)) {
                ConfigurationSection section = afkpoolConfig.getConfigurationSection("pools." + key);
                if (section != null) {
                    AFKPool pool = AFKPool.deserialize(key, section);
                    if (pool != null) {
                        afkPools.put(key.toLowerCase(), pool);
                    } else {
                        getLogger().warning("Failed to load AFK Pool: " + key);
                    }
                }
            }
        }
        getLogger().info("Loaded " + afkPools.size() + " AFK Pools.");
    }

    public void saveAfkPools() {
        afkpoolConfig.set("pools", null);
        for (AFKPool pool : afkPools.values()) {
            afkpoolConfig.set("pools." + pool.getName(), pool.serialize());
        }
        try {
            afkpoolConfig.save(afkpoolFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public int getShards(UUID uuid) { return shards.getOrDefault(uuid, 0); }

    @Deprecated
    public void addShards(UUID uuid, int amount, boolean save) {
        addShards(uuid, amount, save, false);
    }

    public void addShards(UUID uuid, int amount, boolean save, boolean silent) {
        shards.put(uuid, getShards(uuid) + amount);
        if (!silent) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline() && amount > 0) notifyReceiveShards(p, amount);
        }
        if (save) savePlayerData();
    }

    public void setShards(UUID uuid, int amount, boolean save) {
        shards.put(uuid, amount);
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) {
            sendOptionalMessage(p, getMsg("shards-set").replace("%amount%", formatNumberWithComma(amount)));
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

    private String formatLocation(Location loc) {
        return "X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z: " + loc.getBlockZ();
    }

    public void notifyReceiveShards(Player p, int amount) {
        String chatMsg = getMsg("shards-receive").replace("%amount%", formatNumberWithComma(amount));
        sendOptionalMessage(p, chatMsg);

        String barMsg = getMsg("shards-receive-bar").replace("%amount%", formatNumberWithComma(amount));
        if (!barMsg.isEmpty()) {
            sendActionBar(p, barMsg);
        }

        playSound(p, "shards-receive");
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
        String priceFmt = getMsg("shop-ui.price-lore");
        if (!priceFmt.isEmpty()) {
            lore.add(priceFmt.replace("%cost%", formatNumber(shopItem.cost)));
        }
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

    private String getMsg(String key) {
        return color(config.getString("messages." + key, config.getString(key, "")));
    }

    private void playSound(Player p, String path) {
        String soundName = config.getString("sounds." + path);
        if (soundName == null || soundName.isEmpty()) return;
        try { p.playSound(p.getLocation(), org.bukkit.Sound.valueOf(soundName.toUpperCase().replace("MINECRAFT:", "")), 1, 1);
        } catch (IllegalArgumentException e) { getLogger().warning("Invalid sound name in config.yml: " + soundName); }
    }

    private void sendActionBar(Player p, String msg) { p.sendActionBar(color(msg)); }

    private void sendOptionalMessage(CommandSender sender, String message) {
        if (message != null && !message.isEmpty()) {
            sender.sendMessage(message);
        }
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
        @Override public String getVersion() { return "1.9";
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