package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.locale.provider.MessageProvider;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.CategoryObjectiveType.CATEGORY_OBJECTIVE_TYPE_ID;

/**
 * This class encapsulates additional objectives to create a kind of category.
 * It is considered complete only when all its additional objectives are also completed.
 */
public class CategoryObjective implements Objective {

    private final @NotNull List<Objective> extras = new LinkedList<>();

    public CategoryObjective(@NotNull Objective... extras) {
        this.extras.addAll(Arrays.asList(extras));
    }

    public @NotNull List<Objective> getExtras() {
        return extras;
    }

    @Override
    public @NotNull CategoryObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(CATEGORY_OBJECTIVE_TYPE_ID);

        if (type instanceof CategoryObjectiveType) {
            return (CategoryObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + CATEGORY_OBJECTIVE_TYPE_ID + "' isn't a instance of the category objective type class. (" + CategoryObjectiveType.class.getName() + ")");
        }
    }

    @Override
    public @NotNull IMessage getName() {
        return new MessageProvider("t", new LinkedHashMap<String, BaseComponent[]>() {{
            put("en_us", TextComponent.fromLegacyText("Minere"));
        }});
    }

    @Override
    public @NotNull IMessage getDescription() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void complete() {

    }
}
