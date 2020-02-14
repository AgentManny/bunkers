package org.minevale.bunkers.core.chat;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.minevale.bunkers.core.BunkersCore;

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

        //PlayerData playerData = BunkersCore.getInstance().getPlayerDataManager().getPlayerData(player);
        //if (playerData.getChatType() != ChatType.LOCAL) return; // Local message isn't required for any other chat modes

        FileConfiguration config = BunkersCore.getInstance().getConfig();

        ChatColor messageColor = null;
        for (Player other : new ArrayList<>(event.getRecipients())) { // Prevent concurrent exception or array write on read
            int distance = (int) player.getLocation().distanceSquared(other.getLocation());
            for (Map.Entry<Integer, ChatColor> entry : chatManager.getLocalChatRadius().entrySet()) {
                if (distance < entry.getKey()) {
                    messageColor = entry.getValue();
                }
            }

            if (messageColor == null) {
                // Not visible
                event.getRecipients().remove(other);
            } else {
                player.sendMessage(event.getFormat().replace(event.getMessage(), messageColor + event.getMessage())); // Hotfix
            }
        }
    }

}
