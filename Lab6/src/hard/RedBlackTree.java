package hard;

import java.util.ArrayList;
import java.util.List;

/* Ліво-нахильне червоно-чорне дерево пошуку (варіант Седжвіка) з цілими числами:
    червоні вузли можуть бути лише лівими дітьми. Числа не повторюються. */
public class RedBlackTree {

    private static class Node {
        int key;
        Node left;
        Node right;
        boolean red;

        Node(int key, boolean red) {
            this.key = key;
            this.red = red;
        }
    }

    private Node root;
    private int rotations;      /* лічильники для статистики операцій */
    private int colorFlips;

    public boolean contains(int key) {
        Node node = root;
        while (node != null) {
            if (key == node.key) {
                return true;
            }
            node = key < node.key ? node.left : node.right;
        }
        return false;
    }

    /* Додає число. Повертає false, якщо воно вже є в дереві. */
    public boolean insert(int key) {
        if (contains(key)) {
            return false;
        }
        root = insert(root, key);
        root.red = false;
        return true;
    }

    private Node insert(Node node, int key) {
        if (node == null) {
            return new Node(key, true);
        }
        if (key < node.key) {
            node.left = insert(node.left, key);
        } else {
            node.right = insert(node.right, key);
        }
        return fixUp(node);
    }

    /* Видаляє число. Повертає false, якщо його немає в дереві. */
    public boolean delete(int key) {
        if (!contains(key)) {
            return false;
        }
        if (!isRed(root.left) && !isRed(root.right)) {
            root.red = true;
        }
        root = delete(root, key);
        if (root != null) {
            root.red = false;
        }
        return true;
    }

    private Node delete(Node node, int key) {
        if (key < node.key) {
            if (!isRed(node.left) && !isRed(node.left.left)) {
                node = moveRedLeft(node);
            }
            node.left = delete(node.left, key);
        } else {
            if (isRed(node.left)) {
                node = rotateRight(node);
            }
            if (key == node.key && node.right == null) {
                return null;
            }
            if (!isRed(node.right) && !isRed(node.right.left)) {
                node = moveRedRight(node);
            }
            if (key == node.key) {
                /* замінюємо число найменшим із правого піддерева й видаляємо цей вузол */
                node.key = minimum(node.right).key;
                node.right = deleteMinimum(node.right);
            } else {
                node.right = delete(node.right, key);
            }
        }
        return fixUp(node);
    }

    private Node deleteMinimum(Node node) {
        if (node.left == null) {
            return null;
        }
        if (!isRed(node.left) && !isRed(node.left.left)) {
            node = moveRedLeft(node);
        }
        node.left = deleteMinimum(node.left);
        return fixUp(node);
    }

    private Node minimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /* Відновлює властивості дерева після зміни піддерева. */
    private Node fixUp(Node node) {
        if (isRed(node.right) && !isRed(node.left)) {
            node = rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }
        return node;
    }

    private boolean isRed(Node node) {
        return node != null && node.red;
    }

    /* Червоне праве посилання стає лівим. */
    private Node rotateLeft(Node node) {
        rotations++;
        Node top = node.right;
        node.right = top.left;
        top.left = node;
        top.red = node.red;
        node.red = true;
        return top;
    }

    /* Два червоні лівих посилання поспіль перетворюються на пару червоних дітей. */
    private Node rotateRight(Node node) {
        rotations++;
        Node top = node.left;
        node.left = top.right;
        top.right = node;
        top.red = node.red;
        node.red = true;
        return top;
    }

    /* Міняє кольори вузла та обох його дітей на протилежні. */
    private void flipColors(Node node) {
        colorFlips++;
        node.red = !node.red;
        node.left.red = !node.left.red;
        node.right.red = !node.right.red;
    }

    /* Готує лівий бік до видалення: після виклику лівий син або його лівий онук червоний,
        тому видалення не порушить кількість чорних вузлів. */
    private Node moveRedLeft(Node node) {
        flipColors(node);
        if (isRed(node.right.left)) {
            node.right = rotateRight(node.right);
            node = rotateLeft(node);
            flipColors(node);
        }
        return node;
    }

