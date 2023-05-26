package codes.laivy.quests.api.provider.objectives.blocks.mechanic;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.locale.IMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public interface IBlock {

    @NotNull Material getMaterial();

    /**
     * The name of the block at the menus
     * @return the block name
     */
    @NotNull IMessage getName();

    /**
     * This is used to serialize or deserialize this block using the api
     * @return the block type
     * @see QuestsApi#getBlockTypes()
     */
    <T extends IBlock> @NotNull BlockType<T> getType();

    boolean equals(@NotNull Block block);

}
