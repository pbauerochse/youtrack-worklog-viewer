package de.pbauerochse.worklogviewer.settings;

import java.time.DayOfWeek;

class WeekdaySettingsBitMask {

    WeekdaySettingsBitMask(DayOfWeek... initialState) {
    }

    private int createBitMaskState(DayOfWeek... setDays) {
        int bitmask = 0;

        for (DayOfWeek day : setDays) {
            bitmask = setBitValue(bitmask, day, true);
        }

        return bitmask;
    }

    private int setBitValue(int state, DayOfWeek day, boolean selected) {
        if (selected) {
            return state | (1 << day.ordinal());
        } else {
            return state & ~(1 << day.ordinal());
        }
    }

    private boolean hasBitValue(int state, DayOfWeek day) {
        int bitValue = (1 << day.ordinal());
        return (state & bitValue) == bitValue;
    }

}
