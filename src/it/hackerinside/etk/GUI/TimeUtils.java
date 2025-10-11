package it.hackerinside.etk.GUI;

import java.util.concurrent.TimeUnit;


public class TimeUtils {
    /**
     * Calculates the elapsed time between two given timestamps
     * and formats it as a human-readable string. The time will be displayed
     * in the most appropriate unit (milliseconds, seconds, minutes, hours, or days).
     * 
     * <p>The resulting format adapts to the elapsed time:
     * <ul>
     *     <li>Milliseconds are shown when the time is less than a second.</li>
     *     <li>Seconds are shown when the time is less than a minute.</li>
     *     <li>Minutes and seconds are shown when the time is less than an hour.</li>
     *     <li>Hours, minutes, and seconds are shown when the time is less than a day.</li>
     *     <li>Days, hours, minutes, and seconds are shown for durations spanning more than a day.</li>
     * </ul>
     */
	public static String formatElapsedTime(long start, long finish) {
        long durationInMillis = finish - start;
        
        if (durationInMillis < 0) {
            throw new IllegalArgumentException("Finish time must be after start time");
        }

        // Calculate various time intervals
        long millis = durationInMillis % 1000;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis) % 24;
        long days = TimeUnit.MILLISECONDS.toDays(durationInMillis);

        StringBuilder result = new StringBuilder();

        // Add time units in descending order
        if (days > 0) {
            result.append(days).append("d ");
        }
        if (hours > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0) {
            result.append(minutes).append("m ");
        }
        if (seconds > 0) {
            result.append(seconds).append("s ");
        }
        if (millis > 0 || (days == 0 && hours == 0 && minutes == 0 && seconds == 0)) {
            result.append(millis).append("ms");
        }

        // Remove trailing spaces and return the result
        return result.toString().trim();
    }
}
