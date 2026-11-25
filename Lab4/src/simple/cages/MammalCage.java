package simple.cages;

import simple.animals.Mammal;

public abstract class MammalCage<T extends Mammal> extends Cage<T> {

    protected MammalCage(int maxPlaces) {
        super(maxPlaces);
    }
}
