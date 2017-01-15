package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;

public class Tank extends BotState {
    public static void run() throws GameActionException {
        /* Tank specific init */
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
                Debug.out("Tank Exception");
                e.printStackTrace();
            }
        }
    }

    private static void init() {
        try {
            Map.initCenter();
            Map.initBorders();
        } catch (Exception e){
            Debug.out("Init Exception");
            e.printStackTrace();
        }
    }

    private static void act() {
        try {
            Map.updateBorders();
        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }
}
