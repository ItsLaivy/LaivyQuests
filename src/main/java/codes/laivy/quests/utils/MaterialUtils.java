package codes.laivy.quests.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class MaterialUtils {

    public static @NotNull String convertToBeautifulName(@NotNull Material material) {
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String capitalizedWord = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            result.append(capitalizedWord).append(" ");
        }

        return result.toString().trim();
    }

}
