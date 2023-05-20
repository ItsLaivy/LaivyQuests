package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.quests.Objective;
import codes.laivy.quests.quests.ObjectiveType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.BreakBlocksObjectiveType.BREAK_BLOCKS_OBJECTIVE_TYPE_ID;

public class BreakBlocksObjective implements Objective {

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
            int m = getMeta().get(material);

            if (!material.isBlock()) {
                throw new IllegalArgumentException("This material '" + material + "' isn't a block!");
            }
            if (m < 1) {
                throw new IllegalStateException("This meta material '" + material + "' objective is '" + m + "', the number needs to be higher than zero");
            }
        }

        for (Material material : getCurrent().keySet()) {
            int m = getCurrent().get(material);

            if (!material.isBlock()) {
                throw new IllegalArgumentException("This material '" + material + "' isn't a block!");
            }
            if (m < 0) {
                throw new IllegalStateException("This current material '" + material + "' objective is '" + m + "', the number needs to be higher than or equal to zero");
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
    public @NotNull BreakBlocksObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(BREAK_BLOCKS_OBJECTIVE_TYPE_ID);

        if (type instanceof BreakBlocksObjectiveType) {
            return (BreakBlocksObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + BREAK_BLOCKS_OBJECTIVE_TYPE_ID + "' isn't a instance of the break blocks objective type class. (" + type.getClass().getName() + ")");
        }
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
