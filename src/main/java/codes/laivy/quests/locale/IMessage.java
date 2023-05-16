package codes.laivy.quests.locale;

import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IMessage {

    @NotNull String getId();

    @NotNull BaseComponent[] getText(@NotNull String locale);
    @NotNull List<BaseComponent[]> getArray(@NotNull String locale);

}
