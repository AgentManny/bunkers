package org.minevale.bunkers.core.api;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.api.exception.InsufficientFundsException;
import org.minevale.bunkers.core.api.exception.InventoryFullException;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.balance.Balance;
import org.minevale.bunkers.core.util.InventoryUtils;

@RequiredArgsConstructor
public class BunkersCoreApi implements BunkersApi {

    private final BunkersCore bunkersCore;

    private PlayerData getPlayerData(Player player) {
        return bunkersCore.getPlayerDataManager().getPlayerData(player); // Method used to accessing it faster
    }

    @Override
    public int getBalance(Player player) {
        PlayerData playerData = getPlayerData(player);
        return playerData.getBalance();
    }

    @Override
    public int clearBalance(Player player) {
        PlayerData playerData = getPlayerData(player);
        int balanceBefore = playerData.getBalance();

        for (Balance balance : Balance.values()) {
            while (player.getInventory().contains(balance.getType())) {
                player.getInventory().remove(balance.getType());
            }
        }
        player.updateInventory();
        return balanceBefore;
    }

    @Override
    public int clearBalance(Player player, Balance balance) {
        PlayerData playerData = getPlayerData(player);
        int balanceBefore = playerData.getBalance(balance);

        while (player.getInventory().contains(balance.getType())) {
            player.getInventory().remove(balance.getType());
        }

        player.updateInventory();
        return balanceBefore;
    }

    @Override
    public int addCurrency(Player player, Balance balance, int amount) throws InventoryFullException {
        ItemStack item = balance.getItem().clone();
        item.setAmount(amount);
        if (InventoryUtils.fits(item, player.getInventory())) {
            player.getInventory().addItem(item);
            return amount;
        }
        throw new InventoryFullException(player, player.getInventory(), item);
    }

    @Override
    public int removeCurrency(Player player, Balance balance, int amount) throws InsufficientFundsException {
        PlayerData playerData = getPlayerData(player);
        int contains = playerData.getBalance(balance);
        if (contains < amount) {
            throw new InsufficientFundsException(player, balance, amount);
        }

        player.getInventory().removeAmount(balance.getType(), amount);
        return amount;
    }

    @Override
    public double getCoins(Player player) {
        PlayerData playerData = getPlayerData(player);
        return playerData.getBalance(Balance.COINS);
    }

    @Override
    public double addCoins(Player player, int amount) {
        PlayerData playerData = getPlayerData(player);
        player.getInventory().addItem(new ItemStack(Balance.COINS.getType(), amount));
        return playerData.getBalance(Balance.COINS);
    }

    @Override
    public double removeCoins(Player player, int amount) {
        PlayerData playerData = getPlayerData(player);
        player.getInventory().removeAmount(Balance.COINS.getType(), amount);
        return playerData.getBalance(Balance.COINS);
    }

    @Override
    public double addNuggets(Player player, int amount) {
        PlayerData playerData = getPlayerData(player);
        player.getInventory().addItem(new ItemStack(Balance.NUGGETS.getType(), amount));
        return playerData.getBalance(Balance.NUGGETS);
    }

    @Override
    public double removeNuggets(Player player, int amount) {
        PlayerData playerData = getPlayerData(player);
        player.getInventory().removeAmount(Balance.NUGGETS.getType(), amount);
        return playerData.getBalance(Balance.NUGGETS);
    }

    @Override
    public double addBars(Player player, int amount) {
        PlayerData playerData = getPlayerData(player);
        player.getInventory().addItem(new ItemStack(Balance.BARS.getType(), amount));
        return playerData.getBalance(Balance.BARS);
    }

    @Override
    public double removeBars(Player player, int amount) {
        PlayerData playerData = getPlayerData(player);
        player.getInventory().removeAmount(Balance.BARS.getType(), amount);
        return playerData.getBalance(Balance.BARS);
    }
}
