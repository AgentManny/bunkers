package org.minevale.bunkers.core.chat;

import lombok.Getter;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.command.ChatCommand;
import org.minevale.bunkers.core.command.SetChatCommand;

@Getter
public class ChatManager {

    private final BunkersCore plugin;

    private boolean chatLock;
    private ChatType chatMode;

    public ChatManager(BunkersCore plugin) {
        this.plugin = plugin;

        this.chatMode = ChatType.parse(plugin.getConfig().getString("chat.mode", "local"));
        this.chatLock = plugin.getConfig().getBoolean("chat.locked", false);

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
