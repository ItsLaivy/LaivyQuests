package codes.laivy.quests.quests.objectives.reward.money;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.quests.objectives.reward.RewardType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class MoneyRewardType extends RewardType<MoneyReward> {

    public MoneyRewardType() {
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
