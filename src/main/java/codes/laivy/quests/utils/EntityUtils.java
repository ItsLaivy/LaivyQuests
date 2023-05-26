package codes.laivy.quests.utils;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class EntityUtils {

    public static @NotNull String convertToBeautifulName(@NotNull EntityType entityType) {
        String[] words = entityType.name().toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String capitalizedWord = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            result.append(capitalizedWord).append(" ");
        }

        return result.toString().trim();
    }

}
