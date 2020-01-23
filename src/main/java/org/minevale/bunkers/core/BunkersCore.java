package org.minevale.bunkers.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.minevale.bunkers.core.player.PlayerDataManager;

@Getter
public class BunkersCore extends JavaPlugin {

    @Getter
    private static BunkersCore instance;

    private MongoDatabase mongoDatabase;

    private PlayerDataManager playerDataManager;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        registerCommands();
        registerManagers();
        registerListeners();

        loadDatabase();
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

    }

    private void registerListeners() {
//        Arrays.asList(
//                new PlayerListener(this)
//        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
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