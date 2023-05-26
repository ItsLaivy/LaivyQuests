package codes.laivy.quests.api.provider.objectives.entities.mechanic;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.BlockType;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.IBlock;
import codes.laivy.quests.locale.IMessage;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface IEntity {

    /**
     * This is used to serialize or deserialize this block using the api
     * @return the block type
     * @see QuestsApi#getBlockTypes()
     */
    <T extends IEntity> @NotNull EntityType<T> getType();

    /**
     * The name of the entities at the menus
     * @return the entity name
     */
    @NotNull IMessage getName();

    boolean equals(@NotNull Entity entity);

}
