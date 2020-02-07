package org.minevale.bunkers.core.api.exception;

import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.balance.Balance;

public class InsufficientFundsException extends PlayerEconomyException {

    private Balance balance;
    private int amount;

    public InsufficientFundsException(PlayerData player, Balance balance, int amount) {
        super(player, "Insufficient amount, tried to remove " + balance.getId() + " but doesn't have enough");

        this.balance = balance;
        this.amount = amount;
    }

    @Override
    public String getFriendlyMessage() {
        return player.getUsername() + " does not have " + amount + " " + balance.getFriendlyName() + " to remove.";
    }
}