    /* Те саме для правого боку. */
    private Node moveRedRight(Node node) {
        flipColors(node);
        if (isRed(node.left.left)) {
            node = rotateRight(node);
            flipColors(node);
        }
        return node;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return inOrder().size();
    }

    public int height() {
        return height(root);
    }

    private int height(Node node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }

    /* Обхід у порядку зростання: ліве піддерево, вузол, праве піддерево. */
    public List<Integer> inOrder() {
        List<Integer> keys = new ArrayList<>();
        inOrder(root, keys);
        return keys;
    }

    private void inOrder(Node node, List<Integer> keys) {
        if (node != null) {
            inOrder(node.left, keys);
            keys.add(node.key);
            inOrder(node.right, keys);
        }
    }

    /* Прямий обхід: вузол, ліве піддерево, праве піддерево. */
    public List<Integer> preOrder() {
        List<Integer> keys = new ArrayList<>();
        preOrder(root, keys);
        return keys;
    }

    private void preOrder(Node node, List<Integer> keys) {
        if (node != null) {
            keys.add(node.key);
            preOrder(node.left, keys);
            preOrder(node.right, keys);
        }
    }

    /* Зворотний обхід: ліве піддерево, праве піддерево, вузол. */
    public List<Integer> postOrder() {
        List<Integer> keys = new ArrayList<>();
        postOrder(root, keys);
        return keys;
    }

    private void postOrder(Node node, List<Integer> keys) {
        if (node != null) {
            postOrder(node.left, keys);
            postOrder(node.right, keys);
            keys.add(node.key);
        }
    }

    public int rotations() {
        return rotations;
    }

    public int colorFlips() {
        return colorFlips;
    }

    /* Шлях пошуку від кореня: ключі всіх вузлів, які відвідано, поки число не знайдено
        або не стало зрозуміло, що його немає. */
    public List<Integer> searchPath(int key) {
        List<Integer> path = new ArrayList<>();
        for (Node node = root; node != null; node = key < node.key ? node.left : node.right) {
            path.add(node.key);
            if (key == node.key) {
                break;
            }
        }
        return path;
    }

    /* Дерево текстом згори вниз: корінь угорі, нижче - діти з гілками. Перед кожною дитиною
        стоїть позначка л (ліва) або п (права), у дужках - колір вузла. */
    public String draw() {
        StringBuilder text = new StringBuilder();
        if (root != null) {
            text.append(label(root)).append('\n');
            drawChildren(root, "", text);
        }
        return text.toString();
    }

    private void drawChildren(Node node, String indent, StringBuilder text) {
        Node[] children = {node.left, node.right};
        String[] sides = {"л", "п"};
        int last = children[1] != null ? 1 : 0;
        for (int i = 0; i < 2; i++) {
            if (children[i] == null) {
                continue;
            }
            boolean isLast = i == last;
            text.append(indent).append(isLast ? "└─ " : "├─ ").append(sides[i]).append(": ")
                    .append(label(children[i])).append('\n');
            drawChildren(children[i], indent + (isLast ? "   " : "│  "), text);
        }
    }

    private String label(Node node) {
        return node.key + (node.red ? " [червоний]" : " [чорний]");
    }

    /* Перевіряє властивості: числа впорядковані, корінь чорний, червоний вузол не має
        червоного сина, правих червоних посилань немає, а кількість чорних вузлів
        на кожному шляху від кореня до порожнього місця однакова. */
    public boolean isValid() {
        return !isRed(root) && blackHeight(root, Long.MIN_VALUE, Long.MAX_VALUE) != -1;
    }

    /* Повертає кількість чорних вузлів на шляху вниз або -1, якщо властивості порушено. */
    private int blackHeight(Node node, long min, long max) {
        if (node == null) {
            return 0;
        }
        if (node.key <= min || node.key >= max || isRed(node.right) || (node.red && isRed(node.left))) {
            return -1;
        }
        int left = blackHeight(node.left, min, node.key);
        int right = blackHeight(node.right, node.key, max);
        if (left == -1 || right == -1 || left != right) {
            return -1;
        }
        return left + (node.red ? 0 : 1);
    }
}
