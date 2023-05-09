package codes.laivy.quests.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Objects;

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
        private final @NotNull Date date;
        private final boolean preRelease;

        public Update(@NotNull String name, @NotNull String link, @NotNull Date date, boolean preRelease) {
            this.name = name;
            this.link = link;
            this.date = date;
            this.preRelease = preRelease;
        }

        public @NotNull String getName() {
            return name;
        }

        public @NotNull String getLink() {
            return link;
        }

        public @NotNull Date getDate() {
            return date;
        }

        public boolean isPreRelease() {
            return preRelease;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Update update = (Update) o;
            return preRelease == update.preRelease && name.equals(update.name) && link.equals(update.link) && date.equals(update.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, link, date, preRelease);
        }
    }

}
