package codes.laivy.quests.api.provider.objectives.blocks.mechanic.material;

import codes.laivy.quests.api.provider.objectives.blocks.mechanic.BlockType;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.IBlock;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.utils.MaterialUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.blocks.mechanic.material.MaterialBlockType.MATERIAL_BLOCK_TYPE;

public class MaterialBlock implements IBlock {

    private final @NotNull Material material;

    public MaterialBlock(@NotNull Material material) {
        this.material = material;
    }

    @Override
    public @NotNull Material getMaterial() {
        return material;
    }

    @Override
    public @NotNull IMessage getName() {
        return IMessage.from("EN_US", MaterialUtils.convertToBeautifulName(getMaterial()));
    }

    @Override
    public <T extends IBlock> @NotNull BlockType<T> getType() {
        //noinspection unchecked
        return (BlockType<T>) laivyQuests().getApi().getBlockType(MATERIAL_BLOCK_TYPE);
    }

    @Override
    public boolean equals(@NotNull Block block) {
        return block.getType().equals(getMaterial());
    }
}
