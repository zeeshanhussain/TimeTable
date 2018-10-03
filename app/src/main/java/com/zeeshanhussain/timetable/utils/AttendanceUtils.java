package com.zeeshanhussain.timetable.utils;

public class AttendanceUtils {

    public static float percentage(int attended, int total) {
        return ((float) attended) / total * 100;
    }

    /**
     * Calculates the number of lectures that has to be attended or can be bunked in order to
     * maintain the target attendance percentage
     *
     * @param attendedLectures The number of lectures that have been attended
     * @param totalLectures    The total number of lectures that have been taken
     * @param targetAttendance The attendance percentage that has to be maintained
     * @return The number of lectures that can be skipped, or has to be attended. <br>
     *     <ul>
     *         <li><0 for number of lectures that can be bunked</li>
     *         <li>>0 for number of lectures to be attended</li>
     *         <li>=0 if target attendance has been reached</li>
     *     </ul>
     */
    public static int attendanceTargetOffset(int attendedLectures, int totalLectures,
                                             int targetAttendance) {
        float percentage;
        if (totalLectures == 0) {
            return 0;
        } else {
            percentage = percentage(attendedLectures, totalLectures);
        }

        if (percentage == targetAttendance) {
            return 0;
        } else if (percentage > targetAttendance) {
            int bunkNo = (100 * attendedLectures
                    - totalLectures * targetAttendance)
                    / targetAttendance;

            // bunkNo will always be > 0, because of the percentage check above
            return -1 * bunkNo;
        } else {
            int attendNo = (int) Math.ceil((100 * attendedLectures
                    - totalLectures * targetAttendance)
                    / (targetAttendance - 100));

            // attendNo will always be > 0, because of the percentage check above
            return attendNo;
        }
    }
}
