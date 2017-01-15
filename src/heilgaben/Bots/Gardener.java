package heilgaben.Bots;

import battlecode.common.*;
import heilgaben.*;

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
            Util.initCenter();
            Util.initBorders();
            state = State.SEARCHING_GARDEN_SPOT;
        } catch (Exception e){
            Debug.out("Init Exception");
            e.printStackTrace();
        }
    }

    private static void act() {
        try {
            Util.updateBorders();

            switch(state){
                case SEARCHING_GARDEN_SPOT:
                    search();
                    break;
                case PLANTING_GARDEN:
                    plant();
                    water();
                    break;
                case TENDING_GARDEN:
                    spawn(RobotType.SCOUT);
                    water();
                    break;
                default:
                    Nav.move(Nav.getMoveDirection(myLocation));
                    break;
            }
        } catch (Exception e){
            Debug.out("Act Exception");
            e.printStackTrace();
        }
    }

    private static boolean search(){
        // Check for state change
        if(rc.senseNearbyRobots(3).length == 0 && rc.senseNearbyTrees(5).length == 0) {
            state = State.PLANTING_GARDEN;
            return true;
        }

        // Act according to state
        Nav.move(Nav.getMoveDirection(myLocation));
        return false;
    }

    private static boolean plant(){
        // Check for state change
        if(rc.senseNearbyTrees(3, myTeam).length >= 5){
            state = State.TENDING_GARDEN;
            return true;
        }

        // Act according to state;
        try {
            Direction spawnDirection = Util.getPlantDirection();
            if(spawnDirection == null)
                return false;

            if(rc.canPlantTree(spawnDirection)) {
                rc.plantTree(spawnDirection);
                return true;
            }

        } catch (Exception e){
            Debug.out("Plant Exception");
            e.printStackTrace();
        }

        return false;
    }

    private static boolean water(){
        try {
            TreeInfo[] treesInRange = rc.senseNearbyTrees(2, myTeam);
            if(treesInRange.length > 0) {
                TreeInfo lowestTree = treesInRange[0];

                for (TreeInfo tree : treesInRange) {
                    MapLocation treeLocation = tree.location;
                    if (rc.canInteractWithTree(treeLocation))
                        if (rc.canWater(treeLocation)) {
                            if (lowestTree.health > tree.health) {
                                lowestTree = tree;
                            }
                        }
                }
                rc.water(lowestTree.location);
                return true;
            }
        } catch (Exception e){
            Debug.out("Water Exception");
            e.printStackTrace();
        }

        return false;
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
