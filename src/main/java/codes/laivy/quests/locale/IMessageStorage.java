package codes.laivy.quests.locale;

import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IMessageStorage {

    @NotNull String getDefaultLocale();

    @NotNull Map<String, Map<String, BaseComponent[]>> getMessages();

    @NotNull BaseComponent[] get(@Nullable String locale, @NotNull String message, Object... replaces);
    @NotNull BaseComponent[] get(@NotNull UUID uuid, @NotNull String message, Object... replaces);

    @NotNull List<BaseComponent[]> getArray(@Nullable String locale, @NotNull String message, Object... replaces);
    @NotNull List<BaseComponent[]> getArray(@NotNull UUID uuid, @NotNull String message, Object... replaces);

    @NotNull Map<String, Set<String>> getArrays();

}
