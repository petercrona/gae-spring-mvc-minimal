package com.icecoldcode.appengine;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@EnableWebMvc
@Configuration
@ComponentScan({"com.icecoldcode.appengine"})
public class WebMvcConfig extends WebMvcConfigurerAdapter { }
