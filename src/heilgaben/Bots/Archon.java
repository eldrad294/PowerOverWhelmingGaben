package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.Debug;
import heilgaben.BotState;
import heilgaben.Util;

public class Archon extends BotState {
    public static void run() throws GameActionException {
        /* Archon specific init */
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
                Debug.out("Exception");
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
            spawn();
        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }

    private static boolean spawn() {
        try {
            Direction spawnDirection = Util.getRobotSpawnDirection(RobotType.GARDENER);
            if(spawnDirection != null) {
                rc.hireGardener(spawnDirection);
                return true;
            }
        } catch (Exception e) {
            Debug.out("Spawn Exception");
            e.printStackTrace();
        }

        return false;
    }
}
