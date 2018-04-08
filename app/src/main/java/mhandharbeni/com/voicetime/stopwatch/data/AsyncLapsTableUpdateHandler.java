package mhandharbeni.com.voicetime.stopwatch.data;

import android.content.Context;
import android.content.Intent;

import mhandharbeni.com.voicetime.data.AsyncDatabaseTableUpdateHandler;
import mhandharbeni.com.voicetime.list.ScrollHandler;
import mhandharbeni.com.voicetime.stopwatch.Lap;
import mhandharbeni.com.voicetime.stopwatch.StopwatchNotificationService;

/**
 * Created by Phillip Hsu on 8/9/2016.
 */
public class AsyncLapsTableUpdateHandler extends AsyncDatabaseTableUpdateHandler<Lap, LapsTableManager> {

    public AsyncLapsTableUpdateHandler(Context context, ScrollHandler scrollHandler) {
        super(context, scrollHandler);
    }

    @Override
    protected LapsTableManager onCreateTableManager(Context context) {
        return new LapsTableManager(context);
    }

    @Override
    protected void onPostAsyncInsert(Long result, Lap item) {
        if (result > 1) {
            // Update the notification's title with this lap number
            Intent intent = new Intent(getContext(), StopwatchNotificationService.class)
                    .setAction(StopwatchNotificationService.ACTION_UPDATE_LAP_TITLE)
                    .putExtra(StopwatchNotificationService.EXTRA_LAP_NUMBER, result.intValue());
            getContext().startService(intent);
        }
    }

    // ===================== DO NOT IMPLEMENT =========================

    @Override
    protected void onPostAsyncDelete(Integer result, Lap item) {
        // Leave blank.
    }

    @Override
    protected void onPostAsyncUpdate(Long result, Lap item) {
        // Leave blank.
    }
}
