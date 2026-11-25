package simple;

import simple.animals.Eagle;
import simple.animals.Giraffe;
import simple.animals.Lion;
import simple.animals.Zebra;
import simple.cages.BirdCage;
import simple.cages.LionCage;
import simple.cages.UngulateCage;
import simple.exceptions.AnimalNotFoundException;
import simple.exceptions.CageFullException;

public class Main {

    public static void main(String[] args) {
        LionCage lionCage = new LionCage(2);
        UngulateCage ungulateCage = new UngulateCage(3);
        BirdCage birdCage = new BirdCage(2);

        Zoo zoo = new Zoo();
        zoo.addCage(lionCage);
        zoo.addCage(ungulateCage);
        zoo.addCage(birdCage);

        System.out.println("1. Розміщення тварин у вольєрах");
        lionCage.add(new Lion("Сімба"));
        lionCage.add(new Lion("Нала"));
        ungulateCage.add(new Zebra("Марті"));
        ungulateCage.add(new Giraffe("Мелман"));
        Eagle eagle = new Eagle("Сем");
        birdCage.add(eagle);
        for (var cage : zoo.cages) {
            System.out.println("   " + cage);
        }
        System.out.println("   Усього тварин у зоопарку: " + zoo.getCountOfAnimals());

        System.out.println();
        System.out.println("2. Обмеження типів перевіряє компілятор");
        /* lionCage.add(new Zebra("Зебра")); - не скомпілюється: у вольєр для левів можна лише левів */
        /* birdCage.add(new Lion("Лев")); - не скомпілюється: лев - не птах */
        System.out.println("   У вольєр для копитних можна і зебр, і жирафів: " + ungulateCage);

        System.out.println();
        System.out.println("3. Виняткові ситуації");
        try {
            lionCage.add(new Lion("Муфаса"));
        } catch (CageFullException e) {
            System.out.println("   Додавання у повний вольєр: " + e.getMessage());
        }
        try {
            birdCage.remove(new Eagle("Чужий"));
        } catch (AnimalNotFoundException e) {
            System.out.println("   Вилучення відсутньої тварини: " + e.getMessage());
        }
        birdCage.remove(eagle);
        System.out.println("   Після вилучення: " + birdCage);
        System.out.println("   Усього тварин у зоопарку: " + zoo.getCountOfAnimals());
    }
}
