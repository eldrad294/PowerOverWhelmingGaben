package heilgaben;

import static heilgaben.SignalConstants.*;

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
            if(channelX != NO_DATA)
                rc.broadcast(channelX, Float.floatToIntBits(data[0]));
            if(channelY != NO_DATA)
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
            return NO_DATA;
        }
    }

    public static float[] receiveCoordinate(int channelX, int channelY){
        try{
            float[] coord = { Float.intBitsToFloat(rc.readBroadcast(channelX)), Float.intBitsToFloat(rc.readBroadcast(channelY))};
            return coord;
        } catch (Exception e) {
            Debug.out("Receive Coordinate Exception");
            e.printStackTrace();
            float[] coord = {NO_CHANNEL, NO_CHANNEL};
            return coord;
        }
    }

    public static float[] receiveBorders(){
        float[] northWest = Signal.receiveCoordinate(DATA_CHANNEL_X | NORTH_WEST, DATA_CHANNEL_Y | NORTH_WEST);
        float[] southEast = Signal.receiveCoordinate(DATA_CHANNEL_X | SOUTH_EAST, DATA_CHANNEL_Y | SOUTH_EAST);
        float[] borders = {northWest[0], northWest[1], southEast[0], southEast[1]};
        return borders;
    }

    public static boolean isBorderDetected(){
        return Signal.receiveSignal(BORDER) == DETECTED;
    }

    public static boolean isBorderXDetected() {
        return Signal.receiveSignal(BORDER | DATA_CHANNEL_X) == DETECTED;
    }

    public static boolean isBorderYDetected() {
        return Signal.receiveSignal(BORDER | DATA_CHANNEL_Y) == DETECTED;
    }

    public static int getGlobalState() {
        return Signal.receiveSignal(GLOBAL_STATE);
    }
}
