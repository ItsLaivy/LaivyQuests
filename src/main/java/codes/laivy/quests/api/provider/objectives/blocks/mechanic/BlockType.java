package codes.laivy.quests.api.provider.objectives.blocks.mechanic;

import codes.laivy.quests.api.Serializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockType)) return false;
        BlockType<?> blockType = (BlockType<?>) o;
        return Objects.equals(getId(), blockType.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
