package objects.misc;

import objects.misc.TermYear;

public class SystemConfiguration {
    public static int weeksInTermYear(TermYear termYear) {
        return switch (termYear.getTerm()) {
            case fall, spring -> 14;
            case winter, jterm -> 4;
            case summer -> 8;
        };
    }
}
