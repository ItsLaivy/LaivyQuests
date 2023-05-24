package codes.laivy.quests.locale;

import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IMessage {

    @NotNull String getId();

    @NotNull BaseComponent[] getText(@NotNull String locale, @NotNull Object... replaces);
    @NotNull List<BaseComponent[]> getArray(@NotNull String locale, @NotNull Object... replaces);

}
