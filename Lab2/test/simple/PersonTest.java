package simple;

import com.google.gson.Gson;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/* Модульні тести методу equals (а також hashCode) класу Person. */
class PersonTest {

    static {
        /* дозволяє byte-buddy (на якому працює EqualsVerifier) працювати з новішими версіями JDK */
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private final Person person = new Person("Шевченко", "Тарас", 47);

    /* Перевірка всього контракту equals/hashCode бібліотекою EqualsVerifier. */
    @Test
    void equalsAndHashCodeContract() {
        EqualsVerifier.forClass(Person.class).verify();
    }

    @Test
    void isReflexive() {
        assertTrue(person.equals(person));
    }

    @Test
    void isSymmetric() {
        Person copy = new Person("Шевченко", "Тарас", 47);
        assertTrue(person.equals(copy));
        assertTrue(copy.equals(person));
    }

    @Test
    void isTransitive() {
        Person b = new Person("Шевченко", "Тарас", 47);
        Person c = new Person("Шевченко", "Тарас", 47);
        assertTrue(person.equals(b));
        assertTrue(b.equals(c));
        assertTrue(person.equals(c));
    }

    @Test
    void isNotEqualToNullOrOtherType() {
        assertFalse(person.equals(null));
        assertFalse(person.equals("Шевченко Тарас 47"));
    }

    @Test
    void differsWhenAnyFieldDiffers() {
        assertFalse(person.equals(new Person("Франко", "Тарас", 47)));
        assertFalse(person.equals(new Person("Шевченко", "Іван", 47)));
        assertFalse(person.equals(new Person("Шевченко", "Тарас", 48)));
    }

    @Test
    void equalObjectsHaveEqualHashCodes() {
        assertEquals(person.hashCode(), new Person("Шевченко", "Тарас", 47).hashCode());
    }

    @Test
    void handlesNullFields() {
        assertTrue(new Person(null, null, 0).equals(new Person(null, null, 0)));
        assertFalse(new Person(null, "Тарас", 0).equals(new Person("Шевченко", "Тарас", 0)));
    }

    /* Сценарій завдання: об'єкт -> JSON -> об'єкт, початковий і одержаний рівні за equals. */
    @Test
    void survivesJsonRoundTrip() {
        Gson gson = new Gson();
        Person restored = gson.fromJson(gson.toJson(person), Person.class);

        assertNotSame(person, restored);
        assertEquals(person, restored);
        assertEquals(person.hashCode(), restored.hashCode());
    }
}
