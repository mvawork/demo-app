package ru.menshevva.demoapp.security.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.security.AppOidcUser;
import ru.menshevva.demoapp.security.AppUser;
import ru.menshevva.demoapp.security.dto.UserData;
import ru.menshevva.demoapp.security.entities.*;
import ru.menshevva.demoapp.security.entities.UserEntity;
import ru.menshevva.demoapp.security.entities.UserRoleEntity;
import ru.menshevva.demoapp.security.service.SecurityService;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private static final String ANONYMOUS = "Anonymous";

    @PersistenceContext
    private EntityManager em;

    private Optional<UserData> getUserInfo(String userLogin) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(UserEntity.class);
        return em.createQuery(cq.multiselect(root.get(UserEntity_.userId).alias(UserEntity_.USER_ID),
                                root.get(UserEntity_.userLogin).alias(UserEntity_.USER_LOGIN),
                                root.get(UserEntity_.userName).alias(UserEntity_.USER_NAME),
                                root.get(UserEntity_.userPassword).alias(UserEntity_.USER_PASSWORD)
                        )
                        .where(cb.equal(cb.lower(root.get(UserEntity_.userLogin)), userLogin.toLowerCase())))
                .getResultList()
                .stream()
                .map(v -> UserData.builder()
                        .userId(v.get(UserEntity_.USER_ID, UserEntity_.userId.getJavaType()))
                        .userLogin(v.get(UserEntity_.USER_LOGIN, UserEntity_.userLogin.getJavaType()))
                        .userName(v.get(UserEntity_.USER_NAME, UserEntity_.userName.getJavaType()))
                        .userPassword(v.get(UserEntity_.USER_PASSWORD, UserEntity_.userPassword.getJavaType()))
                        .build())
                .findFirst();
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities(BigInteger userId) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(UserRoleEntity.class);
        var joinRole = root.join(UserRoleEntity_.role);
        var joinPrivilege = joinRole.join(RoleEntity_.privileges, JoinType.LEFT);
        cq.multiselect(
                joinRole.get(RoleEntity_.roleName).alias(RoleEntity_.ROLE_NAME),
                joinPrivilege.get(PrivilegeEntity_.privilegeName).alias(PrivilegeEntity_.PRIVILEGE_NAME)
        );
        cq.where(cb.equal(root.get(UserRoleEntity_.userId), userId));
        var l = em.createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.groupingBy(v -> v.get(RoleEntity_.ROLE_NAME, RoleEntity_.roleName.getJavaType()),
                        Collectors.mapping(v -> v.get(PrivilegeEntity_.PRIVILEGE_NAME, PrivilegeEntity_.privilegeName.getJavaType()), Collectors.toSet()))
                );
        var authorities = new HashSet<GrantedAuthority>();
        for (var v : l.entrySet()) {
            authorities.add(new SimpleGrantedAuthority(v.getKey()));
            authorities.addAll(l.get(v.getKey())
                    .stream()
                    .filter(Objects::nonNull)
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        }
        return authorities;
    }

    @Override
    public AppOidcUser getOidcUser(OidcUser oidcUser) {
        return getUserInfo(oidcUser.getName())
                .map(v -> new AppOidcUser(getUserAuthorities(v.getUserId()), oidcUser.getIdToken(), oidcUser.getUserInfo(), v))
                .orElse(new AppOidcUser(Collections.emptyList(), oidcUser.getIdToken(), oidcUser.getUserInfo(),
                        UserData.builder()
                                .userName(ANONYMOUS)
                                .build()));
    }

    @Override
    public User findByUsername(String username) {
        return getUserInfo(username)
                .map(v -> {
                            var appUser = new AppUser(v.getUserName(), v.getUserPassword(), getUserAuthorities(v.getUserId()), v);
                            return appUser;
                        }
                )
                .orElse(null);
    }
}
