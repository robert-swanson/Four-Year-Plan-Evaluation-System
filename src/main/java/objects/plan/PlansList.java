package objects.plan;

import exceptions.PlanException;
import objects.Linkable;
import objects.Link;

import java.util.ArrayList;

public class PlansList implements Linkable {
    ArrayList<Plan> plans;

    private transient Link link;
    @Override
    public void link(Link l) throws PlanException {
        link = l;
        for (Plan p : plans) {
            p.link(l);
        }
    }

    public Plan getPlan(int planIndex) {
        return plans.get(planIndex);
    }

    public int size() {
        return plans.size();
    }
}
