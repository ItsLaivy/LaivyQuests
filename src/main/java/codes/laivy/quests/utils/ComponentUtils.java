package codes.laivy.quests.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

// TODO: 07/04/2023 OOP
public class ComponentUtils {

    private ComponentUtils() {
    }

    public static @NotNull String getLocale(@NotNull Player player) {
        try {
            return player.spigot().getLocale().toUpperCase();
        } catch (NoSuchMethodError ignore) {
            try {
                Method method = player.getClass().getDeclaredMethod("getLocale");
                return ((String) method.invoke(player)).toUpperCase();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Locale of the player", e);
            }
        }
    }

    public static @NotNull String getText(@NotNull BaseComponent... components) {
        StringBuilder str = new StringBuilder();

        for (BaseComponent component : components) {
            str.append(component.toLegacyText());
        }

        return str.substring(2);
    }
    public static @NotNull List<String> getText(@NotNull List<BaseComponent[]> components) {
        List<String> list = new LinkedList<>();
        for (BaseComponent[] component : components) {
            list.add(getText(component));
        }
        return list;
    }

    /**
     * Clones the components at the array
     * @param original the components that will be cloned
     * @return the component clones (including extras recursively)
     */
    public static @NotNull BaseComponent[] cloneComponent(@NotNull BaseComponent[] original) {
        List<BaseComponent> componentList = new LinkedList<>();

        for (BaseComponent component : original) {
            BaseComponent cloned = component.duplicate();
            if (component.getExtra() != null) {
                List<BaseComponent> clonedExtras = new ArrayList<>(new LinkedList<>(Arrays.asList(cloneComponent(component.getExtra().toArray(new BaseComponent[0])))));
                cloned.setExtra(clonedExtras);
            }
            componentList.add(cloned);
        }

        return componentList.toArray(new BaseComponent[0]);
    }

    public static @NotNull BaseComponent[] getComponents(@NotNull BaseComponent component) {
        List<BaseComponent> componentList = new LinkedList<>();

        componentList.add(component);
        if (component.getExtra() != null) {
            for (BaseComponent extra : component.getExtra()) {
                componentList.addAll(Arrays.asList(getComponents(extra)));
            }
        }

        return componentList.toArray(new BaseComponent[0]);
    }

}
