import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class Rakete {

    public static void main(String[] args) {
        Instant instant = Instant.ofEpochMilli(1525132799999L);

        System.out.println(new Date(1522540800000L));
        System.out.println(instant.atZone(ZoneId.systemDefault()));
        System.out.println(instant.atOffset(ZoneOffset.UTC));
        System.out.println(instant.atZone(ZoneId.of("UTC")));

        System.out.println(1522540800000L);
        System.out.println(LocalDate.of(2018, 4, 1).atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli());
        System.out.println(LocalDate.of(2018, 4, 30).plusDays(1).atStartOfDay(ZoneId.of("UTC")).minusNanos(1).toInstant().toEpochMilli());
    }

}
