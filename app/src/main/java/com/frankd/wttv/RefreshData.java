package com.frankd.wttv;

/**
 * Created by Tom on 9/7/15.
 */
public class RefreshData {
    public interface RefreshDataListener {
        public void onQueueEmpty();

        public void onQueueNotEmpty();
    }

    private RefreshDataListener listener;

    public RefreshData() {
        this.listener = null;
    }

    public void setRequestQueueNotifierListener(RefreshDataListener listener) {
        this.listener = listener;
    }
}
