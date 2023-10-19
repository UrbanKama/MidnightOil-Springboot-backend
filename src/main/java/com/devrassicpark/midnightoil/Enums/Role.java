package com.devrassicpark.midnightoil.Enums;

import com.devrassicpark.midnightoil.constants.Authorities;

public enum Role {

    ROLE_USER(Authorities.USER_AUTHORITIES),
    ROLE_TEAM_MANAGER(Authorities.TEAM_MANAGER_AUTHORITIES),
    ROLE_STORE_MANAGER(Authorities.STORE_MANAGER_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities){
        this.authorities = authorities;
    }

    public String[] getAuthorities(){
        return authorities;
    }
}
