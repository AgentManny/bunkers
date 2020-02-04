package org.minevale.bunkers.core.command.economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.minevale.bunkers.core.command.CommandArgument;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EconomyCommand implements CommandExecutor {

    private Map<String, CommandArgument> arguments = new HashMap<>();

    public EconomyCommand() {
        arguments.put("balance", new EconomyBalanceArgument());
        arguments.put("clear", new EconomyClearArgument());
        arguments.put("add", new EconomyAddArgument());
        arguments.put("remove", new EconomyRemoveArgument());
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            arguments.forEach((argumentLabel, argument) -> {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + argument.usage());
            });
            return true;
        }

        Optional<Map.Entry<String, CommandArgument>> argument = arguments.entrySet().stream()
                .filter(arg -> arg.getKey().equalsIgnoreCase(args[0]))
                .findAny();

        if (argument.isPresent()) {
            argument.get().getValue().execute(sender, args);
        } else {
            sender.sendMessage(ChatColor.RED + "Command argument /" + label + " " + args[0] + " not found.");
        }
        return true;
    }
}
