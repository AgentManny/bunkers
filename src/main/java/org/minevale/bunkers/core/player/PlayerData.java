package org.minevale.bunkers.core.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {

    private final UUID uuid;
    private final String username;

    private int coins;
    private int nuggets;
    private int bars;

    private boolean needsSaving = false;

    public void update(Document document) {
        if (document == null) {
            return;
        }

        Document balanceData = document.get("Document", Document.class);
        this.coins = balanceData.getInteger("coins", 0);
        this.nuggets = balanceData.getInteger("nuggets", 0);
        this.bars = balanceData.getInteger("bars", 0);
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
