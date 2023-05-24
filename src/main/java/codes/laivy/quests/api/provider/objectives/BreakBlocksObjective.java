package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.locale.provider.MessageProvider;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.BreakBlocksObjectiveType.BREAK_BLOCKS_OBJECTIVE_TYPE_ID;

public class BreakBlocksObjective implements Objective {

    private final @NotNull Material material;
    private final @Range(from = 1, to = Integer.MAX_VALUE) int meta;
    private @Range(from = 0, to = Integer.MAX_VALUE) int current;

    public BreakBlocksObjective(
            @NotNull Material material,
            @Range(from = 1, to = Integer.MAX_VALUE) int meta,
            @Range(from = 0, to = Integer.MAX_VALUE) int current
    ) {
        this.material = material;

        this.meta = meta;
        this.current = current;

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

    public @Range(from = 0, to = Integer.MAX_VALUE) int getCurrent() {
        return current;
    }
    public void setCurrent(@Range(from = 0, to = Integer.MAX_VALUE) int current) {
        this.current = current;
    }

    @Override
    public @NotNull BreakBlocksObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(BREAK_BLOCKS_OBJECTIVE_TYPE_ID);

        if (type instanceof BreakBlocksObjectiveType) {
            return (BreakBlocksObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + BREAK_BLOCKS_OBJECTIVE_TYPE_ID + "' isn't a instance of the break blocks objective type class. (" + BreakBlocksObjectiveType.class.getName() + ")");
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
        return getCurrent() >= getMeta();
    }

    @Override
    public void complete() {

    }
}
