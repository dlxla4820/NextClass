package nicTry.nextClass;
import com.nextClass.config.JasyptConfigAES;
import org.assertj.core.api.Assertions;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JasyptConfigAES.class)
public class JasyptTest {
    @Autowired
    private StringEncryptor jasyptEncryptor;

    @Test
    void custom_jasypt_test_user() {
        String encrypted = jasyptEncryptor.encrypt("root");
        System.out.println("encrypted: " + encrypted);

        String decrypted = jasyptEncryptor.decrypt(encrypted);
        System.out.println("decrypted: " + decrypted);
        Assertions.assertThat(decrypted).isEqualTo("root");
    }
    @Test
    void custom_jasypt_test_pw() {
        String encrypted = jasyptEncryptor.encrypt("1q2w3e4r!@");
        System.out.println("encrypted: " + encrypted);

        String decrypted = jasyptEncryptor.decrypt(encrypted);
        System.out.println("decrypted: " + decrypted);
        Assertions.assertThat(decrypted).isEqualTo("1q2w3e4r!@");
    }
    @Test
    void custom_jasypt_test_dev_url() {
        String encrypted = jasyptEncryptor.encrypt("jdbc:mysql://localhost:3306/next_class?serverTimezone=Asia/Seoul");
        System.out.println("encrypted: " + encrypted);

        String decrypted = jasyptEncryptor.decrypt(encrypted);
        System.out.println("decrypted: " + decrypted);
        Assertions.assertThat(decrypted).isEqualTo("jdbc:mysql://localhost:3306/next_class?serverTimezone=Asia/Seoul");
    }
}