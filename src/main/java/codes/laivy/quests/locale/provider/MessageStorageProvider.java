package codes.laivy.quests.locale.provider;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.locale.IMessageStorage;
import codes.laivy.quests.utils.ComponentUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MessageStorageProvider implements IMessageStorage {

    private final @NotNull String defaultLocale;
    private final @NotNull Map<String, Map<String, BaseComponent[]>> messages;

    private final @NotNull Map<String, Set<String>> arrays = new HashMap<>();
    private final @NotNull Map<String, Set<String>> legacies = new HashMap<>();

    public MessageStorageProvider(@NotNull String defaultLocale, @NotNull Map<String, Map<String, BaseComponent[]>> messages) {
        this.defaultLocale = defaultLocale;
        this.messages = messages;
    }

    @Override
    public @NotNull Map<String, Set<String>> getArrays() {
        return arrays;
    }

    @Override
    public @NotNull Map<String, Set<String>> getLegacies() {
        return legacies;
    }

    public @NotNull String getDefaultLocale() {
        return defaultLocale;
    }

    public @NotNull Map<String, Map<String, BaseComponent[]>> getData() {
        return messages;
    }

    @Override
    public @NotNull BaseComponent[] get(@Nullable String locale, @NotNull String message, Object... replaces) {
        return getMessage(message).getText(locale, replaces);
    }

    @Override
    public @NotNull BaseComponent[] get(@NotNull UUID uuid, @NotNull String message, Object... replaces) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return get(ComponentUtils.getLocale(player), message, replaces);
        } else {
            throw new NullPointerException("This player '" + uuid + "' isn't on-line.");
        }
    }

    @Override
    public @NotNull List<BaseComponent[]> getArray(@Nullable String locale, @NotNull String message, Object... replaces) {
        return getMessage(message).getArray(locale, replaces);
    }

    @Override
    public @NotNull List<BaseComponent[]> getArray(@NotNull UUID uuid, @NotNull String message, Object... replaces) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return getArray(ComponentUtils.getLocale(player), message, replaces);
        } else {
            throw new NullPointerException("This player '" + uuid + "' isn't on-line.");
        }
    }

    @Override
    public @NotNull IMessage getMessage(@NotNull String id, @NotNull Object... replaces) {
        return new MessageProvider(id, messages.get(id), replaces);
    }

}
