package mhandharbeni.com.voicetime.alarms.data;

import android.content.Context;

import mhandharbeni.com.voicetime.alarms.Alarm;
import mhandharbeni.com.voicetime.data.SQLiteCursorLoader;

/**
 * Created by Phillip Hsu on 6/28/2016.
 */
public class AlarmsListCursorLoader extends SQLiteCursorLoader<Alarm, AlarmCursor> {
    public static final String ACTION_CHANGE_CONTENT
            = "mhandharbeni.com.voicetime.alarms.data.action.CHANGE_CONTENT";

    public AlarmsListCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected AlarmCursor loadCursor() {
        return new AlarmsTableManager(getContext()).queryItems();
    }

    @Override
    protected String getOnContentChangeAction() {
        return ACTION_CHANGE_CONTENT;
    }
}
