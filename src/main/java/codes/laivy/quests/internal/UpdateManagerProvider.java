package codes.laivy.quests.internal;

import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.utils.DateUtils;
import com.google.gson.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class UpdateManagerProvider implements UpdateManager {

    private final @NotNull String version;
    private final boolean checkUpdates;

    private final @Nullable BukkitTask task;

    /**
     * The default update manager of the LaivyQuests
     * @param version the current version of LaivyQuests
     */
    public UpdateManagerProvider(@NotNull LaivyQuests plugin, @NotNull String version, boolean checkUpdates) {
        this.version = version;
        this.checkUpdates = checkUpdates;

        if (checkUpdates) {
            Update update = getUpdate(version);
            task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Update last = getLastStableUpdate();
                if (!last.equals(update)) {
                    plugin.log(TextComponent.fromLegacyText("§7There is a update available for the §6LaivyQuests §7since §f" + DateUtils.getDateAsString(System.currentTimeMillis() - last.getDate().getTime()) + "§7!"));
                    plugin.log(TextComponent.fromLegacyText("§7New version: §f" + last.getName() + "§7, your version: §f" + getVersion() + "§7."));
                    plugin.log(TextComponent.fromLegacyText("§6" + last.getLink()));
                } else {
                    plugin.log(TextComponent.fromLegacyText("§7Your §6LaivyQuests §7is updated :)"));
                }
            }, 0, 3600000);
        } else {
            task = null;
        }
    }

    public @Nullable BukkitTask getTask() {
        return task;
    }

    public @NotNull String getVersion() {
        return version;
    }

    protected @NotNull Set<Update> getUpdates(boolean stableOnly) {
        Set<Update> updates = new LinkedHashSet<>();

        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/repos/ItsLaivy/LaivyQuests/releases");

            URI uri = uriBuilder.build();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Accept", "application/vnd.github.v3+json");

            String responseString = httpClient.execute(httpGet, new BasicResponseHandler());
            JsonArray releasesJson = new JsonParser().parse(responseString).getAsJsonArray();

            for (JsonElement element : releasesJson) {
                JsonObject releaseJson = element.getAsJsonObject();

                String createdAt = releaseJson.get("created_at").getAsString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                dateFormat.setLenient(false);
                Date date = dateFormat.parse(createdAt);
                boolean preRelease = releaseJson.get("prerelease").getAsBoolean();

                if (stableOnly && preRelease) {
                    continue;
                }

                updates.add(new Update(
                        releaseJson.get("tag_name").getAsString(),
                        releaseJson.get("html_url").getAsString(),
                        date,
                        preRelease
                ));
            }
        } catch (Throwable e) {
            throw new RuntimeException("Github API linking", e);
        }

        return updates;
    }

    protected @NotNull Update getUpdate(@NotNull String version) {
        for (Update update : getUpdates(false)) {
            if (update.getName().equals(version)) {
                return update;
            }
        }
        throw new NullPointerException("Couldn't find details for version '" + version + "'");
    }
    protected @NotNull Update getLastStableUpdate() {
        return new LinkedList<>(getUpdates(true)).getLast();
    }

    @Override
    public boolean isCheckUpdates() {
        return checkUpdates;
    }

    @Override
    public @Nullable Update getUpdate() {
        return getUpdate(getVersion());
    }
}
