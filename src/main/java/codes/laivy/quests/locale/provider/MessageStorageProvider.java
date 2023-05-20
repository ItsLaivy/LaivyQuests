package codes.laivy.quests.locale.provider;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.locale.IMessageStorage;
import codes.laivy.quests.utils.ComponentUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MessageStorageProvider implements IMessageStorage {

    private final @NotNull String defaultLocale;
    private final @NotNull Map<String, Map<String, BaseComponent[]>> messages;

    private final @NotNull Map<String, Set<String>> arrays = new HashMap<>();

    public MessageStorageProvider(@NotNull String defaultLocale, @NotNull Map<String, Map<String, BaseComponent[]>> messages) {
        this.defaultLocale = defaultLocale;
        this.messages = messages;
    }

    @Override
    public @NotNull Map<String, Set<String>> getArrays() {
        return arrays;
    }

    public @NotNull String getDefaultLocale() {
        return defaultLocale;
    }

    public @NotNull Map<String, Map<String, BaseComponent[]>> getData() {
        return messages;
    }

    @Override
    public @NotNull BaseComponent[] get(@Nullable String locale, @NotNull String message, Object... replaces) {
        // Yes, most of this method has been copied from the LvMultiplesLanguages systems xD
        if (getData().containsKey(message)) {
            if (locale == null || !getData().get(message).containsKey(locale)) {
                locale = defaultLocale;
            }

            Set<BaseComponent> componentSet = new LinkedHashSet<>();
            BaseComponent[] components = getData().get(message).get(locale);

            int row = 0;
            for (final BaseComponent component : ComponentUtils.cloneComponent(components)) {
                for (BaseComponent recursive : ComponentUtils.getComponents(component)) {
                    if (replaces.length > row) {
                        Object replace = replaces[row];
                        BaseComponent index;

                        if (replace instanceof BaseComponent) {
                            index = (BaseComponent) replace;
                        } else if (replace instanceof BaseComponent[]) {
                            index = new TextComponent((BaseComponent[]) replace);
                        } else {
                            index = new TextComponent(ChatColor.translateAlternateColorCodes('&', String.valueOf(replace)));
                        }

                        if (recursive instanceof TextComponent) {
                            TextComponent text = (TextComponent) recursive;

                            if (text.getText().contains("%s")) {
                                text.setText(text.getText().replaceFirst("%s", ComponentUtils.getText(index)));
                                row++;
                            }
                        }
                    }
                }

                componentSet.add(component);
            }

            return componentSet.toArray(new BaseComponent[0]);
        }
        throw new NullPointerException("Couldn't find this message '" + message + "'");
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
        if (!getArrays().containsKey(message)) {
            throw new UnsupportedOperationException("This message '" + message + "' isn't an array.");
        }

        List<BaseComponent[]> components = new LinkedList<>();

        for (BaseComponent component : get(locale, message, replaces)) {
            components.add(new BaseComponent[] {
                    component.duplicate()
            });
        }

        return components;
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
    public @NotNull IMessage getMessage(@NotNull String id) {
        return new MessageProvider(id, messages.get(id));
    }

}
