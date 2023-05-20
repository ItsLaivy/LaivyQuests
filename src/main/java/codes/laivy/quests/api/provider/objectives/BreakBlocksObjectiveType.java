package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.quests.Objective;
import codes.laivy.quests.quests.ObjectiveType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public final class BreakBlocksObjectiveType extends ObjectiveType {

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
