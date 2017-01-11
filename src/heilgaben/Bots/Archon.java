package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.Debug;
import heilgaben.BotState;

public class Archon extends BotState {
    public static void run() throws GameActionException {
        /* Archon specific init */
        init();

        while (true) {
            try {
                /* Update State */
                BotState.update();

                /* Round Actions


                /* Yield round */
                Clock.yield();

            } catch (Exception e) {
                Debug.out("Exception");
                e.printStackTrace();
            }
        }
    }

    private static void init() {
        try {

        } catch (Exception e){
            Debug.out("Init Exception");
        }
    }
}
