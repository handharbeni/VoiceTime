package mhandharbeni.com.voicetime.stopwatch.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import mhandharbeni.com.voicetime.R;
import mhandharbeni.com.voicetime.list.BaseViewHolder;
import mhandharbeni.com.voicetime.stopwatch.Lap;

public class LapViewHolder extends BaseViewHolder<Lap> {

    @BindView(R.id.lap_number) TextView mLapNumber;
    @BindView(R.id.elapsed_time) ChronometerWithMillis mElapsedTime;
    @BindView(R.id.total_time) TextView mTotalTime;

    public LapViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_lap, null);
    }

    @Override
    public void onBind(Lap lap) {
        super.onBind(lap);
        if (getItemViewType() == LapsAdapter.VIEW_TYPE_FIRST_LAP) {
            itemView.setVisibility(View.GONE);
        } else {
            mLapNumber.setText(String.valueOf(lap.getId()));
            bindElapsedTime(lap);
            bindTotalTime(lap);
        }
    }

    private void bindElapsedTime(Lap lap) {
        // In case we're reusing a chronometer instance that could be running:
        // If the lap is not running, this just guarantees the chronometer
        // won't tick, regardless of whether it was running.
        // If the lap is running, we don't care whether the chronometer is
        // also running, because we call start() right after. Stopping it just
        // guarantees that, if it was running, we don't deliver another set of
        // concurrent messages to its handler.
        mElapsedTime.stop();
        // We're going to forget about the + sign in front of the text. I think
        // the 'Elapsed' header column is sufficient to convey what this timer means.
        // (Don't want to figure out a solution)
        mElapsedTime.setDuration(lap.elapsed());
        if (lap.isRunning()) {
            mElapsedTime.start();
        }
    }

    private void bindTotalTime(Lap lap) {
        if (lap.isEnded()) {
            mTotalTime.setVisibility(View.VISIBLE);
            mTotalTime.setText(lap.totalTimeText());
        } else {
            mTotalTime.setVisibility(View.INVISIBLE);
        }
    }
}
