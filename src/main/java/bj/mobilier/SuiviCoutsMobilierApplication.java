package bj.mobilier;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SuiviCoutsMobilierApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Porto-Novo"));
        SpringApplication.run(SuiviCoutsMobilierApplication.class, args);
    }
}
