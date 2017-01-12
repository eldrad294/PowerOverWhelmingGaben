package heilgaben.Bots;

import battlecode.common.*;

import heilgaben.BotState;
import heilgaben.Util;
import heilgaben.Debug;

public class Archon extends BotState {

    static boolean bordersDetected = false;

    public static void run() throws GameActionException {

        /* Archon specific init */
        init();

        while (true) {
            try {
                /* Update State */
                update();

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
            Util.setCenter();
        } catch (Exception e){
            Debug.out("Init Exception");
            e.printStackTrace();
        }
    }

    private static void act() {
        try {
            spawn();
            rc.setIndicatorDot(center, 255, 255, 0);
            rc.setIndicatorDot(new MapLocation(0, 0), 255, 255, 0);
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
