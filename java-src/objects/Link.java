package objects;


import exceptions.CatalogException;
import exceptions.LinkingException;
import exceptions.OfferingException;
import exceptions.PlanException;
import objects.catalog.Catalog;
import objects.offerings.Offerings;
import objects.plan.PlanTerm;
import objects.plan.PlansList;

public class Link {
    private Catalog catalog;
    private Offerings offerings;
    private PlansList plansList;

    public Link(Catalog catalog, Offerings offerings, PlansList plansList) {
        this.catalog = catalog;
        this.offerings = offerings;
        this.plansList = plansList;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public Offerings getOfferings() {
        return offerings;
    }

    public PlansList getPlansList() {
        return plansList;
    }

    public static void linkObjects(Catalog catalog, Offerings offerings, PlansList plansList) throws LinkingException {
        Link l = new Link(catalog, offerings, plansList);
        try {
            catalog.link(l);
            offerings.link(l);
            plansList.link(l);
        } catch (CatalogException e) {
            throw new LinkingException(String.format("Linking Catalog: %s", e.getMessage()));
        } catch (OfferingException e) {
            throw new LinkingException(String.format("Linking Offerings: %s", e.getMessage()));
        } catch (PlanException e) {
            throw new LinkingException(String.format("Linking Plans: %s", e.getMessage()));
        } catch (Exception e) {
            throw new LinkingException(String.format("Linking: %s", e.toString()));
        }
    }
}
