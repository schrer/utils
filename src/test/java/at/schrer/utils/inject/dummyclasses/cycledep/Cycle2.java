package at.schrer.utils.inject.dummyclasses.cycledep;

import at.schrer.utils.inject.annotations.Component;

@Component
public class Cycle2 {
    private final Cycle1 cycle1;

    public Cycle2(Cycle1 cycle1) {
        this.cycle1 = cycle1;
    }
}
