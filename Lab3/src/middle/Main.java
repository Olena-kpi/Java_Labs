package middle;

import middle.controller.SkiController;
import middle.model.SkiPassRegistry;
import middle.model.Turnstile;
import middle.view.ConsoleView;

public class Main {

    public static void main(String[] args) {
        SkiPassRegistry registry = new SkiPassRegistry();
        Turnstile turnstile = new Turnstile(registry);
        new SkiController(registry, turnstile, new ConsoleView()).run();
    }
}
