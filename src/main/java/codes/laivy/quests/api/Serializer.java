package codes.laivy.quests.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public interface Serializer<T> {

    @NotNull JsonElement serialize(@NotNull T object);

    @NotNull T deserialize(@NotNull JsonElement object);

}
