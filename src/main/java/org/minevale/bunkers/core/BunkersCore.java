package org.minevale.bunkers.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.minevale.bunkers.core.command.TradeCommand;
import org.minevale.bunkers.core.listener.PlayerSyncListener;
import org.minevale.bunkers.core.player.PlayerDataManager;
import org.minevale.bunkers.core.util.serializer.ItemStackAdapter;
import org.minevale.bunkers.core.util.serializer.PotionEffectAdapter;

import java.util.Arrays;

@Getter
public class BunkersCore extends JavaPlugin {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @Getter
    private static BunkersCore instance;

    private MongoDatabase mongoDatabase;

    private PlayerDataManager playerDataManager;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        loadDatabase();

        registerCommands();
        registerManagers();
        registerListeners();
    }

    public void onDisable() {
        save(true);
    }

    public void save(boolean force) {
        playerDataManager.save(force);
    }

    private void registerManagers() {
        this.playerDataManager = new PlayerDataManager(this);

    }

    private void registerCommands() {
        getCommand("trade").setExecutor(new TradeCommand());
    }

    private void registerListeners() {
        Arrays.asList(
                new PlayerSyncListener(this)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    public static void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }

        Bukkit.getServer().getLogger().info("[Broadcast] [G] " + message);
    }

    public static void broadcast(String message, String permission) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (permission.equals("op") ? player.isOp() : (player.isOp() || player.hasPermission(permission))) {
                player.sendMessage(message);
            }
        }

        Bukkit.getServer().getLogger().info("[Broadcast] [P] " + message);
    }

    private void loadDatabase() {
        boolean auth = !getConfig().getString("mongo.authentication.username").isEmpty();

        String database = getConfig().getString("mongo.database");
        String[] data = getConfig().getString("mongo.host").split(":");
        ServerAddress serverAddress = new ServerAddress(data[0], Integer.parseInt(data[1]));

        if (auth) {
            MongoCredential credential = MongoCredential.createCredential(
                    getConfig().getString("mongo.authentication.username"),
                    "admin",
                    getConfig().getString("mongo.authentication.password").toCharArray());

            mongoDatabase = new MongoClient(serverAddress, credential, MongoClientOptions.builder().build())
                    .getDatabase(database);
        } else {
            mongoDatabase = new MongoClient(new ServerAddress(data[0], Integer.parseInt(data[1]))).getDatabase(database);
        }
    }
}
