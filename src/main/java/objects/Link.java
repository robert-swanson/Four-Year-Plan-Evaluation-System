package objects;


import exceptions.CatalogException;
import exceptions.LinkingException;
import exceptions.OfferingException;
import exceptions.PlanException;
import objects.catalog.Catalog;
import objects.offerings.Offerings;

public class Link {
    private Catalog catalog;
    private Offerings offerings;

    public Link(Catalog catalog, Offerings offerings) throws LinkingException {
        this.catalog = catalog;
        this.offerings = offerings;

        try {
            catalog.link(this);
            offerings.link(this);
        } catch (CatalogException e) {
            throw new LinkingException(String.format("Linking Catalog: %s", e.getMessage()));
        } catch (OfferingException e) {
            throw new LinkingException(String.format("Linking Offerings: %s", e.getMessage()));
//        } catch (PlanException e) {
//            throw new LinkingException(String.format("Linking Plans: %s", e.getMessage()));
        } catch (Exception e) {
            throw new LinkingException(String.format("Linking: %s", e));
        }
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public Offerings getOfferings() {
        return offerings;
    }

}
