package de.pbauerochse.worklogviewer.domain;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
@FunctionalInterface
public interface Callback {

    void invoke();

}
