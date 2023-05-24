package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.QuestsPlayerData;
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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public final class BreakBlocksObjectiveType extends ObjectiveType {

    public static final class Events implements Listener {
        @EventHandler
        private void breakBlock(@NotNull BlockBreakEvent e) {
            QuestsPlayerData data = laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId());

            Set<Objective> objectives = new HashSet<>();
            data.getQuests().forEach(q -> objectives.addAll(q.getObjectives().stream().filter(o -> o instanceof BreakBlocksObjective).collect(Collectors.toSet())));

            for (Objective h : objectives) {
                if (h instanceof BreakBlocksObjective) {
                    final BreakBlocksObjective holder = (BreakBlocksObjective) h;
                    Material material = e.getBlock().getType();

                    if (holder.getMaterial() == material) {
                        int current = holder.getCurrent();
                        holder.setCurrent(current + 1);
                    }
                }
            }
        }
    }

    private static final @NotNull Events EVENTS = new Events();

    static {
        Bukkit.getPluginManager().registerEvents(EVENTS, laivyQuests());
    }

    public static final @NotNull String BREAK_BLOCKS_OBJECTIVE_TYPE_ID = "Block break natives";

    @ApiStatus.Internal
    public BreakBlocksObjectiveType() {
        super(
                BREAK_BLOCKS_OBJECTIVE_TYPE_ID,
                new Serializer<Objective>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull Objective o) {
                        if (!(o instanceof BreakBlocksObjective)) {
                            throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + BREAK_BLOCKS_OBJECTIVE_TYPE_ID + "'");
                        }
                        BreakBlocksObjective objective = (BreakBlocksObjective) o;

                        JsonObject object = new JsonObject();

                        object.addProperty("material", objective.getMaterial().name());
                        object.addProperty("meta", objective.getMeta());
                        object.addProperty("current", objective.getCurrent());

                        return object;
                    }

                    @Override
                    public @NotNull Objective deserialize(@NotNull JsonElement objective) {
                        JsonObject object = objective.getAsJsonObject();

                        Material material = Material.valueOf(object.get("material").getAsString());
                        int meta = object.get("meta").getAsInt();
                        int current = object.get("current").getAsInt();

                        return new BreakBlocksObjective(material, meta, current);
                    }
                }
        );
    }

    @Override
    public @NotNull IMessage getName(@NotNull Objective objective) {
        if (objective instanceof BreakBlocksObjective) {
            BreakBlocksObjective o = (BreakBlocksObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: block break name", MaterialUtils.convertToBeautifulName(o.getMaterial()));
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }

    @Override
    public @NotNull IMessage getDescription(@NotNull Objective objective) {
        if (objective instanceof BreakBlocksObjective) {
            BreakBlocksObjective o = (BreakBlocksObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objective types: block break lore", MaterialUtils.convertToBeautifulName(o.getMaterial()));
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }
}
