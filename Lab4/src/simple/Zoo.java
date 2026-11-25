package simple;

import simple.cages.Cage;

import java.util.ArrayList;
import java.util.List;

public class Zoo {

    /* Cage<?> - вольєр для будь-яких тварин: так у списку можуть бути вольєри різних видів. */
    public List<Cage<?>> cages = new ArrayList<>();

    public void addCage(Cage<?> cage) {
        cages.add(cage);
    }

    public int getCountOfAnimals() {
        int count = 0;
        for (Cage<?> cage : cages) {
            count += cage.getOccupiedPlaces();
        }
        return count;
    }
}
