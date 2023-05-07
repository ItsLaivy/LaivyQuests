package codes.laivy.quests.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface UpdateManager {

    /**
     * Checks if the updates will be checked or not
     * @return true if the updates will be looked, false otherwise
     */
    boolean isCheckUpdates();

    /**
     * Return the latest stable update available
     * @return the latest stable update or null if it doesn't have one ready yet
     */
    @Nullable Update getUpdate();

    final class Update {
        private final @NotNull String name;
        private final @NotNull String link;
        private final @Range(from = 1, to = Integer.MAX_VALUE) int behind;

        public Update(@NotNull String name, @NotNull String link, @Range(from = 1, to = Integer.MAX_VALUE) int behind) {
            this.name = name;
            this.link = link;
            this.behind = behind;
        }

        public @NotNull String getName() {
            return name;
        }

        public @NotNull String getLink() {
            return link;
        }

        public int getBehind() {
            return behind;
        }
    }

}
