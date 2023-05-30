package codes.laivy.quests.api.provider.objectives.items.mechanic.provider;

import codes.laivy.quests.api.provider.objectives.items.mechanic.Item;
import codes.laivy.quests.api.provider.objectives.items.mechanic.ItemType;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.utils.MaterialUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.items.mechanic.provider.ItemTypeProvider.ITEM_PROVIDER_TYPE;

public class ItemProvider implements Item {

    private final @NotNull ItemStack itemStack;

    public ItemProvider(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;

        if (!itemStack.getType().isEdible()) {
            throw new IllegalArgumentException("This material '" + itemStack.getType() + "' isn't edible.");
        }
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public @NotNull <T extends Item> ItemType<T> getType() {
        //noinspection unchecked
        return (ItemType<T>) laivyQuests().getApi().getItemType(ITEM_PROVIDER_TYPE);
    }

    @Override
    public @NotNull IMessage getName() {
        return IMessage.from("EN_US", MaterialUtils.convertToBeautifulName(getItemStack().getType()));
    }

    @Override
    public boolean isSimilar(@NotNull ItemStack itemStack) {
        return getItemStack().isSimilar(itemStack);
    }

    @Override
    public boolean equals(@NotNull ItemStack itemStack) {
        return getItemStack().equals(itemStack);
    }
}
