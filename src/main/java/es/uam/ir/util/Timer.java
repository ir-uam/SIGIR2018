package es.uam.ir.util;

import static java.lang.System.currentTimeMillis;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Pablo
 */
public class Timer {

    private static final Map<Object, Long> now = new HashMap<>();
    private static final Map<Object, Long> prev = new HashMap<>();

    public static void start(Object obj, String... msg) {
        if (msg.length > 0) {
            System.out.println(msg[0]);
        }
        prev.put(obj, currentTimeMillis());
    }

    public static void done(Object obj, String msg) {
        now.put(obj, currentTimeMillis());
        long ms = now.get(obj) - prev.get(obj);
        int s = (int) (ms / 1000);
        int min = (int) (ms / (1000 * 60));
        int h = (int) (ms / (1000 * 60 * 60));
        int d = (int) (ms / (1000 * 60 * 60 * 24));
        s -= min * 60;
        min -= h * 60;
        h -= d * 24;
        System.out.print(msg + " (");
        if (d > 0) {
            System.out.print(d + " days ");
        }
        if (h > 0) {
            System.out.print(h + "h ");
        }
        if (min > 0) {
            System.out.print(min + "min ");
        }
        System.out.println(s + "s)");
        prev.put(obj, now.get(obj));
    }

    public static void start(String... msg) {
        start(0, msg);
    }

    public static void done(String msg) {
        done(0, msg);
    }
}
