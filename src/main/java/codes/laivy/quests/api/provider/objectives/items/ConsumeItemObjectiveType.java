package codes.laivy.quests.api.provider.objectives.items;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.api.provider.objectives.entities.kill.EntityKillObjectiveType;
import codes.laivy.quests.api.provider.objectives.items.mechanic.Item;
import codes.laivy.quests.api.provider.objectives.items.mechanic.ItemType;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.QuestsPlayerData;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.objectives.reward.Reward;
import codes.laivy.quests.quests.objectives.reward.RewardType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class ConsumeItemObjectiveType extends ObjectiveType {

    public static final class Events implements Listener {
        @EventHandler
        private void itemConsume(@NotNull PlayerItemConsumeEvent e) {
            QuestsPlayerData data = laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId());

            for (Quest quest : data.getQuests()) {
                for (Objective objective : quest.getObjectives(true)) {
                    if (!objective.isCompleted() && objective instanceof ConsumeItemObjective) {
                        final ConsumeItemObjective holder = (ConsumeItemObjective) objective;

                        if (holder.getItem().isSimilar(e.getItem())) {
                            int current = holder.getProgress();
                            holder.setProgress(current + 1);
                        }

                        if (objective.isCompleted()) {
                            objective.complete(quest);
                        }
                    }
                }
            }
        }
    }

    private static final @NotNull Events EVENTS = new Events();

    static {
        Bukkit.getPluginManager().registerEvents(EVENTS, laivyQuests());
    }

    public static final @NotNull String CONSUME_ITEM_OBJECTIVE_TYPE_ID = "CONSUME_ITEM";

    public ConsumeItemObjectiveType() {
        super(
                CONSUME_ITEM_OBJECTIVE_TYPE_ID,
                new Serializer<Objective>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull Objective o) {
                        if (!(o instanceof ConsumeItemObjective)) {
                            throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + CONSUME_ITEM_OBJECTIVE_TYPE_ID + "'");
                        }
                        ConsumeItemObjective objective = (ConsumeItemObjective) o;

                        JsonObject object = new JsonObject();

                        object.addProperty("name", objective.getName().getId());
                        object.addProperty("description", objective.getDescription().getId());

                        if (objective.getReward() != null) {
                            JsonObject rewardObject = new JsonObject();
                            Reward reward = objective.getReward();

                            rewardObject.addProperty("type id", reward.getType().getId());
                            rewardObject.add("data", reward.getType().getSerializer().serialize(reward));

                            object.add("reward", rewardObject);
                        }

                        JsonObject block = new JsonObject();
                        block.addProperty("type id", objective.getItem().getType().getId());
                        block.add("data", objective.getItem().getType().getSerializer().serialize(objective.getItem()));

                        object.add("item", block);
                        object.addProperty("meta", objective.getMeta());
                        object.addProperty("progress", objective.getProgress());

                        return object;
                    }

                    @Override
                    public @NotNull ConsumeItemObjective deserialize(@NotNull JsonElement o) {
                        JsonObject object = o.getAsJsonObject();

                        IMessage name = laivyQuests().getMessageStorage().getMessage(object.get("name").getAsString());
                        IMessage description = laivyQuests().getMessageStorage().getMessage(object.get("description").getAsString());

                        @Nullable Reward reward = null;
                        if (object.has("reward")) {
                            JsonObject rewardObject = object.getAsJsonObject("reward");
                            String typeId = rewardObject.get("type id").getAsString();

                            RewardType<? extends Reward> type = laivyQuests().getApi().getRewardType(typeId);
                            reward = type.getSerializer().deserialize(rewardObject.get("data"));
                        }

                        JsonObject itemObject = object.getAsJsonObject("item");
                        ItemType<? extends Item> itemType = laivyQuests().getApi().getItemType(itemObject.get("type id").getAsString());
                        Item item = itemType.getSerializer().deserialize(itemObject.get("data"));

                        int meta = object.get("meta").getAsInt();
                        int progress = object.get("progress").getAsInt();

                        return new ConsumeItemObjective(name, description, item, meta, progress, reward);
                    }
                }
        );
    }

    @Override
    public @NotNull IMessage getName(@NotNull Objective objective) {
        if (objective instanceof ConsumeItemObjective) {
            ConsumeItemObjective o = (ConsumeItemObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: consume item name", o.getItem().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }

    @Override
    public @NotNull IMessage getDescription(@NotNull Objective objective) {
        if (objective instanceof ConsumeItemObjective) {
            ConsumeItemObjective o = (ConsumeItemObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: consume item lore", o.getMeta(), o.getItem().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }
}
