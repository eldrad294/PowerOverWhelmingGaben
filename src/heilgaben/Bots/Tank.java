package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.Debug;
import heilgaben.BotState;
import heilgaben.Signal;
import heilgaben.Util;

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
            Util.initCenter();
            Util.initBorders();
        } catch (Exception e){
            Debug.out("Init Exception");
            e.printStackTrace();
        }
    }

    private static void act() {
        try {
            Util.updateBorders();
        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }
}
