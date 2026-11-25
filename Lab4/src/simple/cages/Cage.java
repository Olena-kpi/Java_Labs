package simple.cages;

import simple.animals.Animal;
import simple.exceptions.AnimalNotFoundException;
import simple.exceptions.CageFullException;

import java.util.ArrayList;
import java.util.List;

/* T - вид тварин, яких можна тримати у вольєрі. Тварину іншого виду компілятор не дозволить. */
public abstract class Cage<T extends Animal> {

    private final int maxPlaces;
    private final List<T> animals = new ArrayList<>();

    protected Cage(int maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public int getMaxPlaces() {
        return maxPlaces;
    }

    public int getOccupiedPlaces() {
        return animals.size();
    }

    /* Якщо вільних місць немає, кидає CageFullException. */
    public void add(T animal) {
        if (animals.size() >= maxPlaces) {
            throw new CageFullException("У вольєрі " + getClass().getSimpleName()
                    + " немає вільних місць");
        }
        animals.add(animal);
    }

    /* Якщо тварини у вольєрі немає, кидає AnimalNotFoundException. */
    public void remove(T animal) {
        if (!animals.remove(animal)) {
            throw new AnimalNotFoundException("Тварини " + animal + " немає у вольєрі "
                    + getClass().getSimpleName());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + animals.size() + "/" + maxPlaces + "] " + animals;
    }
}
