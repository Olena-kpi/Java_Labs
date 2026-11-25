package task3;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/* Потік-фільтр: зсуває код кожного прочитаного символу на offset
    (додатній - шифрування, від'ємний - дешифрування). */
public class KeyShiftReader extends FilterReader {

    /* Усі коди 0-FFFF, крім сурогатів D800-DFFF, - саме ці символи можна записати у файл. */
    private static final int SURROGATES = 0x800;
    private static final int USABLE = 0x10000 - SURROGATES;

    private final int offset;

    public KeyShiftReader(Reader source, int offset) {
        super(source);
        this.offset = offset;
    }

    @Override
    public int read() throws IOException {
        int c = in.read();
        return c == -1 ? -1 : shift((char) c);
    }

    @Override
    public int read(char[] buffer, int from, int length) throws IOException {
        int count = in.read(buffer, from, length);
        for (int i = from; i < from + count; i++) {
            buffer[i] = shift(buffer[i]);
        }
        return count;
    }

    /* Зсув по колу серед символів, які можна записати у файл, тому результат завжди
        коректний, а зворотний зсув точно відновлює початковий символ. Для звичайного
        тексту це просто "код + зсув". Сурогатні пари (емодзі) не змінюються. */
    private char shift(char c) {
        if (Character.isSurrogate(c)) {
            return c;
        }
        int number = c < 0xD800 ? c : c - SURROGATES;
        number = Math.floorMod(number + offset, USABLE);
        return (char) (number < 0xD800 ? number : number + SURROGATES);
    }
}
