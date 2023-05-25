package codes.laivy.quests.utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtils {

    public static @NotNull String formatNumber(@NotNull BigDecimal number) {
        String[] suffixes = new String[]{"", "K", "M", "B", "T", "Q", "S", "O", "N", "D"};
        int suffixIndex = 0;
        BigDecimal divisor = new BigDecimal("1000");

        while (number.compareTo(divisor) >= 0 && suffixIndex < suffixes.length - 1) {
            number = number.divide(divisor, RoundingMode.HALF_UP);
            suffixIndex++;
        }

        return number.setScale(2, RoundingMode.HALF_UP) + suffixes[suffixIndex];
    }

    public static @NotNull String formatNumber(double number) {
        return formatNumber(BigDecimal.valueOf(number));
    }

    public static @NotNull String formatNumber(@NotNull String number) {
        return formatNumber(new BigDecimal(number));
    }

}
