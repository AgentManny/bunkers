package org.minevale.bunkers.core.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.player.inventory.PlayerInventoryData;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {

    private final UUID uuid;
    private final String username;

    private PlayerInventoryData inventoryData;

    private int coins = 0;
    private int nuggets = 0;
    private int bars = 0;

    private boolean needsSaving = false;

    public void update(Document document) {
        if (document == null) {
            Player player = getPlayer();
            if (player != null) {
                this.inventoryData = new PlayerInventoryData(player);
            }
            save(); // Saves to disk
            return;
        }

        Document balanceData = document.get("balance", Document.class);
        this.coins = balanceData.getInteger("coins", 0);
        this.nuggets = balanceData.getInteger("nuggets", 0);
        this.bars = balanceData.getInteger("bars", 0);
    }

    public void save() {
        needsSaving = false;
        BunkersCore.getInstance().getPlayerDataManager().save(this);
    }

    public Document toDocument() {
        Document balanceData = new Document("coins", coins)
                .append("nuggets", nuggets)
                .append("bars", bars);

        return new Document("uuid", uuid.toString())
                .append("username", username)
                .append("balance", balanceData);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
