package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.locale.provider.MessageProvider;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.objectives.Progressable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.BreakBlockObjectiveType.BREAK_BLOCKS_OBJECTIVE_TYPE_ID;

public class BreakBlockObjective implements Objective, Progressable<Integer> {

    private final @NotNull Material material;
    private final @Range(from = 1, to = Integer.MAX_VALUE) int meta;
    private @Range(from = 0, to = Integer.MAX_VALUE) int progress;

    public BreakBlockObjective(
            @NotNull Material material,
            @Range(from = 1, to = Integer.MAX_VALUE) int meta,
            @Range(from = 0, to = Integer.MAX_VALUE) int progress
    ) {
        this.material = material;

        this.meta = meta;
        this.progress = progress;

        // Security checks
        if (!material.isBlock()) {
            throw new IllegalArgumentException("This material '" + material + "' isn't a block!");
        }
    }

    public final @NotNull Material getMaterial() {
        return material;
    }

    public @Range(from = 1, to = Integer.MAX_VALUE) int getMeta() {
        return meta;
    }

    @Override
    public @NotNull BreakBlockObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(BREAK_BLOCKS_OBJECTIVE_TYPE_ID);

        if (type instanceof BreakBlockObjectiveType) {
            return (BreakBlockObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + BREAK_BLOCKS_OBJECTIVE_TYPE_ID + "' isn't a instance of the break blocks objective type class. (" + BreakBlockObjectiveType.class.getName() + ")");
        }
    }

    @Override
    public @NotNull IMessage getName() {
        return new MessageProvider("t", new LinkedHashMap<String, BaseComponent[]>() {{
            put("en_us", TextComponent.fromLegacyText("Minerador maniaco"));
        }});
    }

    @Override
    public @NotNull IMessage getDescription() {
        return new MessageProvider("t", new LinkedHashMap<String, BaseComponent[]>() {{
            put("en_us", TextComponent.fromLegacyText("Minerador maniaco"));
        }});
    }

    @Override
    public boolean isCompleted() {
        return getProgress() >= getMeta();
    }

    @Override
    public void complete() {

    }

    @Override
    public @NotNull @Range(from = 0, to = Integer.MAX_VALUE) Integer getProgress() {
        return progress;
    }

    @Override
    public void setProgress(@NotNull @Range(from = 0, to = Integer.MAX_VALUE) Integer progress) {
        this.progress = progress;
    }

    @Override
    public @NotNull IMessage getProgressMessage(@NotNull Objective objective) {
        if (objective instanceof BreakBlockObjective) {
            BreakBlockObjective o = (BreakBlockObjective) objective;
            return laivyQuests().getMessageStorage().getMessage("Objectives: progress message", o.getProgress(), o.getMeta());
        }
        throw new IllegalArgumentException("This objective '" + objective + "' isn't valid");
    }
}
