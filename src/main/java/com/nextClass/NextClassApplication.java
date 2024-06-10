package nicTry.nextClass;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class NextClassApplication {

	public static void main(String[] args) {
		SpringApplication.run(NextClassApplication.class, args);
	}

}
