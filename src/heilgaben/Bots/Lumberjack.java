package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.Debug;
import heilgaben.BotState;

public class Lumberjack extends BotState {
    public static void run() throws GameActionException {
        /* Lumberjack specific init */
        init();

        while (true) {
            try {
                /* Update State */
                BotState.update();

                /* Round Actions */
                act();

                /* Yield round */
                Clock.yield();

            } catch (Exception e) {
                Debug.out("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }

    private static void init() {
        try {

        } catch (Exception e){
            Debug.out("Init Exception");
            e.printStackTrace();
        }
    }

    private static void act() {
        try {

        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }
}
