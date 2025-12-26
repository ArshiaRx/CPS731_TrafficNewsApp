package com.trafficnewsapp.scheduler.services;

import java.util.Timer;
import java.util.TimerTask;

/**
 * RefreshScheduler (C07)
 * Business Logic Layer - Auto-refresh functionality
 */
public class RefreshScheduler {
    private Timer timer;
    private long interval; // milliseconds
    private boolean isRunning;
    private Runnable refreshCallback;
    
    public RefreshScheduler(Runnable refreshCallback, long interval) {
        this.refreshCallback = refreshCallback;
        this.interval = interval;
        this.isRunning = false;
    }
    
    public RefreshScheduler(Runnable refreshCallback) {
        this(refreshCallback, 30000); // Default 30 seconds
    }
    
    /**
     * Start auto-refresh
     * @return true if started successfully
     */
    public boolean start() {
        if (isRunning) {
            return false;
        }
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            }
        }, 0, interval);
        
        isRunning = true;
        return true;
    }
    
    /**
     * Stop auto-refresh
     * @return true if stopped successfully
     */
    public boolean stop() {
        if (!isRunning) {
            return false;
        }
        
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        
        isRunning = false;
        return true;
    }
    
    /**
     * Set refresh interval
     * @param interval Interval in milliseconds
     * @return true if set successfully
     */
    public boolean setInterval(long interval) {
        if (interval < 5000) {
            System.err.println("Refresh interval too short, minimum is 5000ms");
            return false;
        }
        
        this.interval = interval;
        
        // Restart if running
        if (isRunning) {
            stop();
            start();
        }
        
        return true;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
}













