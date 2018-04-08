package mhandharbeni.com.voicetime.stopwatch.data;

import android.content.Context;

import mhandharbeni.com.voicetime.data.SQLiteCursorLoader;
import mhandharbeni.com.voicetime.stopwatch.Lap;

public class LapsCursorLoader extends SQLiteCursorLoader<Lap, LapCursor> {
    public static final String ACTION_CHANGE_CONTENT
            = "mhandharbeni.com.voicetime.stopwatch.data.action.CHANGE_CONTENT";

    public LapsCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected LapCursor loadCursor() {
        return new LapsTableManager(getContext()).queryItems();
    }

    @Override
    protected String getOnContentChangeAction() {
        return ACTION_CHANGE_CONTENT;
    }
}
