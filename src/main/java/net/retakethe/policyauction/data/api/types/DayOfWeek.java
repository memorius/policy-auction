package net.retakethe.policyauction.data.api.types;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;


/**
 * Represents a day of the week in the Gregorian calendar, Mon-Sun.
 *
 * @author Nick Clarke
 */
public enum DayOfWeek {

    MONDAY   ("Monday",    "Mon"),
    TUESDAY  ("Tuesday",   "Tue"),
    WEDNESDAY("Wednesday", "Wed"),
    THURSDAY ("Thursday",  "Thu"),
    FRIDAY   ("Friday",    "Fri"),
    SATURDAY ("Saturday",  "Sat"),
    SUNDAY   ("Sunday",    "Sun");

    public static DayOfWeek fromLocalDate(LocalDate date) {
        return fromISO8601DayOfWeek(date.getDayOfWeek());
    }

    public static DayOfWeek fromISO8601DayOfWeek(final int dayOfWeek) {
        switch (dayOfWeek) {
            case DateTimeConstants.MONDAY:    return MONDAY;
            case DateTimeConstants.TUESDAY:   return TUESDAY;
            case DateTimeConstants.WEDNESDAY: return WEDNESDAY;
            case DateTimeConstants.THURSDAY:  return THURSDAY;
            case DateTimeConstants.FRIDAY:    return FRIDAY;
            case DateTimeConstants.SATURDAY:  return SATURDAY;
            case DateTimeConstants.SUNDAY:    return SUNDAY;
            default:
                throw new IllegalArgumentException("Invalid ISO8601 day of week: " + dayOfWeek);
        }
    }

    private final String longName;
    private final String shortName;

    private DayOfWeek(String longName, String shortName) {
        this.longName = longName;
        this.shortName = shortName;
    }

    public String getLongName() {
        return this.longName;
    }

    public String getShortName() {
        return this.shortName;
    }

    @Override
    public String toString() {
        return getLongName();
    }
}
