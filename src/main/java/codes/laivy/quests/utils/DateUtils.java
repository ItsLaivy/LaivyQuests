package codes.laivy.quests.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String getDateAsString(long millis) {
        if (millis < 1000) {
            return "0s";
        }

        long DD = TimeUnit.MILLISECONDS.toDays(millis);
        long HH = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long MM = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long SS = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        StringBuilder builder = new StringBuilder();

        if (Math.floor(DD) != 0) {
            builder.append(DD).append("d");
        }
        if (Math.floor(HH) != 0) {
            builder.append(HH).append("h");
        }
        if (Math.floor(MM) != 0) {
            builder.append(MM).append("m");
        }
        if (Math.floor(SS) != 0) {
            builder.append(SS).append("s");
        }

        return builder.toString();
    }

}
