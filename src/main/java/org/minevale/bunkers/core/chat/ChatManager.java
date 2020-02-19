package org.minevale.bunkers.core.chat;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.command.ChatCommand;
import org.minevale.bunkers.core.command.SetChatCommand;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class ChatManager {

    private final BunkersCore plugin;

    private boolean chatLock;
    private ChatType chatMode;

    private Map<Integer, ChatColor> localChatRadius = new LinkedHashMap<>();
    private int maxDistance = 0;

    public ChatManager(BunkersCore plugin) {
        this.plugin = plugin;

        FileConfiguration config = plugin.getConfig();

        this.chatMode = ChatType.parse(config.getString("chat.mode", "local"));
        this.chatLock = config.getBoolean("chat.locked", false);

        Set<String> chatRadius = config.getConfigurationSection("chat.local-distance").getKeys(false);
        chatRadius.stream()
                .map(Integer::parseInt)
                .sorted(Integer::compareTo)
                .forEach(value -> {
                    if (maxDistance < value) {
                        maxDistance = value;
                    }
                    ChatColor color = ChatColor.valueOf(config.getString("chat.local-distance." + value));
                    localChatRadius.put(value, color);
                });

        plugin.getCommand("setchat").setExecutor(new SetChatCommand(this));
        plugin.getCommand("chat").setExecutor(new ChatCommand(this));

        plugin.getServer().getPluginManager().registerEvents(new ChatListener(this), plugin);
    }

    public void setChatLock(boolean chatLock) {
        this.chatLock = chatLock;
        plugin.getConfig().set("chat.locked", chatLock);
    }

    public void setChatMode(ChatType chatMode) {
        this.chatMode = chatMode;
        plugin.getConfig().set("chat.mode", chatMode.id());

    }

}
