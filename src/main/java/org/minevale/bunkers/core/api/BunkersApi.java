package org.minevale.bunkers.core.api;

import org.bukkit.entity.Player;

public interface BunkersApi {

    /**
     * Balance calculation will translate all currencies back to coins
     *
     * @param player Player balance to fetch
     *
     * @return total sum of coins
     */
    int getBalance(Player player);

    /**
     * Removes all coins, nuggets and bars from the players inventory.
     *
     * @param player Player balance to clear
     *
     * @return total sum of coins removed
     */
    int clearBalance(Player player);

    /**
     * Balance of the player including only coins.
     *
     * @param player Player balance to get
     * @return
     */
    double getCoins(Player player);

    double addCoins(Player player, int amount);

    double removeCoins(Player player, int amount);

    double addNuggets(Player player, int amount);

    double removeNuggets(Player player, int amount);

    double addBars(Player player, int amount);

    double removeBars(Player player, int amount);

    /*
    getCoins(Player p) - Returns specifically the number of coins the player holds, excludes nuggets and bars. Returns an integer
getNuggets(Player p) - Returns specifically the number of nuggets a player holds, excluding coins and bars. Returns an integer
getBars(Player p) - Returns specifically the number of bars a player holds, excluding coins and nuggets. Returns an integer
addCoins(Player p) - Add specified number of coins to the players inventory. If inventory is full or cannot hold more, then send an error message to the sender.
addNuggets(Player p) - Add specified number of nuggets to the player’s inventory. If inventory is full or cannot hold more, then send an error message to the sender.
addBars(Player p) - Add specified number of bars to the player’s inventory. If inventory is full or cannot hold more, send an error message to the sender.
removeCoins(Player p) - Remove specified number of coins to the players inventory. If inventory does not contain coins, then send an error message to the sender.
removeNuggets(Player p) - Remove specified number of nuggets to the player’s inventory. If inventory does not contain nuggets, then send an error message to the sender.
removeBars(Player p) - Remove specified number of bars to the player’s inventory. If inventory does not contain bars, then send an error message to the sender.

     */

}
