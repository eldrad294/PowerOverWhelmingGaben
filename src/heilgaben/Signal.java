package heilgaben;

import battlecode.common.*;
import heilgaben.Objects.Broadcast;

import java.text.ParseException;

/**
 * Signaling Class
 */
public class Signal extends BotState {


    public static void broadcastSignal(int channel, int report){
        try {
            rc.broadcast(channel, report);
        } catch (Exception e) {
            Debug.out("Signal Exception");
            e.printStackTrace();
        }
    }

    public static void broadcastCoordinate(int channelX, int channelY, float[] data){
        try {
            if(channelX != SignalConstants.NO_DATA)
                rc.broadcast(channelX, Float.floatToIntBits(data[0]));
            if(channelY != SignalConstants.NO_DATA)
                rc.broadcast(channelY, Float.floatToIntBits(data[1]));
        } catch (Exception e) {
            Debug.out("Data Exception");
            e.printStackTrace();
        }
    }

    public static int receiveSignal(int channel){
        try {
            return rc.readBroadcast(channel);
        } catch (Exception e) {
            Debug.out("Receive Signal Exception");
            e.printStackTrace();
            return SignalConstants.NO_DATA;
        }
    }

    public static float[] receiveCoordinate(int channelX, int channelY){
        try{
            float[] coord = { Float.intBitsToFloat(rc.readBroadcast(channelX)), Float.intBitsToFloat(rc.readBroadcast(channelY))};
            return coord;
        } catch (Exception e) {
            Debug.out("Receive Coordinate Exception");
            e.printStackTrace();
            float[] coord = {SignalConstants.NO_CHANNEL, SignalConstants.NO_CHANNEL};
            return coord;
        }
    }

    public static float[] receiveBorders(){
        float[] northWest = Signal.receiveCoordinate(SignalConstants.DATA_CHANNEL_X | SignalConstants.NORTH_WEST, SignalConstants.DATA_CHANNEL_Y | SignalConstants.NORTH_WEST);
        float[] southEast = Signal.receiveCoordinate(SignalConstants.DATA_CHANNEL_X | SignalConstants.SOUTH_EAST, SignalConstants.DATA_CHANNEL_Y | SignalConstants.SOUTH_EAST);
        float[] borders = {northWest[0], northWest[1], southEast[0], southEast[1]};
        return borders;
    }

    public static boolean isBorderDetected(){
        return Signal.receiveSignal(SignalConstants.BORDER) == SignalConstants.DETECTED;
    }

    public static boolean isBorderXDetected() {
        return Signal.receiveSignal(SignalConstants.BORDER | SignalConstants.DATA_CHANNEL_X) == SignalConstants.DETECTED;
    }

    public static boolean isBorderYDetected() {
        return Signal.receiveSignal(SignalConstants.BORDER | SignalConstants.DATA_CHANNEL_Y) == SignalConstants.DETECTED;
    }

    public static int getGlobalState() {
        return Signal.receiveSignal(SignalConstants.GLOBAL_STATE);
    }

    /*
        Channel[0] - xCoordinate
        Channel[1] - yCoordinate
        Channel[2] - Robot Type + Robot Id
    */
    // Encapsulates the broadcast method and stores the necessary information on the broadcast array
    public static boolean sendBroadcast(RobotController rc, RobotInfo robot, int channel) throws ParseException, GameActionException, GameActionException {
        if(rc  != null && robot != null && channel > 0) {
            int xCoordinate = Float.floatToIntBits(robot.location.x);
            int yCoordinate = Float.floatToIntBits(robot.location.y);
            int robotId = robot.getID();
            int robotType = robot.getType().ordinal();

            // Broadcast signals to array
            rc.broadcast(channel, xCoordinate);
            rc.broadcast(channel + 1, yCoordinate);
            rc.broadcast(channel + 2, Integer.parseInt(String.valueOf(robotType) + String.valueOf(robotId)));

            return true;
        }
        return false;
    }

    // Encapsulates the readBroadcast method and returns array content as an object of type Broadcast
    public static Broadcast readBroadcast(RobotController rc, int channel) throws ParseException, GameActionException {
        if (rc != null && channel > 0) {
            //Retrieve signals from neighbouring channels
            int signalInfo1 = rc.readBroadcast(channel);
            int signalInfo2 = rc.readBroadcast(channel+1);
            int signalInfo3 = rc.readBroadcast(channel+2);

            float xCoordinate = Float.intBitsToFloat(signalInfo1);
            float yCoordinate = Float.intBitsToFloat(signalInfo2);
            RobotType robotType = Util.getRobotType(Integer.parseInt(String.valueOf(signalInfo3).substring(0,1)));
            int robotId = Integer.parseInt(String.valueOf(signalInfo3).substring(1,String.valueOf(signalInfo3).length()));

            //Place returned information inside wrapper object of type: Broadcast
            return new Broadcast(new MapLocation(xCoordinate, yCoordinate), robotType, robotId);
        }
        return null;
    }
}
