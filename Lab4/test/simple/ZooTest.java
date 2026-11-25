package simple;

import org.junit.jupiter.api.Test;
import simple.animals.Eagle;
import simple.animals.Lion;
import simple.animals.Zebra;
import simple.cages.BirdCage;
import simple.cages.LionCage;
import simple.cages.UngulateCage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZooTest {

    @Test
    void emptyZooHasNoAnimals() {
        assertEquals(0, new Zoo().getCountOfAnimals());
    }

    @Test
    void zooCountsAnimalsInAllCages() {
        LionCage lions = new LionCage(2);
        UngulateCage ungulates = new UngulateCage(2);
        BirdCage birds = new BirdCage(2);
        lions.add(new Lion("Сімба"));
        lions.add(new Lion("Нала"));
        ungulates.add(new Zebra("Марті"));
        birds.add(new Eagle("Сем"));

        Zoo zoo = new Zoo();
        zoo.addCage(lions);
        zoo.addCage(ungulates);
        zoo.addCage(birds);

        assertEquals(4, zoo.getCountOfAnimals());
    }
}
