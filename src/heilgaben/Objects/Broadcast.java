package heilgaben.Objects;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

/**
 * Created by eldrad on 1/16/17.
 */
public class Broadcast {

    private static MapLocation mapLocation;
    private static RobotType robotType;
    private static int robotId;

    public Broadcast(MapLocation mapLocation, RobotType robotType, int robotId){
        this.mapLocation = mapLocation;
        this.robotType = robotType;
        this.robotId = robotId;
    }

    public static void setMapLocation(MapLocation iMapLocation){
        mapLocation = iMapLocation;
    }

    public static void setRobotType(RobotType iRobotType){
        robotType = iRobotType;
    }

    public static void setRobotId(int iRobotId){
        robotId = iRobotId;
    }

    public static MapLocation getMapLocation(){
        return mapLocation;
    }

    public static RobotType getRobotType(){
        return robotType;
    }

    public static int getRobotId(){
        return robotId;
    }
}
