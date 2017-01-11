package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.Debug;
import heilgaben.BotState;
import heilgaben.Util;

public class Gardener extends BotState {
    public static void run() throws GameActionException {
        /* Gardener specific init */
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
                Debug.out("Gardener Exception");
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
            spawn(RobotType.SCOUT);
        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }

    private static boolean spawn(RobotType robotType) {
        try {
            Direction spawnDirection = Util.getRobotSpawnDirection(robotType);
            if(spawnDirection != null) {
                rc.buildRobot(robotType, spawnDirection);
                return true;
            }
        } catch (Exception e) {
            Debug.out("Spawn Exception");
            e.printStackTrace();
        }

        return false;
    }
}
