package codes.laivy.quests.locale;

import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IMessageStorage {

    @NotNull String getDefaultLocale();

    @NotNull Map<String, Map<String, BaseComponent[]>> getMessages();

    @NotNull BaseComponent[] get(@Nullable String locale, @NotNull String message, Object... replaces);

}
