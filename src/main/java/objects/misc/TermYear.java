package objects.misc;

import com.google.gson.JsonParseException;
import exceptions.JSONParseException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TermYear implements Comparable<TermYear> {
    public final Term term;
    public final CatalogYear catalogYear;

    public TermYear(Term term, CatalogYear catalogYear) {
        this.term = term;
        this.catalogYear = catalogYear;
    }

    public TermYear(String termYear) throws JSONParseException {
        Matcher termYearMatcher = Pattern.compile("(\\w+) ?(\\d{4})").matcher(termYear);
        if (!termYearMatcher.matches()) throw new JsonParseException(String.format("TermYear '%s' does not follow format '{semester} yyyy'", termYear));
        term = Term.parseTerm(termYearMatcher.group(1));
        catalogYear = new CatalogYear(term, termYearMatcher.group(2));
    }

    public TermYear nextTermYear() {
        return switch (term) {
            case jterm -> new TermYear(Term.spring, catalogYear);
            case spring -> new TermYear(Term.summer, catalogYear);
            case summer -> new TermYear(Term.fall, catalogYear);
            case fall -> new TermYear(Term.winter, catalogYear);
            case winter -> new TermYear(Term.jterm, catalogYear.increment(1));
        };
    }


    public boolean isSemester() {
        return switch (term) {
            case fall, spring -> true;
            default -> false;
        };
    }

    @Override
    public int compareTo(TermYear o) {
        int yearDiff = year() - o.year();
        int termDiff = term.ordinal() - o.term.ordinal();
        return yearDiff * 10 + termDiff;
    }

    public enum Term {
        jterm, spring, summer, fall, winter;

        public static Term parseTerm(String term) throws JSONParseException {
            return switch (term) {
                case "spring" -> Term.spring;
                case "fall" -> Term.fall;
                case "summer" -> Term.summer;
                case "winter" -> Term.winter;
                case "jterm" -> Term.jterm;
                default -> throw new JSONParseException(String.format("Unknown term '%s'", term));
            };
        }

        public int intVal() {
            return switch (this) {
                case jterm -> 0;
                case spring -> 1;
                case summer -> 2;
                case fall -> 3;
                case winter -> 4;
            };
        }

    }

    public Term getTerm() {
        return term;
    }

    public int year() {
        return switch (term) {
            case jterm, spring, summer -> catalogYear.endYear;
            case fall, winter -> catalogYear.startYear;
        };
    }

    @Override
    public String toString() {
        return String.format("%s %d", term.toString(), year());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermYear otherTermYear = (TermYear) o;
        return term == otherTermYear.term && Objects.equals(catalogYear, otherTermYear.catalogYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, catalogYear);
    }
}
