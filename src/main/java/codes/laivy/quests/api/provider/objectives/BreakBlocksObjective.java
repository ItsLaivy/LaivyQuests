package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.quests.Objective;
import codes.laivy.quests.quests.QuestsPlayerData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.stream.Collectors;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class BreakBlocksObjective implements Objective {

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
    private static final @NotNull String ID = "BREAK_BLOCKS_NATIVE";
    public static void load() {
        Bukkit.getPluginManager().registerEvents(EVENTS, laivyQuests());

        laivyQuests().getApi().getObjectiveSerializers().put(ID, new Serializer<Objective>() {
            @Override
            public @NotNull JsonElement serialize(@NotNull Objective o) {
                if (!(o instanceof BreakBlocksObjective)) {
                    throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + ID + "'");
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
        });
    }

    private final @NotNull Map<Material, @Range(from = 1, to = Integer.MAX_VALUE) Integer> meta;
    private final @NotNull Map<Material, @Range(from = 0, to = Integer.MAX_VALUE) Integer> current;

    public BreakBlocksObjective(
            @NotNull Map<Material, @Range(from = 1, to = Integer.MAX_VALUE) Integer> meta,
            @NotNull Map<Material, @Range(from = 0, to = Integer.MAX_VALUE) Integer> current
    ) {
        this.meta = meta;
        this.current = current;

        // Security checks
        for (Material material : getMeta().keySet()) {
            int m = meta.get(material);
            if (m < 1) {
                throw new IllegalStateException("This meta material '" + material + "' objective is '" + m + "', the number needs to be higher than zero");
            }
        }
    }

    public @NotNull Map<Material, @Range(from = 1, to = Integer.MAX_VALUE) Integer> getMeta() {
        return meta;
    }

    public @NotNull Map<Material, @Range(from = 0, to = Integer.MAX_VALUE) Integer> getCurrent() {
        return current;
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public boolean isCompleted() {
        boolean completed = true;

        for (Map.Entry<Material, Integer> targetEntry : getMeta().entrySet()) {
            Material material = targetEntry.getKey();
            int meta = targetEntry.getValue();

            if (getCurrent().getOrDefault(material, 0) < meta) {
                completed = false;
                break;
            }
        }

        return completed;
    }

    @Override
    public void complete() {

    }
}
