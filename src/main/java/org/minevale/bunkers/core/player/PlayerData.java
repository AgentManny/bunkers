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

    private boolean needsSaving = false;

    public void update(Document document) {
        if (document == null) {
            return;
        }

    }

    public Document toDocument() {
        return new Document("uuid", uuid.toString())
                .append("username", username);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
