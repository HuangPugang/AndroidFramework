package org.hpdroid.base.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by paul on 16/10/22.
 */

public class CountDownButton extends TextView {
    private static final int DEFAULT_MAX_COUNT = 60;
    private TimerTask mTimerTask;
    private int mMaxCount = DEFAULT_MAX_COUNT;

    public CountDownButton(Context context) {
        super(context);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxCount(int maxCount) {
        this.mMaxCount = maxCount;
    }

    public int getMaxCount() {
        return mMaxCount;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void startTimer() {
        if (mTimerTask == null || mTimerTask.getStatus() == AsyncTask.Status.FINISHED) {
            mTimerTask = new TimerTask(this, mMaxCount);
            mTimerTask.execute();
        }
    }

    public void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel(true);
            mTimerTask = null;
        }
    }

    private static final class TimerTask extends AsyncTask<Void, Integer, Void> {
        private WeakReference<CountDownButton> timerButton;
        private int count;

        public TimerTask(CountDownButton timerButton, int count) {
            this.timerButton = new WeakReference<>(timerButton);
            this.count = count;
        }

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(this.count);
            while (!isCancelled() && this.count > 0) {
                try {
                    Thread.sleep(1000);
                    this.count--;
                    publishProgress(this.count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int value = values[0].intValue();
            CountDownButton button = timerButton.get();
            if (button == null) return;
            if (value <= 0) {
                button.setText("重新获取");
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
                button.setText(String.valueOf(values[0].intValue()) + "s后重新发送");
            }
        }
    }
}
