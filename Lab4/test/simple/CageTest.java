package simple;

import org.junit.jupiter.api.Test;
import simple.animals.Eagle;
import simple.animals.Giraffe;
import simple.animals.Lion;
import simple.animals.Zebra;
import simple.cages.BirdCage;
import simple.cages.LionCage;
import simple.cages.UngulateCage;
import simple.exceptions.AnimalNotFoundException;
import simple.exceptions.CageFullException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CageTest {

    @Test
    void addingAnimalsIncreasesOccupiedPlaces() {
        LionCage cage = new LionCage(3);
        cage.add(new Lion("Сімба"));
        cage.add(new Lion("Нала"));
        assertEquals(2, cage.getOccupiedPlaces());
        assertEquals(3, cage.getMaxPlaces());
    }

    @Test
    void fullCageThrowsException() {
        BirdCage cage = new BirdCage(1);
        cage.add(new Eagle("Сем"));
        assertThrows(CageFullException.class, () -> cage.add(new Eagle("Рекс")));
    }

    @Test
    void removingAnimalFreesPlace() {
        LionCage cage = new LionCage(2);
        Lion lion = new Lion("Сімба");
        cage.add(lion);
        cage.remove(lion);
        assertEquals(0, cage.getOccupiedPlaces());
    }

    @Test
    void removingMissingAnimalThrowsException() {
        LionCage cage = new LionCage(2);
        assertThrows(AnimalNotFoundException.class, () -> cage.remove(new Lion("Чужий")));
    }

    @Test
    void ungulateCageAcceptsZebrasAndGiraffes() {
        UngulateCage cage = new UngulateCage(2);
        cage.add(new Zebra("Марті"));
        cage.add(new Giraffe("Мелман"));
        assertEquals(2, cage.getOccupiedPlaces());
    }
}
