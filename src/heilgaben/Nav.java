package heilgaben;

import battlecode.common.*;

import java.util.ArrayList;

/**
 * Navigation Class
 */
public class Nav extends BotState {
    public static boolean move(Direction moveDirection) {
        try {
            if(rc.hasMoved())
                return false;

            if(moveDirection == null)
                return false;

            float maxMoveDistance = Nav.getMaxMoveDistance(moveDirection);
            if (rc.canMove(moveDirection, maxMoveDistance)) {
                rc.move(moveDirection, maxMoveDistance);

                MapLocation nextLocation = new MapLocation(myLocation.x + moveDirection.getDeltaX(1), myLocation.y + moveDirection.getDeltaY(1));
                rc.setIndicatorLine(myLocation, nextLocation, 255, 0, 255);

                if(targetLocation != null)
                    rc.setIndicatorLine(myLocation, targetLocation, 255, 0, 255);

                return true;
            }
        } catch (Exception e){
            Debug.out("Move Exception");
            e.printStackTrace();
        }
        return false;

    }

    public static Direction getMoveDirection(MapLocation destination){
        ArrayList<Direction> allDirections = new ArrayList<>();

        if(destination != myLocation)
            allDirections.add(new Direction(myLocation, destination));

        // TO DO - Use vectors with magnitude instead of Directions
        ArrayList<Direction> repulsiveDirections = getRepulsiveDirections(myBodyRadius*2);
        allDirections.addAll(repulsiveDirections);

//        ArrayList<Direction> attractiveDirections = getAttractiveDirections(myBodyRadius*2);
//        allDirections.addAll(attractiveDirections);

        return computeResultantDirection(allDirections);
    }

    public static ArrayList<Direction> getRepulsiveDirections(float repulsionRadius){
        ArrayList<Direction> repulsionDirections = new ArrayList<>();

        for(RobotInfo robot: nearbyRobots) {
            MapLocation robotLocation = robot.getLocation();
            if (myLocation.distanceTo(robotLocation) <= (repulsionRadius + (robot.getRadius()*2)))
                repulsionDirections.add(new Direction(robotLocation, myLocation));
        }

        if(myType != RobotType.SCOUT) {
            for (TreeInfo tree : nearbyTrees) {
                MapLocation treeLocation = tree.getLocation();
                if (myLocation.distanceTo(treeLocation) <= (repulsionRadius + (tree.getRadius() * 1)))
                    repulsionDirections.add(new Direction(treeLocation, myLocation));
            }
        }

        for(BulletInfo bullet: nearbyBullets) {
            MapLocation bulletLocation = bullet.getLocation();
            if(willCollideWithMe(bullet)) {
                repulsionDirections.add(new Direction(bulletLocation, myLocation).rotateLeftDegrees(90));
            }
        }

        if(state != State.DETECTING_BORDER_Y && state != State.DETECTING_BORDER_X && Signal.receiveSignal(Signal.BORDER) == Signal.DETECTED) {
            try {
                if (!rc.onTheMap(myLocation, myBodyRadius + 2)) {
                    repulsionDirections.add(Util.getClosestBorder());
                }
            } catch (Exception e) {
                Debug.out("Get Repulsive Directions Exception");
                e.printStackTrace();
            }
        }

        return repulsionDirections;
    }

    private static ArrayList<Direction> getAttractiveDirections(float attractionMin) {
        ArrayList<Direction> attractionDirections = new ArrayList<>();

        for(RobotInfo robot: nearbyRobots) {
            MapLocation robotLocation = robot.getLocation();
            if (myLocation.distanceTo(robotLocation) >= attractionMin + (robot.getRadius()*2))
                attractionDirections.add(new Direction(myLocation, robotLocation));
        }

        for(TreeInfo tree: nearbyTrees) {
            MapLocation treeLocation = tree.getLocation();
            if (myLocation.distanceTo(treeLocation) <= (attractionMin + (tree.getRadius()*2)))
                attractionDirections.add(new Direction(myLocation, treeLocation));
        }

        return attractionDirections;
    }

    public static Direction computeResultantDirection(ArrayList<Direction> directionList){
        if(directionList.size() == 0)
            return null;

        Direction result = directionList.get(0);
        directionList.remove(0);

        for(Direction direction: directionList){
            float x1 = result.getDeltaX(1);
            float y1 = result.getDeltaY(1);
            float x2 = direction.getDeltaX(1);
            float y2 = direction.getDeltaY(1);

            result = new Direction((x1 + x2), (y1 + y2));
        }

        return result;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }

    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    public static float getMaxMoveDistance(Direction direction){
        float stride = myStrideRadius;
        int maxPrecision = 4;
        int precisionCounter = 0;

        while(!rc.canMove(direction, stride) && precisionCounter++ < maxPrecision)
            stride /= 2;

        return stride;
    }
}
