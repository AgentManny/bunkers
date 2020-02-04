package org.minevale.bunkers.core.trade.player;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.minevale.bunkers.core.BunkersCore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PlayerTrade {

    private final Player sender;
    private final Player target;

    private Map<Player, PlayerTradeState> playerState = new HashMap<>();

    private List<Integer> editableSlots = Arrays.asList(
            0, 1, 2, 3,
            9, 10, 11, 12,
            18, 19, 20, 21,
            27, 28, 29, 30
    );

    private List<Integer> viewingSlots = Arrays.asList(
            5, 6, 7, 8,
            14, 15, 16, 17,
            23, 24, 25, 26,
            32, 33, 34, 35
    ); // Opposite player

    private List<Integer> divider = Arrays.asList(4, 13, 22, 31);
    private int[] playerDivider = new int[] { 4, 13 };
    private int[] opponentDivider = new int[] { 22, 31 };

    private Inventory senderInventory;
    private Inventory targetInventory;

    private boolean senderReady = false;

    private BukkitTask updateTask;

    public PlayerTrade(Player sender, Player target) {
        this.sender = sender;
        this.target = target;

        playerState.put(sender, PlayerTradeState.UNREADY);
        playerState.put(target, PlayerTradeState.UNREADY);

        this.senderInventory = Bukkit.createInventory(sender, 36, "Trading: " + target.getName());
        this.targetInventory = Bukkit.createInventory(target, 36, "Trading: " + sender.getName());

        // Prevent conflicts
        this.sender.closeInventory();
        this.target.closeInventory();

        this.sender.openInventory(senderInventory);
        this.target.openInventory(targetInventory);

        updateDivider();

        updateTask = BunkersCore.getInstance().getServer().getScheduler().runTaskTimer(BunkersCore.getInstance(), this::refresh, 10L, 10L);
    }

    public void cancel() {
        for (Player player : getPlayers()) {
            Inventory inventory = getInventoryByPlayer(player);
            if (isConfirmed()) { // Done
                Inventory otherInventory = getInventoryByPlayer(getOppositePlayer(player));
                editableSlots.forEach(slot -> {
                    ItemStack item = otherInventory.getItem(slot);
                    if (item != null) {
                        player.getInventory().addItem(item);
                        inventory.setItem(slot, null);
                    }
                });
                player.sendMessage(ChatColor.GREEN + "Trade complete.");
            } else {
                editableSlots.forEach(slot -> {
                    ItemStack item = inventory.getItem(slot);
                    if (item != null) {
                        player.getInventory().addItem(item);
                        inventory.setItem(slot, null);
                    }
                });
                player.sendMessage(ChatColor.RED + "Trade cancelled.");
            }

            BunkersCore.getInstance().getTradeManager().getActivePlayerTrades().remove(player.getUniqueId());
            BunkersCore.getInstance().getServer().getScheduler().runTaskLater(BunkersCore.getInstance(), () -> {
                if (player != null) {
                    player.closeInventory();
                }
            }, 4L);
        }
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    public void setPlayerState(Player player, PlayerTradeState newState) {
        if (playerState.get(player) == newState) return;

        playerState.put(player, newState);
        if (isConfirmed()) {
            cancel();
        } else {
            updateDivider();
        }
    }

    public void refresh() {
        for (Player sender : getPlayers()) {
            Player target = getOppositePlayer(sender);

            Inventory targetInventory = getInventoryByPlayer(target);
            Inventory senderInventory = getInventoryByPlayer(sender);

            for (int i = 0; i < editableSlots.size(); i++) {
                int targetSlot = viewingSlots.get(i);
                int senderSlot = editableSlots.get(i);

                senderInventory.setItem(targetSlot, targetInventory.getItem(senderSlot));
                targetInventory.setItem(senderSlot, senderInventory.getItem(targetSlot));
            }
        }
    }

    private void updateDivider() {
        for (Player player : getPlayers()) {
            Player opponent = getOppositePlayer(player);

            PlayerTradeState playerTrade = playerState.get(player);
            Inventory playerInventory = getInventoryByPlayer(player);

            PlayerTradeState opponentTrade = playerState.get(opponent);
            Inventory opponentInventory = getInventoryByPlayer(opponent);

            for (Integer slot : playerDivider) {
                playerInventory.setItem(slot, playerTrade.getItem());
                opponentInventory.setItem(slot, opponentTrade.getItem());
            }

            for (Integer slot : opponentDivider) {
                playerInventory.setItem(slot, opponentTrade.getItem());
                opponentInventory.setItem(slot, playerTrade.getItem());
            }
        }
    }

    public boolean canConfirm() {
        return playerState.get(sender) == PlayerTradeState.READY && playerState.get(target) == PlayerTradeState.READY || (playerState.get(sender) == PlayerTradeState.CONFIRM || playerState.get(target) == PlayerTradeState.CONFIRM);
    }

    public boolean isConfirmed() {
        return playerState.get(sender) == PlayerTradeState.CONFIRM && playerState.get(target) == PlayerTradeState.CONFIRM;
    }

    public Inventory getInventoryByPlayer(Player player) {
        if (player == sender) {
            return senderInventory;
        }
        return targetInventory;
    }

    public Player getOppositePlayer(Player player) { // retarded but it works
        if (player == sender) {
            return target;
        }
        return sender;
    }

    public Player[] getPlayers() {
        return new Player[] { sender, target };
    }



}
