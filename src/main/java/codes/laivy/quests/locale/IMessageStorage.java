package codes.laivy.quests.locale;

import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface IMessageStorage {

    @NotNull String getDefaultLocale();

    @NotNull Map<String, Map<String, BaseComponent[]>> getData();

    @NotNull BaseComponent[] get(@Nullable String locale, @NotNull String message, Object... replaces);
    @NotNull BaseComponent[] get(@NotNull UUID uuid, @NotNull String message, Object... replaces);

    @NotNull List<BaseComponent[]> getArray(@Nullable String locale, @NotNull String message, Object... replaces);
    @NotNull List<BaseComponent[]> getArray(@NotNull UUID uuid, @NotNull String message, Object... replaces);

    @NotNull IMessage getMessage(@NotNull String id, @NotNull Object... replaces);
    default @NotNull Collection<IMessage> getMessages() {
        List<IMessage> messages = new LinkedList<>();

        for (String id : getData().keySet()) {
            messages.add(getMessage(id));
        }

        return messages;
    }

    @NotNull Map<String, Set<String>> getArrays();

}
