package codes.laivy.quests.api.provider.objectives.entities.fish;

import codes.laivy.quests.api.Serializer;
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
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class FishingObjectiveType extends ObjectiveType {

    public static final class Events implements Listener {
        @EventHandler
        private void fishing(@NotNull PlayerFishEvent e) {
            if (!(e.getCaught() != null && e.getCaught() instanceof Item)) {
                return;
            }

            Item item = (Item) e.getCaught();

            QuestsPlayerData data = laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId());

            for (Quest quest : data.getQuests()) {
                for (Objective objective : quest.getObjectives(true)) {
                    if (!objective.isCompleted() && objective instanceof FishingObjective) {
                        final FishingObjective holder = (FishingObjective) objective;

                        if (holder.getFish().equals(item.getItemStack())) {
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

    public static final @NotNull String FISHING_OBJECTIVE_TYPE_ID = "FISHING";

    public FishingObjectiveType() {
        super(
                FISHING_OBJECTIVE_TYPE_ID,
                new Serializer<Objective>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull Objective o) {
                        if (!(o instanceof FishingObjective)) {
                            throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + FISHING_OBJECTIVE_TYPE_ID + "'");
                        }
                        FishingObjective objective = (FishingObjective) o;

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

                        JsonObject entityObject = new JsonObject();
                        entityObject.addProperty("type id", objective.getFish().getType().getId());
                        entityObject.add("data", objective.getFish().getType().getSerializer().serialize(objective.getFish()));

                        object.add("item", entityObject);
                        object.addProperty("meta", objective.getMeta());
                        object.addProperty("progress", objective.getProgress());

                        return object;
                    }

                    @Override
                    public @NotNull FishingObjective deserialize(@NotNull JsonElement o) {
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
                        ItemType<? extends codes.laivy.quests.api.provider.objectives.items.mechanic.Item> itemType = laivyQuests().getApi().getItemType(itemObject.get("type id").getAsString());
                        codes.laivy.quests.api.provider.objectives.items.mechanic.Item item = itemType.getSerializer().deserialize(itemObject.get("data"));

                        int meta = object.get("meta").getAsInt();
                        int progress = object.get("progress").getAsInt();

                        return new FishingObjective(name, description, item, meta, progress, reward);
                    }
                }
        );
    }

    @Override
    public @NotNull IMessage getName(@NotNull Objective objective) {
        if (objective instanceof FishingObjective) {
            FishingObjective o = (FishingObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: fishing name", o.getFish().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }

    @Override
    public @NotNull IMessage getDescription(@NotNull Objective objective) {
        if (objective instanceof FishingObjective) {
            FishingObjective o = (FishingObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: fishing lore", o.getMeta(), o.getFish().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }

}
