package heilgaben;

import battlecode.common.*;

public class Util extends BotState {
    public static Direction getRobotSpawnDirection(RobotType robotType) {
        float interval = getRadialSpawnInterval(robotType.bodyRadius);

        try {
            for (float radians = 0; radians < Math.PI * 2; radians += interval) {
                Direction spawnDirection = new Direction(radians);
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

    public static Direction getPlantDirection() {
        float interval = getRadialSpawnInterval(1);

        try {
            for (float radians = 0; radians < Math.PI * 2; radians += interval) {
                Direction plantDirection = new Direction(radians);
                if(rc.canPlantTree(plantDirection))
                    return plantDirection;
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

    public static void initCenter() {
        int xavg = 0, yavg = 0;

        for (int i = 0; i < ourStartingLocations.length; i++) {
            xavg += ourStartingLocations[i].x;
            yavg += ourStartingLocations[i].y;

            xavg += enemyStartingLocations[i].x;
            yavg += enemyStartingLocations[i].y;
        }

        center = new MapLocation(Math.round(xavg / (ourStartingLocations.length + enemyStartingLocations.length)), Math.round(yavg / (ourStartingLocations.length + enemyStartingLocations.length)));
    }

    // TODO : Take a closer look - make more efficient
    public static Direction getClosestBorder(){
        float west = myLocation.x - border[0];
        float north = myLocation.y - border[1];
        float east = myLocation.x - border[2];
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
            rc.setIndicatorLine(myLocation, myLocation.add(direction), 255, 255, 0);
        } catch (Exception e){

        }

        return direction;
    }

    public static void initBorders() {
        if(Signal.receiveSignal(Signal.BORDER) == Signal.DETECTED) {
            border = Signal.receiveBorders();
        }
    }

    // TODO : Fix this after fixing closestBorder
    public static void updateBorders(){
        if(Signal.receiveSignal(Signal.BORDER) != Signal.DETECTED)
            return;

//        border = Signal.receiveBorders();
//
//        if((int) myLocation.x < border[0]) {
//            border[0] = (int) Math.floor(myLocation.x + myBodyRadius);
//            Signal.broadcastSignal(Signal.DATA_CHANNEL_X | Signal.NORTH_WEST, border[0]);
//        }
//
//        if((int) myLocation.y < border[1]) {
//            border[1] = (int) Math.floor(myLocation.y - myBodyRadius);
//            Signal.broadcastSignal(Signal.DATA_CHANNEL_Y | Signal.NORTH_WEST, border[1]);
//        }
//
//        if((int) myLocation.x > border[2]) {
//            border[2] = (int) Math.floor(myLocation.x - myBodyRadius);
//            Signal.broadcastSignal(Signal.DATA_CHANNEL_X | Signal.SOUTH_EAST, border[2]);
//        }
//
//
//        if((int) myLocation.y > border[3]) {
//            border[3] = (int) Math.floor(myLocation.y + myBodyRadius);
//            Signal.broadcastSignal(Signal.DATA_CHANNEL_Y | Signal.SOUTH_EAST, border[3]);
//        }
//
//        try {
//            rc.setIndicatorDot(new MapLocation(border[0], border[1]), 255, 255, 0);
//            rc.setIndicatorDot(new MapLocation(border[2], border[3]), 255, 255, 0);
//        } catch (Exception e){
//            Debug.out("Indicator Dot Exception: (" + border[0] + ", " + border[1] + ") - (" + border[2] + ", " + border[3] + ")");
//            e.printStackTrace();
//        }
    }

}
