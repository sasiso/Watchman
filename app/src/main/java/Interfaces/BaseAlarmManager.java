package Interfaces;

/**
 * Created by sss on 10-Feb-17.
 */

public interface BaseAlarmManager {
    enum  FailureType{ Breach, InternalError}
    public void raiseAlarm(final FailureType type, final int threshold);
}


