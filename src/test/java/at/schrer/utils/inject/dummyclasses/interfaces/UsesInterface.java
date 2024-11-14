package at.schrer.utils.inject.dummyclasses.interfaces;

import at.schrer.utils.inject.annotations.Component;

@Component
public class UsesInterface {
    private final SomeInterface someInterface;

    public UsesInterface(SomeInterface someInterface) {
        this.someInterface = someInterface;
    }

    public SomeInterface getSomeInterface() {
        return someInterface;
    }
}
