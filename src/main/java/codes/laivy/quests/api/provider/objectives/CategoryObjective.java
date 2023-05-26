package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.api.provider.quest.ObjectiveProvider;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.CategoryObjectiveType.CATEGORY_OBJECTIVE_TYPE_ID;

/**
 * This class encapsulates additional objectives to create a kind of category.
 * It is considered complete only when all its additional objectives are also completed.
 */
// TODO: 24/05/2023 Trava de objetivos. Exemplo: Você só poder completar o segundo objetivo se o primeiro estiver concluído. 
public class CategoryObjective extends ObjectiveProvider {

    private final @NotNull List<Objective> extras = new LinkedList<>();

    public CategoryObjective(
            @NotNull IMessage name,
            @NotNull IMessage description,

            @NotNull Objective... extras
    ) {
        super(name, description);

        this.extras.addAll(Arrays.asList(extras));

        if (this.extras.isEmpty()) {
            throw new IllegalArgumentException("The extras list of the category objective is empty.");
        }
    }

    public final @NotNull List<Objective> getExtras() {
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
    public boolean isCompleted() {
        for (Objective objective : getExtras()) {
            if (!objective.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void abstractComplete() {

    }
}
