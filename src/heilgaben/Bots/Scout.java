package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.Debug;
import heilgaben.BotState;

public class Scout extends BotState {
    public static void run() throws GameActionException {
        /* Scout specific init */
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
                Debug.out("Scout Exception");
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