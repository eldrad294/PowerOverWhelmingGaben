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
            for (float radians = 0; radians < ((Math.PI * 2) - interval); radians += interval) {
                Direction spawnDirection = new Direction(radians);
                rc.setIndicatorDot(myLocation.add(spawnDirection, myBodyRadius + 1), 255, 255, 255);
                if (!rc.isCircleOccupied(myLocation.add(spawnDirection, myBodyRadius + 1), spawnObjectRadius - 0.5f)) {
                    spawnableDirections.add(spawnDirection);
                }
            }
        } catch (Exception e) {
            Debug.out("Spawnable Directions Exception");
            e.printStackTrace();
        }

        return spawnableDirections;
    }


    public static float getDistance(MapLocation v1, MapLocation v2){
        return (float)Math.sqrt(Math.pow(v1.x - v2.x, 2) + Math.pow(v1.y - v2.y, 2));
    }
}
