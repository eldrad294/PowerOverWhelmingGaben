package heilgaben.Actions;

import battlecode.common.*;
import heilgaben.*;
import scala.util.Random;

import static heilgaben.SignalConstants.*;

/**
 *
 * param  : conditionStateList  : A list of condition-state transitions. Condition must be met in order to transition to State
 * return : boolean             : True if state changed. False otherwise
 */

public class Action extends BotState {

//    public static boolean drive(ConditionState[] conditionStateList){
//        // Check for state change
//        for (ConditionState cs: conditionStateList){
//            if(cs.condition.isValid()){
//                state = cs.nextState;
//                return true;
//            }
//        }
//
//        // Act according to state
//        MapLocation scoutLocation;
//        scoutLocation = startingLocations[scoutCount%startingLocations.length];
//
//        if(scoutLocation.isWithinDistance(myLocation, myRobotSightRadius))
//            scoutCount++;
//
//        Nav.moveTo(scoutLocation);
//        try{
//            float[] coordinates;
//            for(int channel=SCOUT_START;channel<SCOUT_END;channel+=2) {
//                coordinates = Signal.receiveCoordinate(channel, channel + 1);
//                if(coordinates[0] > 0 && coordinates[1] > 0 && rc.getType() != RobotType.SCOUT)
//                    continue;
//                else if(rc.getType() != RobotType.SCOUT) {
//                    Nav.moveTo(new MapLocation(coordinates[0], coordinates[1]));
//                    break;
//                } else {
//                    Nav.moveTo(scoutLocation);
//                    break;
//                }
//            }
//
//        }catch(GameActionException e){
//            e.printStackTrace();
//        }
//
//        return false;
//    }

