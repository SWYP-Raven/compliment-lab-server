// src/main/java/swypraven/complimentlabserver/global/auth/security/CustomUserDetails.java
package swypraven.complimentlabserver.global.auth.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String appleSub;  // username 으로 사용
    private final String email;
    private final List<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user, List<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.appleSub = user.getAppleSub();
        this.email = user.getEmail();
        this.authorities = authorities;
    }

    public CustomUserDetails(Long id, String appleSub, String email, List<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.appleSub = appleSub;
        this.email = email;
        this.authorities = authorities;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return ""; } // 소셜 로그인이라 미사용
    @Override public String getUsername() { return appleSub; } // 핵심: appleSub를 username으로
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
