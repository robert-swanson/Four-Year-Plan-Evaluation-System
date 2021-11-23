package objects;

import exceptions.OfferingException;

public interface Linkable {
    Link link = null;
    public void link(Link link) throws Exception;
}
