package org.minevale.bunkers.core.trade;

import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.trade.player.PlayerTrade;
import org.minevale.bunkers.core.trade.player.PlayerTradeRequest;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TradeManager {

    private final BunkersCore plugin;

    @Getter
    private Set<PlayerTradeRequest> playerTradeRequests = new HashSet<>();

    @Getter
    private Map<UUID, PlayerTrade> activePlayerTrades = new HashMap<>();

    public TradeManager(BunkersCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new TradeListener(this), plugin);
    }

    public void acceptTrade(Player requester, Player target) {
        PlayerTrade playerTrade = new PlayerTrade(requester, target);
        activePlayerTrades.put(requester.getUniqueId(), playerTrade);
        activePlayerTrades.put(target.getUniqueId(), playerTrade);
        playerTradeRequests.removeIf(trade -> (trade.getSender() == requester.getUniqueId() || trade.getTarget() == requester.getUniqueId()) && (trade.getSender() == target.getUniqueId() || trade.getTarget() == target.getUniqueId()));
    }

    public void createTradeRequest(Player requester, Player target) {
        if (hasTradeRequest(requester)) {
            PlayerTradeRequest request = getTradeRequest(requester, target);
            if (getTradeRequest(requester) != request) {
                requester.sendMessage(ChatColor.RED + "You already have a pending trade request."); // Prevent multiple trades from being accepted -- Dupes ect ect
                return;
            }

            if (request != null) {
                if (request.getSender() == requester.getUniqueId()) {
                    requester.sendMessage(ChatColor.RED + "You already have a pending trade request with " + target.getName() + ".");
                } else {
                    acceptTrade(requester, target);
                }
                return;
            }
        }

        requester.sendMessage(ChatColor.GREEN + "You've sent a trade request to " + target.getName() + ", it'll expire in " + BunkersCore.getInstance().getConfig().getInt("trade.expire-time", 30) + " seconds.");

        new FancyMessage("You've received a trade request from " + requester.getName() + ". ")
                .color(ChatColor.GREEN)
                .then("Click Here or type /trade " + requester.getName() + " to accept")
                .tooltip(ChatColor.AQUA + "Accept trade from " + requester.getName())
                .command("/trade " + requester.getName())
                .color(ChatColor.GREEN)
                .send(target);

        int expireTime = BunkersCore.getInstance().getConfig().getInt("trade.expire-time", 30);
        long alive = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expireTime);

        PlayerTradeRequest tradeRequest = new PlayerTradeRequest(requester.getUniqueId(), target.getUniqueId(), alive);

        playerTradeRequests.add(tradeRequest);
        BunkersCore.getInstance().getServer().getScheduler().runTaskLater(BunkersCore.getInstance(), () -> playerTradeRequests.remove(tradeRequest), expireTime * 20L);
    }

    /**
     * Checks if a player has a pending trade request
     *
     * @param requester Player to check
     * @return Whether they have a pending target request
     */
    public boolean hasTradeRequest(Player requester) {
        return getTradeRequest(requester) != null;
    }

    public PlayerTradeRequest getTradeRequest(Player requester) {
        return playerTradeRequests.stream()
                .filter(PlayerTradeRequest::isValid)
                .filter(request -> request.getSender() == requester.getUniqueId() || request.getTarget() == requester.getUniqueId())
                .findAny()
                .orElse(null);
    }

    public PlayerTrade getActiveTrade(Player requester) {
        return activePlayerTrades.get(requester.getUniqueId());
    }

    public PlayerTradeRequest getTradeRequest(Player requester, Player target) {
        return playerTradeRequests.stream()
                .filter(PlayerTradeRequest::isValid)
                .filter(request -> request.getSender() == requester.getUniqueId() || request.getTarget() == requester.getUniqueId())
                .filter(request -> request.getSender() == target.getUniqueId() || request.getTarget() == target.getUniqueId())
                .findAny()
                .orElse(null);
    }

}
