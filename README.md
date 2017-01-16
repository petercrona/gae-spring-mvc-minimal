# gae-spring-mvc-minimal
Hey, this is just a minimal Spring MVC (4.2.7) project that runs on App Engine and serve some JSON. To get it running and to deploy it:

1. Install Python 2.7 (probably you have something that works, so try to skip this first)

2. Create a new project or get the project ID of your existing project in the google cloud console (https://console.cloud.google.com/project).

3. Install Google Cloud SDK (https://cloud.google.com/sdk/docs/)

4. Update "src/main/webapp/WEB-INF/appengine-web.xml with your project id (replace YOUR_APP_ID).

5. Run "gradle aR" (or "gradle appengineRun") when in the project's root folder. Check out localhost:8080!

6. Run "gradle aD" (or "gradle appengineDeploy"). Check out YOUR_PROJECT_ID.appspot.com!

## What happens

Start by looking at *src/main/webapp/WEB-INF/web.xml*. This is the main configuration file where you define servlets. 
The servlets can be thought of as the objects getting HTTP-requests and returning writing HTTP-responses. 
It doesn't look like, but could have looked like:

```
function(request, response) {
    response.write('hello');
}
```

This is not all to them, but I hope you get the gist. Now look into *web.xml*, first interesting thing:

```
<servlet>
    <servlet-name>mvc-dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
      <param-name>contextAttribute</param-name>
      <param-value>org.springframework.web.context.WebApplicationContext.ROOT</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
```

We are defining a Servlet (something to handle HTTP-request). Note that the name is unimportant. The class *DispatcherServlet* is responsible to dispatch an 
incoming HTTP-request to the right method. Like a dispatcher or router. Setting *WebApplicationContext.ROOT* is required
when configuring Spring not via XML but programatically using Java. Moving on:

```
<servlet-mapping>
    <servlet-name>mvc-dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

tells which urls our previously defined servlet (name must match) shall handle. This will make it handle all, allowing you to specify for example:

```@RequestMapping(value = "/kalle", method = RequestMethod.GET)```

to handle "example.com/kalle". Next we need to specify which file is our "main" so to say. Where to initialize our app.

```
<context-param>
    <param-name>contextClass</param-name>
    <param-value>com.icecoldcode.appengine.AppContext</param-value>
</context-param>
```

And finally we need to specify how everything is going to be kicked off:

```
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

Let's now jump to the Java code. *AppContext.java* is the first file. In the constructor we just registering another file, namely *WebMvcConfig.java*.

```
register(new Class[] {WebMvcConfig.class});
```

In WebMvcConfig.java we specify that Spring shall find all annotated files in "com.icecoldcode.appengine" 
and all sub packages, eg. "com.icecoldcode.appengine.dog".

```
@EnableWebMvc
@Configuration
@ComponentScan({"com.icecoldcode.appengine"})
public class WebMvcConfig extends WebMvcConfigurerAdapter { }
```

One of the files captured by this is *WelcomeController.java* which
uses the *@RestController* annotation and *@RequestMapping*. The @RestController annotation together with our dependency on Jackson (in build.gradle)
makes Spring automatically return JSON. @RequestMapping is pretty straight forward I guess. The key is that WebMvcConfig.java, loaded
thanks to AppContext.java, allows us to use annotations.

I hope this minimal example helps you to understand Spring MVC a little better. Newer versions, using Spring Boot and/or Servlet 3+
are a lot nicer. So don't use this! In fact, check out cool stuff like Yesod (Haskell) instead. 
Just play with this if you have a legacy project running on App Engine. 

Creating a minimal example and extending it helps you to actually learn as opposed to just being able to produce features.
And remember, understanding something reduces the risk of you getting stuck, reduces the risk of bugs, makes it possible to improve
it and makes it more fun to work with it.

#build.gradle
It's easy to get overwhelmed by how many stuff are happening. If you are used to messy Java programming with classpath issues and just relying on the IDE to get it right, then you probably feel that something complicated is going on. But in fact most of the work is just based on conventions. You need to have a specific folder structure. Then the build just works. Not by magic, but by a fixed folder structure. Let's go through the *build.gradle* file section by section.

Here we are defining dependencies that the build script (as opposed to the application) itself has.
```
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'com.google.cloud.tools:appengine-gradle-plugin:+'
  }
}
```

Here we define repositories for dependencies that are application has.
```
repositories {
  mavenCentral()
}

```

Here we apply plugins provided by gradle. These are assuming that are directory structure is as it is. And they add "tasks" that you can see by running "gradle tasks". One of these tasks are "appengineRun". Next we declare dependencies of our application (not the build script as before).
```
apply plugin: 'war'
apply plugin: 'com.google.cloud.tools.appengine'
```

Quite clear. These are fetched using the *repositories* previously defined. "compile" means needed to compile. There's also for example "runtime" which I didn't use here. That's it! We can configure more things, such as "appengine". Eg.
```
dependencies {
  compile 'javax.servlet:servlet-api:2.5'
  compile 'com.google.appengine:appengine:+'
  compile 'org.springframework:spring-webmvc:4.2.7.RELEASE'
  compile 'com.fasterxml.jackson.core:jackson-databind:2.7.5'
}
```

to configure "appengineDeploy". But it's not necessary.
```
appengine {
  deploy {
    stopPreviousVersion = true
    promote = true
  }
}
```

