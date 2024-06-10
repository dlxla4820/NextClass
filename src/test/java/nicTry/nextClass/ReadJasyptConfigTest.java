package nicTry.nextClass;

import com.nextClass.config.JasyptConfigAES;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        initializers = ConfigDataApplicationContextInitializer.class,
        classes = JasyptConfigAES.class)
@EnableEncryptableProperties
public class ReadJasyptConfigTest {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    @Test
    void decryption_config_url() {
        Assertions.assertThat(url).isEqualTo("jdbc:mysql://localhost:3306/next_class?serverTimezone=Asia/Seoul");
        System.out.println("decrypted url: " + url);
    }

    @Test
    void decryption_config_username() {
        Assertions.assertThat(username).isEqualTo("dev_user");
        System.out.println("decrypted username: " + username);
    }


    @Test
    void decryption_config_test() {
        Assertions.assertThat(password).isEqualTo("1q2w3e4r!@");
        System.out.println("decrypted password: " + password);
    }




}
