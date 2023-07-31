import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        Thread generator = new Thread(() -> {
            Random random = new Random();
            String letters = "abc";
            for (int i = 0; i < 10000; i++) {
                StringBuilder text = new StringBuilder();
                for (int j = 0; j < 100000; j++) {
                    text.append(letters.charAt(random.nextInt(letters.length())));
                }
                try {
                    queueA.put(text.toString());
                    queueB.put(text.toString());
                    queueC.put(text.toString());
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadA = new Thread(() -> {
            int maxCount = 0;
            String maxText = "";
            while (true) {
                try {
                    String text = queueA.take();
                    int count = countChar(text, 'a');
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                        System.out.println("Поток A: новое максимальное количество 'a' - " + maxCount);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadB = new Thread(() -> {
            int maxCount = 0;
            String maxText = "";
            while (true) {
                try {
                    String text = queueB.take();
                    int count = countChar(text, 'b');
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                        System.out.println("Поток B: новое максимальное количество 'b' - " + maxCount);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadC = new Thread(() -> {
            int maxCount = 0;
            String maxText = "";
            while (true) {
                try {
                    String text = queueC.take();
                    int count = countChar(text, 'c');
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                        System.out.println("Поток C: новое максимальное количество 'c' - " + maxCount);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        generator.start();
        threadA.start();
        threadB.start();
        threadC.start();

        generator.join();
        threadA.interrupt();
        threadB.interrupt();
        threadC.interrupt();
    }

    private static int countChar(String text, char ch) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
}