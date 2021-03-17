package restapiset.accounts;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import common.RestDocsConfiguration;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
class AccountServiceTest {

  @Autowired
  private AccountService accountService;
  @Autowired
  private AccountRepository accountRepository;

  @Test
  void loadUser() {
    String username = "klom";
    String password = "1234";
    Account account = Account.builder()
        .username(username)
        .password(password)
        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
        .build();
    accountRepository.save(account);
    UserDetails userDetails = accountService.loadUserByUsername(username);
    assertThat(userDetails.getPassword()).isEqualTo(password);
  }

  @Test
  void loadEmptyUser() {
    assertThrows(
        UsernameNotFoundException.class,
        () -> {
          String username = "klomlly";
          accountService.loadUserByUsername(username);
        }
    );
  }

}