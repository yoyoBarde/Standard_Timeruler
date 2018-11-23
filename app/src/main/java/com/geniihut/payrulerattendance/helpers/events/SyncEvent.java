package com.geniihut.payrulerattendance.helpers.events;

/**
 * Created by macmini3 on 10/1/15.
 */
public class SyncEvent {

    private boolean hasStarted;
    private boolean syncInProgress;
    private int max;
    private int progress;

    public SyncEvent() {
    }

    public boolean start() {
        this.hasStarted = true;
        syncInProgress = true;
        return true;
    }

    //returns false if sync not started
    public boolean end() {
        if (hasStarted) {
            syncInProgress = false;
            hasStarted = false;
            return true;
        }
        return false;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isSyncInProgress() {
        return syncInProgress;
    }

    @Override
    public String toString() {
        return String.format("hasStarted:%s syncInProgress:%s max:%d progress:%d", hasStarted, syncInProgress, max, progress);
    }
}
