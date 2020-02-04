package org.minevale.bunkers.core.trade;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.minevale.bunkers.core.trade.player.PlayerTrade;
import org.minevale.bunkers.core.trade.player.PlayerTradeState;

@RequiredArgsConstructor
public class TradeListener implements Listener {

    private final TradeManager tradeManager;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerTrade playerTrade = tradeManager.getActiveTrade(player);
        if (playerTrade != null) {
            playerTrade.cancel();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PlayerTrade playerTrade = tradeManager.getActiveTrade(player);
        if (playerTrade != null) {
            playerTrade.cancel();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerTrade playerTrade = tradeManager.getActiveTrade(player);
        if (playerTrade != null && event.getClickedInventory() != null) {
            PlayerTradeState tradeState = playerTrade.getPlayerState().get(player);
            if (event.getClickedInventory().equals(playerTrade.getInventoryByPlayer(player))) { // Allow editing player inventory
                if (playerTrade.getDivider().contains(event.getSlot())) { // Ready up state
                    if (playerTrade.canConfirm()) {
                        playerTrade.setPlayerState(player, PlayerTradeState.CONFIRM);
                    } else if (tradeState != PlayerTradeState.UNREADY) {
                        playerTrade.setPlayerState(player, PlayerTradeState.UNREADY);
                    } else {
                        playerTrade.setPlayerState(player, PlayerTradeState.READY);
                    }
                }

                if (playerTrade.getEditableSlots().contains(event.getSlot()) && !(playerTrade.canConfirm())) {
                    if (event.getCurrentItem() != null) {

                        playerTrade.setPlayerState(player, PlayerTradeState.UNREADY); // todo fix glitches
                    }
                } else {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    // todo add hotkey remove item prevention -- items get lost

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerTrade playerTrade = tradeManager.getActiveTrade(player);
        if (playerTrade != null && event.getView() != null && event.getView().getTopInventory() != null) {
            if (event.getView().getTopInventory().equals(playerTrade.getInventoryByPlayer(player))) { // Allow editing player inventory
                for (Integer slot : event.getInventorySlots()) {
                    if (playerTrade.getEditableSlots().contains(slot)) { // todo improve solution becomes slightly glitchy with bottom inventory
                    } else {
                        event.setCancelled(true);
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
        }
    }

}
