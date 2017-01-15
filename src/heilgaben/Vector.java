package heilgaben;

import heilgaben.*;
import battlecode.common.*;

public class Vector {
    public final float magnitude;
    public final Direction direction;

    Vector(float dx, float dy){
        this.direction = new Direction(dx, dy);
        this.magnitude = (float)Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
    }

    Vector(Direction direction, float magnitude){
        this.direction = direction;
        this.magnitude = magnitude;
    }

    Vector(MapLocation v1, MapLocation v2){
        this.direction = new Direction(v1, v2);
        this.magnitude = Util.getDistance(v1, v2);
    }

    public Vector rotateLeftDegrees(float degrees){
        return new Vector(this.direction.rotateLeftDegrees(degrees), this.magnitude);
    }

    public Vector rotateRightDegrees(float degrees){
        return new Vector(this.direction.rotateRightDegrees(degrees), this.magnitude);
    }

    public float getDeltaX(){
        return this.direction.getDeltaX(this.magnitude);
    }

    public float getDeltaY(){
        return this.direction.getDeltaY(this.magnitude);
    }
}
