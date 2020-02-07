package org.minevale.bunkers.core.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.player.bunker.PlayerBunker;
import org.minevale.bunkers.core.player.currencies.CurrencyType;
import org.minevale.bunkers.core.player.inventory.PlayerInventoryData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {

    private final UUID uuid;
    private final String username;

    private PlayerBunker playerBunker;
    private PlayerInventoryData inventoryData; // todo remove after bunker is done or maybe use for lobby?

    private Map<CurrencyType, Integer> balanceMap = new HashMap<>();
    private long lastBalanceCheck = -1; // This could be a simple solution for continuously updating balance

    private boolean syncing = true; // Do not allow anyone to do anything before they're synced
    private boolean needsSaving = false;

    public void update(Document document) {
        if (document == null) {
            Player player = getPlayer();
            if (player != null) {
                this.inventoryData = new PlayerInventoryData(this);
                recalculateBalance();
            }
            save(); // Saves to disk
            return;
        }

        this.playerBunker = BunkersCore.getInstance().getBunkerHandler().createBunker(this); // Create random for now

        Document balanceData = document.get("balance", Document.class);
        for (CurrencyType balance : CurrencyType.values()) {
            balanceMap.put(balance, balanceData.getInteger(balance.getId(), 0));
        }

        if (document.containsKey("inventory")) {
            this.inventoryData = PlayerInventoryData.deserialize(document.get("inventory", Document.class).toJson()); // Should try to simplify
        }
    }

    public void save() {
        needsSaving = false;
        BunkersCore.getInstance().getPlayerDataManager().save(this);
    }

    public Document toDocument() {
        Document document = new Document("uuid", uuid.toString())
                .append("username", username);

        Document balanceData = new Document();
        for (CurrencyType balance : CurrencyType.values()) {
            balanceData.put(balance.getId(), balanceMap.getOrDefault(balance, 0));
        }
        document.append("balance", balanceData);

        Player player = getPlayer();
        if (player != null) {
            inventoryData.update(player); // Resynchronise
        }
        document.append("inventory", PlayerInventoryData.getAsDocument(inventoryData));
        return document;
    }

    public void recalculateBalance() {
        if (lastBalanceCheck + 150L > System.currentTimeMillis()) {
            return;
        }
        lastBalanceCheck = System.currentTimeMillis() + 150L;
        Player player = getPlayer();
        if (player != null) {
            inventoryData.update(player);
        }

        for (CurrencyType balance : CurrencyType.values()) {
            balanceMap.put(balance, balance.getAmountFromInventory(inventoryData));
        }
    }

    public int getBalance() {
        recalculateBalance(); // Prevent too many updates
        AtomicInteger amount = new AtomicInteger();
        balanceMap.forEach((balance, value) -> amount.addAndGet(value));
        return amount.get();
    }

    public int getBalance(CurrencyType balance) {
        recalculateBalance();
        return balanceMap.getOrDefault(balance, 0);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
