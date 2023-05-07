package codes.laivy.quests.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UpdateManagerProvider implements UpdateManager {

    private final @NotNull String current;
    private final boolean checkUpdates;

    /**
     * The default update manager of the LaivyQuests
     * @param current the current version of LaivyQuests
     */
    public UpdateManagerProvider(@NotNull String current, boolean checkUpdates) {
        this.current = current;
        this.checkUpdates = checkUpdates;
        // TODO: 07/05/2023 Update checker
    }

    public @NotNull String getCurrent() {
        return current;
    }

    @Override
    public boolean isCheckUpdates() {
        return checkUpdates;
    }

    @Override
    public @Nullable Update getUpdate() {
        return null;
    }
}
