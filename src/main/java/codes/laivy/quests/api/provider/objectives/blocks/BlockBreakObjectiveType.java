package codes.laivy.quests.api.provider.objectives.blocks;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.BlockType;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.IBlock;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.QuestsPlayerData;
import codes.laivy.quests.quests.objectives.reward.Reward;
import codes.laivy.quests.quests.objectives.reward.RewardType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public final class BlockBreakObjectiveType extends ObjectiveType {

    public static final class Events implements Listener {
        @EventHandler
        private void breakBlock(@NotNull BlockBreakEvent e) {
            QuestsPlayerData data = laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId());

            for (Quest quest : data.getQuests()) {
                for (Objective objective : quest.getObjectives(true)) {
                    if (!objective.isCompleted() && objective instanceof BlockBreakObjective) {
                        final BlockBreakObjective holder = (BlockBreakObjective) objective;

                        if (holder.getBlock().equals(e.getBlock())) {
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

    public static final @NotNull String BLOCK_BREAK_OBJECTIVE_TYPE_ID = "BLOCK_BREAK";

    @ApiStatus.Internal
    public BlockBreakObjectiveType() {
        super(
                BLOCK_BREAK_OBJECTIVE_TYPE_ID,
                new Serializer<Objective>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull Objective o) {
                        if (!(o instanceof BlockBreakObjective)) {
                            throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + BLOCK_BREAK_OBJECTIVE_TYPE_ID + "'");
                        }
                        BlockBreakObjective objective = (BlockBreakObjective) o;

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
                        block.addProperty("type id", objective.getBlock().getType().getId());
                        block.add("data", objective.getBlock().getType().getSerializer().serialize(objective.getBlock()));

                        object.add("block", block);
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

                        JsonObject blockObject = object.getAsJsonObject("block");
                        BlockType<? extends IBlock> blockType = laivyQuests().getApi().getBlockType(blockObject.get("type id").getAsString());
                        IBlock block = blockType.getSerializer().deserialize(blockObject.get("data"));

                        int meta = object.get("meta").getAsInt();
                        int progress = object.get("progress").getAsInt();

                        return new BlockBreakObjective(name, description, block, meta, progress, reward);
                    }
                }
        );
    }

    @Override
    public @NotNull IMessage getName(@NotNull Objective objective) {
        if (objective instanceof BlockBreakObjective) {
            BlockBreakObjective o = (BlockBreakObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: block break name", o.getBlock().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }

    @Override
    public @NotNull IMessage getDescription(@NotNull Objective objective) {
        if (objective instanceof BlockBreakObjective) {
            BlockBreakObjective o = (BlockBreakObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: block break lore", o.getBlock().getName());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }
}
