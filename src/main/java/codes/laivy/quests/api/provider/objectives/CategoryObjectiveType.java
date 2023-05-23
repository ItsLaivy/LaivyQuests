package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class CategoryObjectiveType extends ObjectiveType {

    public static final @NotNull String CATEGORY_OBJECTIVE_TYPE_ID = "Category";

    @ApiStatus.Internal
    public CategoryObjectiveType() {
        super(
                CATEGORY_OBJECTIVE_TYPE_ID,
                laivyQuests().getMessageStorage().getMessage("Objective types: category name"),
                laivyQuests().getMessageStorage().getMessage("Objective types: category lore"),
                new Serializer<Objective>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull Objective o) {
                        if (!(o instanceof CategoryObjective)) {
                            throw new UnsupportedOperationException("This objective '" + o.getClass().getName() + "' isn't compatible with the objective id '" + CATEGORY_OBJECTIVE_TYPE_ID + "'");
                        }
                        CategoryObjective objective = (CategoryObjective) o;

                        JsonArray objectives = new JsonArray();

                        for (Objective extra : objective.getExtras()) {
                            JsonObject extraObject = new JsonObject();

                            extraObject.addProperty("type id", extra.getType().getId());
                            extraObject.add("data", extra.getType().getSerializer().serialize(extra));

                            objectives.add(extraObject);
                        }

                        return objectives;
                    }

                    @Override
                    public @NotNull CategoryObjective deserialize(@NotNull JsonElement object) {
                        List<Objective> objectiveList = new LinkedList<>();
                        JsonArray objectives = object.getAsJsonArray();

                        for (JsonElement element : objectives) {
                            JsonObject objectiveObject = element.getAsJsonObject();

                            String objectiveId = objectiveObject.get("type id").getAsString();
                            JsonElement data = objectiveObject.get("data");
                            ObjectiveType type = laivyQuests().getApi().getObjectiveType(objectiveId);

                            Objective objective = type.getSerializer().deserialize(data);
                            objectiveList.add(objective);
                        }

                        return new CategoryObjective(objectiveList.toArray(new Objective[0]));
                    }
                }
        );
    }
}
