package com.icecoldcode.appengine;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class AppContext extends AnnotationConfigWebApplicationContext {

    public AppContext() {
        register(new Class[] {WebMvcConfig.class});
    }

}
