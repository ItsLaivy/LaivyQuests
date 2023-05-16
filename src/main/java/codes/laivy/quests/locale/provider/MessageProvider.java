package codes.laivy.quests.locale.provider;

import codes.laivy.quests.locale.IMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MessageProvider implements IMessage {

    private final @NotNull String id;
    private final @NotNull Map<String, BaseComponent[]> data;

    public MessageProvider(@NotNull String id, @NotNull Map<String, BaseComponent[]> data) {
        this.id = id;
        this.data = data;
    }

    public @NotNull Map<String, BaseComponent[]> getData() {
        return data;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull BaseComponent[] getText(@NotNull String locale) {
        if (!getData().containsKey(locale)) {
            locale = new LinkedList<>(getData().keySet()).getFirst();
        }

        if (!getData().containsKey(locale)) {
            throw new NullPointerException("(1) | Message without data '" + locale + "'");
        }

        return getData().get(locale);
    }

    @Override
    public @NotNull List<BaseComponent[]> getArray(@NotNull String locale) {
        if (!getData().containsKey(locale)) {
            locale = getData().keySet().stream().findFirst().orElseThrow(() -> new NullPointerException("Message without data '" + getId() + "'"));
        }

        List<BaseComponent[]> components = new LinkedList<>();

        for (BaseComponent component : getText(locale)) {
            components.add(new BaseComponent[] {
                    component.duplicate()
            });
        }

        return components;
    }
}
