package at.schrer.utils.inject.dummyclasses.yesdeps;

import at.schrer.utils.inject.annotations.Component;

@Component
public class DepComp2 {

    private final DepComp1 dep;

    public DepComp2(DepComp1 dep) {
        this.dep = dep;
    }
}
