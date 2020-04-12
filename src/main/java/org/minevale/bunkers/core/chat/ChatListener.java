package org.minevale.bunkers.core.chat;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final ChatManager chatManager;

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (chatManager.isChatLock() && !player.hasPermission("minevale.chatlock.bypass")) {
            player.sendMessage(ChatColor.RED + "Chat is currently locked.");
            event.setCancelled(true);
            return;
        }

        if (chatManager.getChatMode() != ChatType.LOCAL) return;
        event.setCancelled(true);

        ChatColor messageColor = null;
        for (Player other : new ArrayList<>(event.getRecipients())) {
            if (player.getWorld().getName().equalsIgnoreCase(other.getWorld().getName())) {
                int distance = (int) player.getLocation().distance(other.getLocation());
                for (Map.Entry<Integer, ChatColor> entry : chatManager.getLocalChatRadius().entrySet()) {
                    if (distance < entry.getKey()) {
                        messageColor = entry.getValue();
                        break;
                    }
                }

                if ((distance < chatManager.getMaxDistance()) && messageColor != null) {
                    other.sendMessage(String.format(event.getFormat(), event.getPlayer().getDisplayName(), messageColor + event.getMessage()));
                } else {
                    event.getRecipients().remove(other); // Plugin compatibility
                }
            }
        }
    }

}
