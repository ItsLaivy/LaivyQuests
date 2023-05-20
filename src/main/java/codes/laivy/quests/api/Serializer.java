package codes.laivy.quests.api;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

public interface Serializer<T> {

    @NotNull JsonElement serialize(@NotNull T object);

    @NotNull T deserialize(@NotNull JsonElement object);

}
