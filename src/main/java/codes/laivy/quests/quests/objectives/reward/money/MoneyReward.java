package codes.laivy.quests.quests.objectives.reward.money;

import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.compatibility.VaultCompatibility;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.complements.Rewardable;
import codes.laivy.quests.quests.objectives.reward.Reward;
import codes.laivy.quests.quests.objectives.reward.RewardType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

import static codes.laivy.quests.LaivyQuests.getCompatibility;
import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class MoneyReward implements Reward {

    private final double amount;

    public MoneyReward(double amount) {
        if (LaivyQuests.getCompatibility("Vault") == null) {
            throw new NullPointerException("This reward type requires Vault!");
        }

        this.amount = amount;

        if (amount < 0) {
            throw new IllegalArgumentException("The amount cannot be lower than zero.");
        }
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public @NotNull RewardType<Reward> getType() {
        //noinspection unchecked
        return (RewardType<Reward>) laivyQuests().getApi().getRewardType("MONEY");
    }

    @Override
    public @NotNull IMessage getMessage() {
        String cashPrefixSingular = "dollar";
        String cashPrefixPlural = "dollars";

        VaultCompatibility compatibility = LaivyQuests.getCompatibility("Vault");
        if (compatibility != null) {
            Economy economy = compatibility.getEconomy();

            cashPrefixSingular = economy.currencyNameSingular();
            cashPrefixPlural = economy.currencyNamePlural();
        }

        // TODO: 21/05/2023 Bank
        double amount = getAmount();
        String amountString = "§a+$" + amount;

        if (amount <= 1) {
            amountString = amountString + " " + cashPrefixSingular;
        } else {
            amountString = amountString + " " + cashPrefixPlural;
        }

        return laivyQuests().getMessageStorage().getMessage("Reward type: money reward message", amountString);
    }

    @Override
    public void give(@NotNull Quest quest, @NotNull Objective objective) {
        @NotNull VaultCompatibility compatibility = Objects.requireNonNull(getCompatibility("Vault"));
        Economy economy = compatibility.getEconomy();

        economy.depositPlayer(Bukkit.getOfflinePlayer(quest.getUniqueId()), getAmount());
    }
}
