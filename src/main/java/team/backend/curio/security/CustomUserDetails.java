package team.backend.curio.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import team.backend.curio.domain.users;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final users user;

    public CustomUserDetails(users user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public users getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 보통 로그인 시 기준이 되는 이메일이나 닉네임을 리턴
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인만 쓴다면 null 또는 빈 문자열 처리
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한이 필요한 경우 이렇게
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // 그 외 true 반환
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

}

