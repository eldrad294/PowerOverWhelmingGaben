package heilgaben.Actions;

import heilgaben.State;

public class ConditionState {
    public Condition condition;
    public State nextState;

    public ConditionState(Condition condition, State state){
        this.condition = condition;
        this.nextState = state;
    }
}
