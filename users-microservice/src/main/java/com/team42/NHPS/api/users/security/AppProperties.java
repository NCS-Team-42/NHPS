package com.team42.NHPS.api.users.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Autowired
    private Environment env;

    public String getPropertry(String key) {
        return env.getProperty(key);
    }
}
