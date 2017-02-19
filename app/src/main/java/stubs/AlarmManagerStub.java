package stubs;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import Interfaces.BaseAlarmManager;

/**
 * Created by satbir on 12/02/17.
 */

public class AlarmManagerStub  implements BaseAlarmManager{
    private Activity mActivity;
    public AlarmManagerStub(Activity activity){
        mActivity = activity;

    }
    @Override
    public void raiseAlarm(FailureType type, int threshold) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(mActivity, notification);
        r.play();

    }
}
