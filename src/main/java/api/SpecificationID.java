package api;

import java.util.Objects;

public class SpecificationID implements Comparable<SpecificationID> {
    final public String identifier;
    final public String version;
    int hits = 0;

    public SpecificationID(String id, String ver){
        this.identifier = id;
        this.version = ver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpecificationID specification_id = (SpecificationID) o;

        if (!Objects.equals(identifier, specification_id.identifier)) return false;
        return Objects.equals(version, specification_id.version);
    }

    @Override
    public int hashCode() {
        int result = identifier != null ? identifier.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }


    @Override
    public int compareTo(SpecificationID o) {
        return Integer.compare(hits, o.hits);
    }
}
