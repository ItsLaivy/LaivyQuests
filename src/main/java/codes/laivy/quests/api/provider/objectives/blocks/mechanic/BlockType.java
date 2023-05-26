package codes.laivy.quests.api.provider.objectives.blocks.mechanic;

import codes.laivy.quests.api.Serializer;
import org.jetbrains.annotations.NotNull;

public class BlockType<T extends IBlock> {

    private final @NotNull String id;
    private final @NotNull Serializer<T> serializer;

    public BlockType(@NotNull String id, @NotNull Serializer<T> serializer) {
        this.id = id;
        this.serializer = serializer;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull Serializer<T> getSerializer() {
        return serializer;
    }

}
