package codes.laivy.quests.quests.objectives.reward.money;

import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.compatibility.vault.VaultCompatibility;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.reward.Reward;
import codes.laivy.quests.quests.objectives.reward.RewardType;
import codes.laivy.quests.utils.MoneyUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        // TODO: 26/05/2023 Unchecked
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

        String amountString;
        if (laivyQuests().getConfig().getBoolean("menus.money symbol", true)) {
            amountString = "+$" + MoneyUtils.formatNumber(getAmount());
        } else {
            amountString = "+$" + MoneyUtils.formatCurrency(getAmount());
        }

        if (getAmount() <= 1) {
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

    public static final class Type extends RewardType<MoneyReward> {
        public Type() {
            super("MONEY");
        }

        @Override
        public @NotNull Serializer<MoneyReward> getSerializer() {
            return new Serializer<MoneyReward>() {
                @Override
                public @NotNull JsonElement serialize(@NotNull MoneyReward reward) {
                    JsonObject object = new JsonObject();

                    object.addProperty("amount", reward.getAmount());

                    return object;
                }

                @Override
                public @NotNull MoneyReward deserialize(@NotNull JsonElement reward) {
                    JsonObject object = reward.getAsJsonObject();
                    double amount = object.get("amount").getAsDouble();
                    return new MoneyReward(amount);
                }
            };
        }
    }
}
