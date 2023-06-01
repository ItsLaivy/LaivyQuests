package codes.laivy.quests.compatibility.laivynpc;

import codes.laivy.npc.developers.events.NPCClickEvent;
import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.compatibility.laivynpc.npc.LaivyNpcProvider;
import codes.laivy.quests.compatibility.laivynpc.npc.LaivyNpcTypeProvider;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class LaivyNpcInteractObjectiveType extends ObjectiveType {

    public static final class Events implements Listener {
        @EventHandler
        private void entityKill(@NotNull NPCClickEvent e) {
            QuestsPlayerData data = laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId());

            for (Quest quest : data.getQuests()) {
                for (Objective objective : quest.getObjectives(true)) {
                    if (!objective.isCompleted() && objective instanceof LaivyNpcInteractObjective) {
                        final LaivyNpcInteractObjective holder = (LaivyNpcInteractObjective) objective;
                        holder.setCompleted(true);
                        objective.complete(quest);
                    }
                }
            }
        }
    }

    private static final @NotNull Events EVENTS = new Events();

    static {
        Bukkit.getPluginManager().registerEvents(EVENTS, laivyQuests());
    }

    public static final @NotNull String LAIVYNPC_INTERACT_OBJECTIVE_TYPE_ID = "LAIVYNPC_INTERACT";

    public LaivyNpcInteractObjectiveType() {
        super(
                LAIVYNPC_INTERACT_OBJECTIVE_TYPE_ID,
                new Serializer<Objective>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull Objective o) {
                        if (!(o instanceof LaivyNpcInteractObjective)) {
                            throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + LAIVYNPC_INTERACT_OBJECTIVE_TYPE_ID + "'");
                        }
                        LaivyNpcInteractObjective objective = (LaivyNpcInteractObjective) o;

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

                        object.addProperty("completed", objective.isCompleted());

                        JsonObject entityObject = new JsonObject();
                        entityObject.addProperty("type id", objective.getNPC().getType().getId());
                        entityObject.add("data", objective.getNPC().getType().getSerializer().serialize(objective.getNPC()));

                        object.add("npc", entityObject);

                        return object;
                    }

                    @Override
                    public @NotNull LaivyNpcInteractObjective deserialize(@NotNull JsonElement o) {
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

                        boolean completed = object.get("completed").getAsBoolean();

                        JsonObject entityObject = object.get("npc").getAsJsonObject();
                        LaivyNpcTypeProvider entityType = (LaivyNpcTypeProvider) laivyQuests().getApi().getEntityType(entityObject.get("type id").getAsString());
                        LaivyNpcProvider entity = entityType.getSerializer().deserialize(entityObject.get("data"));

                        return new LaivyNpcInteractObjective(name, description, completed, entity, reward);
                    }
                }
        );
    }

    @Override
    public @NotNull IMessage getName(@NotNull Objective objective) {
        if (objective instanceof LaivyNpcInteractObjective) {
            LaivyNpcInteractObjective o = (LaivyNpcInteractObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: npc interact name", o.getNPC().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }

    @Override
    public @NotNull IMessage getDescription(@NotNull Objective objective) {
        if (objective instanceof LaivyNpcInteractObjective) {
            LaivyNpcInteractObjective o = (LaivyNpcInteractObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: npc interact lore", o.getNPC().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }

}