    public static boolean patrol(ConditionState[] conditionStateList){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        MapLocation scoutLocation;
        scoutLocation = startingLocations[scoutCount%startingLocations.length];

        if(scoutLocation.isWithinDistance(myLocation, myRobotSightRadius))
            scoutCount++;

        try{
            float[] coordinates;
            coordinates = Signal.receiveCoordinate(SignalConstants.SCOUT_START, SignalConstants.SCOUT_START + 1);
            if(coordinates[0] > 0 && coordinates[1] > 0)
                Nav.moveTo(new MapLocation(coordinates[0], coordinates[1]));
            else
                Nav.moveTo(scoutLocation);

        }catch(GameActionException e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean attack(ConditionState[] conditionStateList){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        if(nearbyEnemies.length == 0)
            return false;

        RobotInfo closestEnemy = nearbyEnemies[0];

        try {
            MapLocation enemyLocation = closestEnemy.location;

            if (rc.canFireSingleShot())
                rc.fireSingleShot(new Direction(myLocation, enemyLocation));

            Nav.moveTo(myLocation);

        } catch (Exception e) {
            Debug.out("Attack Exception");
            e.printStackTrace();
        }

        return false;
    }

    public static boolean chop(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        try {
            if (nearbyTrees.length > 0) {
                TreeInfo closestTree = nearbyTrees[0];
                MapLocation closestTreeLocation = closestTree.location;
                for (TreeInfo tree: nearbyTrees) {
                    MapLocation treeLocation = tree.location;

                    if(myLocation.distanceTo(treeLocation) < myLocation.distanceTo(closestTreeLocation))
                        closestTreeLocation = treeLocation;
                }
                rc.setIndicatorLine(myLocation, closestTreeLocation, 255, 0, 0);

                if (rc.canInteractWithTree(closestTreeLocation) && closestTree.team != myTeam) {
                    if (rc.canChop(closestTreeLocation))
                        rc.chop(closestTreeLocation);
                } else
                    Nav.moveTo(closestTreeLocation);
            }
        } catch (Exception e) {
            Debug.out("Chop Exception");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean detectBorderX(ConditionState[] conditionStateList, Direction borderDirection){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state;
        if(!Nav.moveTo(myLocation.add(borderDirection))) {
            float[] westEast = {0, 0};
            if (borderDirection.radians == Direction.getWest().radians) {
                westEast[0] = myLocation.x - myBodyRadius;
                westEast[1] = center.x + (center.x - westEast[0]) + (2*myBodyRadius);
            } else {
                westEast[1] = myLocation.x + myBodyRadius;
                westEast[0] = center.x - (westEast[1] - center.x) - (2*myBodyRadius);
            }

            try {
                Signal.broadcastCoordinate(NORTH_WEST | DATA_CHANNEL_X, SOUTH_EAST | DATA_CHANNEL_X, westEast);
                Signal.broadcastSignal(BORDER | DATA_CHANNEL_X, DETECTED);
            }catch(GameActionException e){
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean detectBorderY(ConditionState[] conditionStateList, Direction borderDirection) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state;
        if(!Nav.moveTo(myLocation.add(borderDirection))) {
            float[] northSouth = {0, 0};
            if (borderDirection.radians == Direction.getSouth().radians) {
                northSouth[1] = myLocation.y - myBodyRadius;
                northSouth[0] = center.y + (center.y - northSouth[1]) + (2*myBodyRadius);
            } else {
                northSouth[0] = myLocation.y + myBodyRadius;
                northSouth[1] = center.y - (northSouth[0] - center.y) - (2*myBodyRadius);
            }

            try{
                Signal.broadcastCoordinate(NORTH_WEST | DATA_CHANNEL_Y, SOUTH_EAST | DATA_CHANNEL_Y, northSouth);
                Signal.broadcastSignal(BORDER | DATA_CHANNEL_Y, DETECTED);
            }catch(GameActionException e){
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean harass(ConditionState[] conditionStateList){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        if(nearbyEnemies.length == 0)
            return false;

        RobotInfo closestEnemy = nearbyEnemies[0];

        try {
            MapLocation enemyLocation = closestEnemy.location;
            if (rc.canFireSingleShot())
                rc.fireSingleShot(new Direction(myLocation, enemyLocation));

            // Scouts also broadcast to allied bots, enemies they have encountered
            float[] coordinates = {enemyLocation.x, enemyLocation.y};
            Signal.broadcastCoordinate(SignalConstants.SCOUT_START, SignalConstants.SCOUT_START+1, coordinates);

            Nav.moveTo(myLocation);

        } catch (Exception e) {
            Debug.out("Harass Exception");
            e.printStackTrace();
        }

        return false;
    }

    public static boolean idle(ConditionState[] conditionStateList){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        Nav.moveTo(myLocation);
        return false;
    }

    public static boolean plant(ConditionState[] conditionStateList, Direction plantDirection) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state;
        try {
            if (plantDirection != null) {
                rc.plantTree(plantDirection);
            }

        } catch (Exception e) {
            Debug.out("Plant Exception");
            e.printStackTrace();
        }

        return false;
    }

    public static boolean spawn(ConditionState[] conditionStateList, RobotType robotType) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        try {
            Direction spawnDirection = Util.getRobotSpawnDirection(robotType);
            if(spawnDirection != null) {
                if(robotType == RobotType.GARDENER) {
                    rc.hireGardener(spawnDirection);
                } else {
                    rc.buildRobot(robotType, spawnDirection);
                }
            }
        } catch (Exception e) {
            Debug.out("Spawn Exception");
            e.printStackTrace();
        }

        return false;
    }


    public static boolean shake(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        TreeInfo closestBulletTree = Map.getClosestNonemptyBulletTree();
        try {
            rc.setIndicatorLine(myLocation, closestBulletTree.location, 255, 255, 255);
            if (rc.canShake(closestBulletTree.location)) {
                rc.shake(closestBulletTree.location);
            } else {
                Nav.moveTo(closestBulletTree.location);
            }
        } catch (Exception e){
            Debug.out("Shake Exception");
            e.printStackTrace();
        }

        return false;
    }

    public static boolean signalBorders(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        try{
            Signal.broadcastSignal(BORDER, DETECTED);
            border = Signal.receiveBorders();
        }catch(GameActionException e){
            e.printStackTrace();
        }
        return false;
    }

    // TODO: WIP
    public static boolean scout(ConditionState[] conditionStateList){
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        MapLocation scoutLocation;
        scoutLocation = startingLocations[scoutCount%startingLocations.length];

        if(scoutLocation.isWithinDistance(myLocation, myRobotSightRadius))
            scoutCount++;

        Nav.moveTo(scoutLocation);

        return false;
    }

    // TODO: WIP
    public static boolean search(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        Nav.moveTo(center);
        return false;
    }

    public static boolean strike(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        try {
            if (nearbyEnemies.length > 0) {
                MapLocation closestEnemyLocation = nearbyEnemies[0].location;
                if(rc.getLocation().distanceTo(closestEnemyLocation) < myBodyRadius + 0.25 + nearbyEnemies[0].getRadius()) {
                    if (rc.canStrike())
                        rc.strike();
                }
                else
                    Nav.moveTo(closestEnemyLocation);
            }
        } catch (Exception e) {
            Debug.out("Strike Exception");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean water(ConditionState[] conditionStateList) {
        // Check for state change
        for (ConditionState cs: conditionStateList){
            if(cs.condition.isValid()){
                state = cs.nextState;
                return true;
            }
        }

        // Act according to state
        try {
            TreeInfo[] treesInRange = rc.senseNearbyTrees(2, myTeam);
            if (treesInRange.length > 0) {
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
            }
        } catch (Exception e) {
            Debug.out("Water Exception");
            e.printStackTrace();
        }

        return false;
    }
}
