package codes.laivy.quests.api.provider.objectives.blocks;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.QuestsPlayerData;
import codes.laivy.quests.quests.objectives.reward.Reward;
import codes.laivy.quests.quests.objectives.reward.RewardType;
import codes.laivy.quests.utils.MaterialUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public final class BreakBlockObjectiveType extends ObjectiveType {

    public static final class Events implements Listener {
        @EventHandler
        private void breakBlock(@NotNull BlockBreakEvent e) {
            QuestsPlayerData data = laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId());

            Set<Objective> objectives = new HashSet<>();
            data.getQuests().forEach(q -> objectives.addAll(q.getObjectives(true).stream().filter(o -> o instanceof BreakBlockObjective).collect(Collectors.toSet())));

            for (Quest quest : data.getQuests()) {
                for (Objective objective : quest.getObjectives(true)) {
                    if (!objective.isCompleted() && objective instanceof BreakBlockObjective) {
                        final BreakBlockObjective holder = (BreakBlockObjective) objective;
                        Material material = e.getBlock().getType();

                        if (holder.getMaterial() == material) {
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

    public static final @NotNull String BREAK_BLOCKS_OBJECTIVE_TYPE_ID = "BLOCK_BREAK";

    @ApiStatus.Internal
    public BreakBlockObjectiveType() {
        super(
                BREAK_BLOCKS_OBJECTIVE_TYPE_ID,
                new Serializer<Objective>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull Objective o) {
                        if (!(o instanceof BreakBlockObjective)) {
                            throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + BREAK_BLOCKS_OBJECTIVE_TYPE_ID + "'");
                        }
                        BreakBlockObjective objective = (BreakBlockObjective) o;

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

                        object.addProperty("material", objective.getMaterial().name());
                        object.addProperty("meta", objective.getMeta());
                        object.addProperty("progress", objective.getProgress());

                        return object;
                    }

                    @Override
                    public @NotNull Objective deserialize(@NotNull JsonElement objective) {
                        JsonObject object = objective.getAsJsonObject();

                        IMessage name = laivyQuests().getMessageStorage().getMessage(object.get("name").getAsString());
                        IMessage description = laivyQuests().getMessageStorage().getMessage(object.get("description").getAsString());

                        @Nullable Reward reward = null;
                        if (object.has("reward")) {
                            JsonObject rewardObject = object.getAsJsonObject("reward");
                            String typeId = rewardObject.get("type id").getAsString();

                            RewardType<? extends Reward> type = laivyQuests().getApi().getRewardType(typeId);
                            reward = type.getSerializer().deserialize(rewardObject.get("data"));
                        }

                        Material material = Material.valueOf(object.get("material").getAsString());
                        int meta = object.get("meta").getAsInt();
                        int progress = object.get("progress").getAsInt();

                        return new BreakBlockObjective(name, description, material, meta, progress, reward);
                    }
                }
        );
    }

    @Override
    public @NotNull IMessage getName(@NotNull Objective objective) {
        if (objective instanceof BreakBlockObjective) {
            BreakBlockObjective o = (BreakBlockObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: block break name", MaterialUtils.convertToBeautifulName(o.getMaterial()));
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }

    @Override
    public @NotNull IMessage getDescription(@NotNull Objective objective) {
        if (objective instanceof BreakBlockObjective) {
            BreakBlockObjective o = (BreakBlockObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: block break lore", MaterialUtils.convertToBeautifulName(o.getMaterial()));
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }
}
