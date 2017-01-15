package heilgaben;

import battlecode.common.*;

import java.util.ArrayList;

public class Util extends BotState {
    public static Direction getRobotSpawnDirection(RobotType robotType) {
        ArrayList<Direction> spawnDirections = getSpawnableDirections(1);

        try {
            for (Direction spawnDirection: spawnDirections) {
                if(robotType == RobotType.GARDENER) {
                    if (rc.canHireGardener(spawnDirection))
                        return spawnDirection;
                }
                else {
                    if(rc.canBuildRobot(robotType, spawnDirection))
                        return spawnDirection;
                }
            }
        } catch (Exception e){
            Debug.out("Get Spawn Direction Exception");
            e.printStackTrace();
        }

        return null;
    }

    private static float getRadialSpawnInterval(float spawnObjectRadius) {
        float radius = myBodyRadius + spawnObjectRadius;
        double perimeter = 2 * Math.PI * radius;
        int spawnCount = (int) (Math.floor(perimeter) / (spawnObjectRadius * 2));
        return (float) (Math.PI * 2) / spawnCount;
    }

    public static ArrayList<Direction> getSpawnableDirections(float spawnObjectRadius){
        float interval = getRadialSpawnInterval(spawnObjectRadius);

        ArrayList<Direction> spawnableDirections = new ArrayList<>();

        try {
            for (float radians = 0; radians < Math.PI * 2; radians += interval) {
                Direction spawnDirection = new Direction(radians);
                if (!rc.isCircleOccupied(myLocation.add(spawnDirection, myBodyRadius + 1), spawnObjectRadius)) {
                    spawnableDirections.add(spawnDirection);
                }
            }
        } catch (Exception e) {
            Debug.out("Spawnable Directions ExceptioN");
            e.printStackTrace();
        }

        return spawnableDirections;
    }

    public static void initCenter() {
        if(Signal.receiveSignal(Signal.CENTER) == Signal.DETECTED) {
            float[] centerArray = Signal.receiveCoordinate(Signal.CENTER | Signal.DATA_CHANNEL_X, Signal.CENTER | Signal.DATA_CHANNEL_Y);
            center = new MapLocation(centerArray[0], centerArray[1]);

            return;
        }

        int xavg = 0, yavg = 0;

        for (int i = 0; i < ourStartingLocations.length; i++) {
            xavg += ourStartingLocations[i].x;
            yavg += ourStartingLocations[i].y;

            xavg += enemyStartingLocations[i].x;
            yavg += enemyStartingLocations[i].y;
        }

        center = new MapLocation(Math.round(xavg / (ourStartingLocations.length + enemyStartingLocations.length)), Math.round(yavg / (ourStartingLocations.length + enemyStartingLocations.length)));
        float[] centerArray = {center.x, center.y};
        Signal.broadcastCoordinate(Signal.CENTER | Signal.DATA_CHANNEL_X, Signal.CENTER | Signal.DATA_CHANNEL_Y, centerArray);
        Signal.broadcastSignal(Signal.CENTER, Signal.DETECTED);
    }

    public static Direction getClosestBorder(){
        float west = myLocation.x - border[0];
        float north = border[1] - myLocation.y;
        float east = border[2] - myLocation.x;
        float south = myLocation.y - border[3];

        float closest = west;
        Direction direction = new Direction((float)Math.PI);

        if(closest > east) {
            closest = east;
            direction = new Direction(0);
        }

        if(closest > north){
            closest = north;
            direction = new Direction((float)Math.PI/2);
        }

        if(closest > south){
            direction = new Direction((float)(3 * (Math.PI/2)));
        }

        try {
            //rc.setIndicatorLine(myLocation, myLocation.add(direction, closest), 255, 255, 0);
        } catch (Exception e){

        }

        return direction;
    }

    public static void initBorders() {
        if(Signal.receiveSignal(Signal.BORDER) == Signal.DETECTED) {
            border = Signal.receiveBorders();
        }
    }

    public static void updateBorders(){
        if(Signal.receiveSignal(Signal.BORDER) != Signal.DETECTED)
            return;

        border = Signal.receiveBorders();

        // WEST
        if(myLocation.x < border[0]) {
            float[] westEast = {0, 0};
            westEast[0] = myLocation.x - myBodyRadius;
            westEast[1] = center.x + (center.x - westEast[0]) + (2*myBodyRadius);

            Signal.broadcastCoordinate(Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, westEast);
            return;
        }

        // NORTH
        if(myLocation.y > border[1]){
            float[] northSouth = {0, 0};

            northSouth[0] = myLocation.y + myBodyRadius;
            northSouth[1] = center.y - (northSouth[1] - center.y) - (2*myBodyRadius);

            Signal.broadcastCoordinate(Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST, Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST, northSouth);
            return;
        }

        // EAST
        if(myLocation.x > border[2]) {
            float[] westEast = {0, 0};

            westEast[1] = myLocation.x + myBodyRadius;
            westEast[0] = center.x - (westEast[1] - center.x) - (2*myBodyRadius);

            Signal.broadcastCoordinate(Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, westEast);
            return;
        }

        // SOUTH
        if( myLocation.y < border[3]) {
            float[] northSouth = {0, 0};

            northSouth[1] = myLocation.y - myBodyRadius;
            northSouth[0] = center.y + (center.y - northSouth[1]) + (2*myBodyRadius);

            Signal.broadcastCoordinate(Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST, Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST, northSouth);
            return;
        }

        try {
            rc.setIndicatorDot(new MapLocation(border[0], border[1]), 255, 255, 0);
            rc.setIndicatorDot(new MapLocation(border[2], border[3]), 255, 255, 0);
        } catch (Exception e){
            Debug.out("Indicator Dot Exception: (" + border[0] + ", " + border[1] + ") - (" + border[2] + ", " + border[3] + ")");
            e.printStackTrace();
        }
    }

    public static float getDistance(MapLocation v1, MapLocation v2){
        return (float)Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2));
    }

    public static TreeInfo getClosestNonemptyBulletTree(){
        TreeInfo closestBulletTree = null;
        for(TreeInfo tree : nearbyTrees){
            if(tree.getContainedBullets() > 0)
                if(closestBulletTree == null || myLocation.distanceTo(tree.location) < myLocation.distanceTo(closestBulletTree.location))
                    closestBulletTree = tree;
        }

        return closestBulletTree;
    }
}
