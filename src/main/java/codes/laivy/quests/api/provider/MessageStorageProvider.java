package codes.laivy.quests.api.provider;

import codes.laivy.quests.locale.IMessageStorage;
import codes.laivy.quests.utils.ComponentUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MessageStorageProvider implements IMessageStorage {

    private final @NotNull String defaultLocale;
    private final @NotNull Map<String, Map<String, BaseComponent[]>> messages;

    public MessageStorageProvider(@NotNull String defaultLocale, @NotNull Map<String, Map<String, BaseComponent[]>> messages) {
        this.defaultLocale = defaultLocale;
        this.messages = messages;
    }

    public @NotNull String getDefaultLocale() {
        return defaultLocale;
    }

    public @NotNull Map<String, Map<String, BaseComponent[]>> getMessages() {
        return messages;
    }

    @Override
    public @NotNull BaseComponent[] get(@Nullable String locale, @NotNull String message, Object... replaces) {
        // Yes, most of this method has been copied from the LvMultiplesLanguages systems xD
        if (getMessages().containsKey(message)) {
            if (locale == null || !getMessages().get(message).containsKey(locale)) {
                locale = defaultLocale;
            }

            Set<BaseComponent> componentSet = new LinkedHashSet<>();
            BaseComponent[] components = getMessages().get(message).get(locale);

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

}
