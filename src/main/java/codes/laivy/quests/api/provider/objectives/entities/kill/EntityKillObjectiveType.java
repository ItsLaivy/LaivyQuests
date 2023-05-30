package codes.laivy.quests.api.provider.objectives.entities.kill;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.IEntity;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class EntityKillObjectiveType extends ObjectiveType {

    public static final class Events implements Listener {
        @EventHandler
        private void entityKill(@NotNull EntityDeathEvent e) {
            @Nullable Player killer = e.getEntity().getKiller();

            if (killer != null) {
                QuestsPlayerData data = laivyQuests().getApi().getPlayerData(killer.getUniqueId());

                for (Quest quest : data.getQuests()) {
                    for (Objective objective : quest.getObjectives(true)) {
                        if (!objective.isCompleted() && objective instanceof EntityKillObjective) {
                            final EntityKillObjective holder = (EntityKillObjective) objective;
                            IEntity entity = holder.getEntity();

                            if (entity.equals(e.getEntity())) {
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
    }

    private static final @NotNull Events EVENTS = new Events();

    static {
        Bukkit.getPluginManager().registerEvents(EVENTS, laivyQuests());
    }

    public static final @NotNull String ENTITY_KILL_OBJECTIVE_TYPE_ID = "ENTITY_KILL";

    @ApiStatus.Internal
    public EntityKillObjectiveType() {
        super(
                ENTITY_KILL_OBJECTIVE_TYPE_ID,
                new Serializer<Objective>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull Objective o) {
                        if (!(o instanceof EntityKillObjective)) {
                            throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + ENTITY_KILL_OBJECTIVE_TYPE_ID + "'");
                        }
                        EntityKillObjective objective = (EntityKillObjective) o;

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
                        entityObject.addProperty("type id", objective.getEntity().getType().getId());
                        entityObject.add("data", objective.getEntity().getType().getSerializer().serialize(objective.getEntity()));

                        object.add("entity", entityObject);
                        object.addProperty("meta", objective.getMeta());
                        object.addProperty("progress", objective.getProgress());

                        return object;
                    }

                    @Override
                    public @NotNull Objective deserialize(@NotNull JsonElement o) {
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

                        JsonObject entityObject = object.get("entity").getAsJsonObject();
                        codes.laivy.quests.api.provider.objectives.entities.mechanic.EntityType<? extends IEntity> entityType = laivyQuests().getApi().getEntityType(entityObject.get("type id").getAsString());
                        IEntity entity = entityType.getSerializer().deserialize(entityObject.get("data"));

                        int meta = object.get("meta").getAsInt();
                        int progress = object.get("progress").getAsInt();

                        return new EntityKillObjective(name, description, entity, meta, progress, reward);
                    }
                }
        );
    }

    @Override
    public @NotNull IMessage getName(@NotNull Objective objective) {
        if (objective instanceof EntityKillObjective) {
            EntityKillObjective o = (EntityKillObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: entity kill name", o.getEntity().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }

    @Override
    public @NotNull IMessage getDescription(@NotNull Objective objective) {
        if (objective instanceof EntityKillObjective) {
            EntityKillObjective o = (EntityKillObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: entity kill lore", o.getMeta(), o.getEntity().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }
}
