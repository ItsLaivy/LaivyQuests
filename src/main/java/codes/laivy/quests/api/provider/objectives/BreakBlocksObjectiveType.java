package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.quests.Objective;
import codes.laivy.quests.quests.ObjectiveType;
import codes.laivy.quests.quests.QuestsPlayerData;
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
import java.util.LinkedHashMap;
import java.util.Map;
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

                    if (holder.getMeta().containsKey(material)) {
                        int current = holder.getCurrent().getOrDefault(material, 0);
                        holder.getCurrent().put(material, current + 1);
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
                laivyQuests().getMessageStorage().getMessage("Objective types: block break name"),
                laivyQuests().getMessageStorage().getMessage("Objective types: block break lore"),
                new Serializer<Objective>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull Objective o) {
                        if (!(o instanceof BreakBlocksObjective)) {
                            throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + BREAK_BLOCKS_OBJECTIVE_TYPE_ID + "'");
                        }
                        BreakBlocksObjective objective = (BreakBlocksObjective) o;

                        JsonObject object = new JsonObject();
                        JsonObject meta = new JsonObject();
                        JsonObject current = new JsonObject();

                        for (Material material : objective.getMeta().keySet()) {
                            int broken = objective.getMeta().get(material);
                            meta.addProperty(material.name(), broken);
                        }
                        for (Material material : objective.getCurrent().keySet()) {
                            int broken = objective.getCurrent().get(material);
                            current.addProperty(material.name(), broken);
                        }

                        object.add("meta", meta);
                        object.add("current", current);

                        return object;
                    }

                    @Override
                    public @NotNull Objective deserialize(@NotNull JsonElement objective) {
                        JsonObject object = objective.getAsJsonObject();

                        @NotNull JsonObject metaObj = object.getAsJsonObject("meta");
                        @NotNull JsonObject currentObj = object.getAsJsonObject("current");

                        Map<Material, Integer> meta = new LinkedHashMap<>();
                        Map<Material, Integer> current = new LinkedHashMap<>();

                        for (Map.Entry<String, JsonElement> entry : metaObj.entrySet()) {
                            meta.put(Material.valueOf(entry.getKey().toUpperCase()), entry.getValue().getAsInt());
                        }
                        for (Map.Entry<String, JsonElement> entry : currentObj.entrySet()) {
                            current.put(Material.valueOf(entry.getKey().toUpperCase()), entry.getValue().getAsInt());
                        }

                        return new BreakBlocksObjective(meta, current);
                    }
                }
        );
    }
}
