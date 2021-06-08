package de.pbauerochse.worklogviewer.tasks;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.Optional;

public class ProgressHandler implements Progress {

    private final double upperLimit;

    @Nullable
    private final ProgressHandler parentProgressHandler;

    private final DoubleProperty currentValue = new SimpleDoubleProperty(0.0);
    private final StringProperty currentMessage = new SimpleStringProperty("");

    public ProgressHandler() {
        this(100d, null);
    }

    ProgressHandler(double upperLimit, @Nullable ProgressHandler parentProgressHandler) {
        this.upperLimit = upperLimit;
        this.parentProgressHandler = parentProgressHandler;
    }

    public synchronized void setProgress(@NotNull String message, int progressInPercent) {
        this.setProgress(message, (double) progressInPercent);
    }

    @Override
    public synchronized void setProgress(@NotNull String message, double progressInPercent) {
        double newValue = Math.max(Math.min(((progressInPercent / 100d) * upperLimit), upperLimit), 0);
        double diff = newValue - currentValue.get();

        currentValue.set(newValue);
        currentMessage.set(message);

        Optional.ofNullable(parentProgressHandler).ifPresent(it -> it.childChanged(message, diff));
    }

    @NotNull
    @Override
    public synchronized Progress subProgress(int percentageOfParentProgress) {
        double remainingProgress = upperLimit - currentValue.get();
        double amountForSubprocess = Math.min(remainingProgress, (double) percentageOfParentProgress);

        return new ProgressHandler(amountForSubprocess, this);
    }

    @Override
    public synchronized void incrementProgress(int increment) {
        incrementProgress((double) increment);
    }

    @Override
    public synchronized void incrementProgress(double increment) {
        incrementProgress(currentMessage.get(), increment);
    }

    @Override
    public void incrementProgress(@NotNull String message, int increment) {
        incrementProgress(message, (double) increment);
    }

    @Override
    public synchronized void incrementProgress(@NotNull String message, double increment) {
        double currentInternalPercentage = (currentValue.get() / upperLimit) * 100d;
        double newInternalPercentage = currentInternalPercentage + increment;
        setProgress(message, newInternalPercentage);
    }

    public double getCurrentValue() {
        return currentValue.get();
    }

    public DoubleProperty currentValueProperty() {
        return currentValue;
    }

    public String getCurrentMessage() {
        return currentMessage.get();
    }

    public StringProperty currentMessageProperty() {
        return currentMessage;
    }

    private void childChanged(@NotNull String message, double diffValue) {
        double newValue = currentValue.get() + diffValue;
        double newValueInPercent = newValue / upperLimit * 100d;
        this.setProgress(message, newValueInPercent);
    }

    @TestOnly
    double getUpperLimit() {
        return upperLimit;
    }

}
