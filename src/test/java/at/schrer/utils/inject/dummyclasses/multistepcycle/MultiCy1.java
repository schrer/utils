package at.schrer.utils.inject.dummyclasses.multistepcycle;

import at.schrer.utils.inject.annotations.Component;

@Component
public class MultiCy1 {
    private final MultiCy2 multiCy2;


    public MultiCy1(MultiCy2 multiCy2) {
        this.multiCy2 = multiCy2;
    }
}
