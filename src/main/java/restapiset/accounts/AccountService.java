package restapiset.accounts;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

  private final AccountRepository accountRepository;

  public AccountService(
      AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Account> optAccount = accountRepository.findByUsername(username);
    Account account = optAccount
        .orElseThrow(() -> new UsernameNotFoundException("유저 정보가 존재하지 않습니다."));
    return new User(account.getUsername(), account.getPassword(), authorities(account.getRoles()));
  }

  private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
    return roles.stream().map(r -> new SimpleGrantedAuthority("_ROLE"+r.name())).collect(Collectors.toSet());
  }
}
