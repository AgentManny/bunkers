package org.minevale.bunkers.core.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private BunkersCore plugin;

    private final Map<UUID, PlayerData> playerMap = new HashMap<>();
    private MongoCollection<Document> mongoCollection;

    public PlayerDataManager(BunkersCore plugin) {
        this.plugin = plugin;
        mongoCollection = plugin.getMongoDatabase().getCollection("players");

        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(plugin), plugin);
    }

    public int save(boolean forceAll) {
        System.out.println("Saving players to Mongo...");
        int saved = 0;
        long startMs = System.currentTimeMillis();

        for (PlayerData playerData : playerMap.values()) {
            if (playerData.isNeedsSaving() || forceAll) {
                saved++;

                playerData.setNeedsSaving(false);
                save(playerData);
            }
        }

        int time = (int) (System.currentTimeMillis() - startMs);
        if (saved > 0) {
            BunkersCore.broadcast(ChatColor.GREEN + "Updated " + saved + " players (Completed: " + ChatColor.YELLOW + time + "ms" + ChatColor.GREEN + ")", "op");
            System.out.println("Saved " + saved + " players to Mongo in " + time + "ms.");
        }
        return saved;
    }

    public void load(PlayerData playerData) {
        Document document = mongoCollection.find(new Document("uuid", playerData.getUuid().toString())).first();
        playerData.update(document);
    }

    public void save(PlayerData playerData) {
        Document document = playerData.toDocument();
        mongoCollection.replaceOne(Filters.eq("uuid", playerData.getUuid().toString()), document, new ReplaceOptions().upsert(true));
    }

    public PlayerData create(PlayerData playerData, boolean cache) {
        if (!cache) {
            playerMap.put(playerData.getUuid(), playerData);
        }
        load(playerData);
        return playerData;
    }

    public void remove(PlayerData profile) {
        this.playerMap.values().removeIf(p -> p.equals(profile));
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerMap.get(uuid);
    }

    public PlayerData getPlayerData(String playerName) {
        Player player = plugin.getServer().getPlayer(playerName);
        if (player != null) {
            return getPlayerData(player.getUniqueId());
        }

        if (Bukkit.isPrimaryThread()) {
            plugin.getLogger().warning("Loading offline player on main thread. This isn't advised as it may cause main server to freeze up.");
        }

        Document document = mongoCollection.find(Filters.eq("username", playerName)).first();
        if (document != null) {
            PlayerData playerData = new PlayerData(UUID.fromString(document.getString("uuid")), document.getString("username"));
            playerData.update(document);
            return playerData;
        }
        return null;
    }
}
