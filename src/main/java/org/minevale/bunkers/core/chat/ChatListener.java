package org.minevale.bunkers.core.chat;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final ChatManager chatManager;

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (chatManager.isChatLock() && !player.hasPermission("minevale.chatlock.bypass")) {
            player.sendMessage(ChatColor.RED + "Chat is currently locked.");
            event.setCancelled(true);
            return;
        }

        if (chatManager.getChatMode() != ChatType.LOCAL) return;

        //PlayerData playerData = BunkersCore.getInstance().getPlayerDataManager().getPlayerData(player);
        //if (playerData.getChatType() != ChatType.LOCAL) return; // Local message isn't required for any other chat modes

        ChatColor messageColor = null;
        for (Player other : new ArrayList<>(event.getRecipients())) { // Prevent concurrent exception or array write on read
            double distance = player.getLocation().distanceSquared(other.getLocation());
            if (distance < 20) {
                messageColor = ChatColor.WHITE;
            } else if (distance < 40) {
                messageColor = ChatColor.GRAY;
            } else if (distance < 60) {
                messageColor = ChatColor.DARK_GRAY;
            } else {
                // Not visible
                event.getRecipients().remove(other);
            }
        }
        if (messageColor != null) {
            //event.setFormat();
            event.setMessage(messageColor + event.getMessage()); // Simple solution and shouldn't cause conflicts
        }
    }

}
