package de.pbauerochse.youtrack.domain;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
@FunctionalInterface
public interface Callback {

    void invoke();

}
