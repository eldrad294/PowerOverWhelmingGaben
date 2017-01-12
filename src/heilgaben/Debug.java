package heilgaben;

/**
 * Created by Alarak on 11/01/2017.
 */
public class Debug extends BotState {
    public static void out(String message){
        System.out.println(myType + ": " + message);
    }

    public static void out(float x, float y) { out("(" + x + ", " + y + ")"); }

    public static void out(int x, int y) { out("(" + x + ", " + y + ")"); }
}
