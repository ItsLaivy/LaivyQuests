package codes.laivy.quests.locale.provider;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.utils.ComponentUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageProvider implements IMessage {

    private final @NotNull String id;
    private final @NotNull Map<String, BaseComponent[]> data;
    private final @NotNull Object[] replaces;

    public MessageProvider(@NotNull String id, @NotNull Map<String, BaseComponent[]> data, @NotNull Object... replaces) {
        this.id = id;
        this.data = data;
        this.replaces = replaces;
    }

    @Override
    public @NotNull Map<String, BaseComponent[]> getData() {
        return data;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    public @NotNull Object[] getReplaces() {
        return replaces;
    }

    @Override
    public @NotNull BaseComponent[] getText(@Nullable String locale, @NotNull Object... replaces) {
        if (locale == null || !getData().containsKey(locale)) {
            locale = getData().keySet().stream().findFirst().orElseThrow(() -> new NullPointerException("Message without data '" + getId() + "'"));
        }

        List<Object> replacesList = new LinkedList<>();
        replacesList.addAll(Arrays.asList(getReplaces()));
        replacesList.addAll(Arrays.asList(replaces));

        return replace(
                locale,
                getData().get(locale),
                replacesList.toArray(new Object[0])
        ).clone();
    }

    @Override
    public @NotNull List<BaseComponent[]> getArray(@Nullable String locale, @NotNull Object... replaces) {
        if (locale == null || !getData().containsKey(locale)) {
            locale = getData().keySet().stream().findFirst().orElseThrow(() -> new NullPointerException("Message without data '" + getId() + "'"));
        }

        List<BaseComponent[]> components = new LinkedList<>();

        for (BaseComponent component : getText(locale, replaces)) {
            for (BaseComponent recurring : ComponentUtils.getComponents(component)) {
                if (recurring instanceof TextComponent) {
                    TextComponent text = (TextComponent) recurring;

                    if (text.getText().contains("\n")) {
                        while (text.getText().contains("\n")) {
                            String[] split = text.getText().split("\n", 2);

                            TextComponent t1 = (TextComponent) text.duplicate();
                            TextComponent t2 = (TextComponent) text.duplicate();

                            t1.setText(split[0]);
                            t2.setText(split[1]);

                            components.add(new BaseComponent[] { t1 });

                            text = t2;
                        }

                        recurring = text;
                    }
                }

                components.add(new BaseComponent[] {
                        recurring
                });
            }
        }

        return components;
    }

    private @NotNull BaseComponent[] replace(@NotNull String locale, @NotNull BaseComponent[] components, @NotNull Object... replaces) {
        // Just copied from the LvMultiplesLanguages systems xD
        List<BaseComponent> componentList = new LinkedList<>();

        int row = 0;
        for (final BaseComponent component : ComponentUtils.cloneComponent(components)) {
            for (BaseComponent recursive : ComponentUtils.getComponents(component)) {
                if (recursive instanceof TextComponent) {
                    TextComponent text = (TextComponent) recursive;

                    while (text.getText().contains("%s")) {
                        if (replaces.length > row) {
                            Object replace = replaces[row];
                            BaseComponent[] index;

                            // TODO: 23/05/2023 Enhance this
                            if (replace instanceof IMessage) {
                                index = ((IMessage) replace).getText(locale);
                            } else if (replace instanceof BaseComponent) {
                                index = new BaseComponent[] { (BaseComponent) replace };
                            } else if (replace instanceof BaseComponent[]) {
                                index = (BaseComponent[]) replace;
                            } else if (replace instanceof Collection || replace instanceof Object[]) {
                                Object[] array;

                                if (replace instanceof Collection) {
                                    array = ((Collection<?>) replace).toArray();
                                } else {
                                    array = (Object[]) replace;
                                }

                                List<BaseComponent> componentList2 = new LinkedList<>();

                                int r = 0;
                                for (Object object : array) {
                                    if (r > 0) componentList2.add(new TextComponent("\n"));

                                    if (object instanceof IMessage) {
                                        componentList2.add(new TextComponent(((IMessage) object).getText(locale)));
                                    } else if (object instanceof BaseComponent) {
                                        componentList2.add((BaseComponent) object);
                                    } else if (object instanceof BaseComponent[]) {
                                        componentList2.add(new TextComponent((BaseComponent[]) object));
                                    } else {
                                        componentList2.add(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.valueOf(object))));
                                    }

                                    r++;
                                }

                                index = componentList2.toArray(new BaseComponent[0]);
                            } else {
                                index = new BaseComponent[] { new TextComponent(ChatColor.translateAlternateColorCodes('&', String.valueOf(replace))) };
                            }

                            // TODO: 11/05/2023 Component-based replace
                            text.setText(text.getText().replaceFirst(Pattern.quote("%s"), Matcher.quoteReplacement(ComponentUtils.getText(index))));
                            row++;
                        } else {
                            break;
                        }
                    }
                }
            }

            componentList.add(component);
        }

        return componentList.toArray(new BaseComponent[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageProvider)) return false;
        MessageProvider that = (MessageProvider) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
