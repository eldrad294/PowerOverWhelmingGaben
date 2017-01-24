package heilgaben;

import battlecode.common.*;
import com.sun.tools.example.debug.expr.ParseException;

/**
 * Signaling Class
 */
public class Signal extends BotState {


    public static void broadcastSignal(int channel, int report) throws GameActionException{
        if(channel >= 0)
            rc.broadcast(channel, report);
    }

    public static void broadcastCoordinate(int channelX, int channelY, float[] data) throws GameActionException{
        if(channelX != SignalConstants.NO_DATA && channelY != SignalConstants.NO_DATA && data[0] >= 0 && data[1] >= 0) {
            rc.broadcast(channelX, Float.floatToIntBits(data[0]));
            rc.broadcast(channelY, Float.floatToIntBits(data[1]));
        }
    }

    public static int receiveSignal(int channel) throws GameActionException{
        if(channel >= 0)
            return rc.readBroadcast(channel);
        return SignalConstants.NO_DATA;
    }

    public static float[] receiveCoordinate(int channelX, int channelY) throws GameActionException{
        float[] coord = new float[2];
        if (channelX >= 0 && channelY >= 0) {
            coord[0] = Float.intBitsToFloat(rc.readBroadcast(channelX));
            coord[1] = Float.intBitsToFloat(rc.readBroadcast(channelY));
        }else{
            coord[0] = (float)0;
            coord[1] = (float)0;
        }
        return coord;
    }

    public static float[] receiveBorders() throws GameActionException{
        float[] northWest = Signal.receiveCoordinate(SignalConstants.DATA_CHANNEL_X | SignalConstants.NORTH_WEST, SignalConstants.DATA_CHANNEL_Y | SignalConstants.NORTH_WEST);
        float[] southEast = Signal.receiveCoordinate(SignalConstants.DATA_CHANNEL_X | SignalConstants.SOUTH_EAST, SignalConstants.DATA_CHANNEL_Y | SignalConstants.SOUTH_EAST);
        float[] borders = {northWest[0], northWest[1], southEast[0], southEast[1]};
        return borders;
    }

    public static boolean isBorderDetected(){
        try{
            return Signal.receiveSignal(SignalConstants.BORDER) == SignalConstants.DETECTED;
        }catch(GameActionException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isBorderXDetected(){
        try{
            return Signal.receiveSignal(SignalConstants.BORDER | SignalConstants.DATA_CHANNEL_X) == SignalConstants.DETECTED;
        }catch(GameActionException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isBorderYDetected(){
        try{
            return Signal.receiveSignal(SignalConstants.BORDER | SignalConstants.DATA_CHANNEL_Y) == SignalConstants.DETECTED;
        }catch(GameActionException e){
            e.printStackTrace();
        }
        return false;
    }

    public static int getGlobalState() throws GameActionException{
        return Signal.receiveSignal(SignalConstants.GLOBAL_STATE);
    }

}
