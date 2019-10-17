package de.pbauerochse.worklogviewer.fx.tasks;

import org.jetbrains.annotations.NotNull;

public interface ProgressHandlerCallback {

    void childChanged(@NotNull String message, double totalValueInPercent);

}
