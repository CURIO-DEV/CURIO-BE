package team.backend.curio.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import team.backend.curio.domain.users;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final users user;

    public CustomUserDetails(users user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    // 필요하면 users 클래스에 더 추가 메서드 만들고 여기서 위임 가능

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 정보가 따로 없으면 빈 리스트 반환
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        // 소셜 로그인이라 비밀번호 없으면 null 또는 빈 문자열
        return null;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}