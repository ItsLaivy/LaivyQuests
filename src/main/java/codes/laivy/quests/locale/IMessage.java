package codes.laivy.quests.locale;

import codes.laivy.quests.locale.provider.MessageProvider;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IMessage {

    @NotNull String getId();

    @NotNull BaseComponent[] getText(@NotNull String locale, @NotNull Object... replaces);
    @NotNull List<BaseComponent[]> getArray(@NotNull String locale, @NotNull Object... replaces);

    @NotNull Map<String, BaseComponent[]> getData();

    static @NotNull IMessage from(@NotNull String locale, @NotNull BaseComponent... message) {
        return new MessageProvider("", new HashMap<String, BaseComponent[]>() {{
            put(locale, message);
        }});
    }
    static @NotNull IMessage from(@NotNull String locale, @NotNull String message) {
        return new MessageProvider("", new HashMap<String, BaseComponent[]>() {{
            put(locale, new BaseComponent[] {
                    new TextComponent(message)
            });
        }});
    }

}
