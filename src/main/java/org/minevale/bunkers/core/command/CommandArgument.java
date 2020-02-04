package org.minevale.bunkers.core.command;

import org.bukkit.command.CommandSender;

public interface CommandArgument {

    void execute(CommandSender sender, String[] args);

    String usage();

}
