package com.jsc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class test {

    @Test
    public void test0() throws Exception {
        ObjectMapper om=new ObjectMapper();
        SimpleGrantedAuthority authority3 = new SimpleGrantedAuthority("ROLE_USER");
        SimpleGrantedAuthority authority2 = new SimpleGrantedAuthority("ROLE_ADMIN");
        Set<SimpleGrantedAuthority> set = new HashSet<>();
        set.add(authority3);
        set.add(authority2);
        String authority_JSON = om.writeValueAsString(set);
        System.out.println("authority_JSON = " + authority_JSON);

        Set<SimpleGrantedAuthority> o = om.readValue(authority_JSON, om.getTypeFactory().constructCollectionType(HashSet.class, SimpleGrantedAuthority.class));
        System.out.println("o = " + o);

    }
}