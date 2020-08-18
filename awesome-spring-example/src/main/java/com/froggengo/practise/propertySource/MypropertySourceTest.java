package com.froggengo.practise.propertySource;

import org.junit.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;

public class MypropertySourceTest {
    /**test
     StandardServletEnvironment默认的propertysource，一般会有系统变量等
     servletConfigInitParams
     servletContextInitParams
     systemProperties：系统变量
     systemEnvironment：系统环境
     */
    @Test
    public void testDefaultStandardServletEnvironment(){
        StandardServletEnvironment environment = new StandardServletEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        StreamSupport.stream(propertySources.spliterator(),false).forEach(n->{
            System.out.println(n.getName());
        });
    }
    /**
     解析application.yml
     以---为分界，获得List<PropertySource<?>>，实际封装了Map<String,object>键值对
     */
    @Test
    public void testParseYml() throws IOException {
        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        ClassPathResource resource = new ClassPathResource("application.yml");
        String name = "applicationConfig: [classpath:/application.yml]";
        List<PropertySource<?>> load = yamlPropertySourceLoader.load(name, resource);
        load.forEach(propertySource->{
            System.out.println(propertySource.getName());
            Map<String,Object> source = (Map<String, Object>) propertySource.getSource();
            source.forEach((k,v)-> System.out.println("   "+k+"="+v));
        });

    }
    /**
     问题：通过Binder无法获取json中设置的spring.profiles.active值
     模拟解析spring.profiles.active和解析json格式的命令行参数参数
     * */

    @Test
    public void testJsonBinder() throws IOException {
        //1、模拟传入参数
        System.setProperty("spring.profiles.active","jvmprofile");
        String[] args={"--spring.profiles.active=dev ","--spring.profiles.active=secProfile",
                "--spring.application.json={\"spring.profiles.active\":\"jsonProfile\"}" };
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        SimpleCommandLinePropertySource simpleCommandLinePropertySource = new SimpleCommandLinePropertySource(applicationArguments.getSourceArgs());
        StandardServletEnvironment environment = new StandardServletEnvironment();
        //2、解析命令行参数，并添加至environment第一位
        //environment.getActiveProfiles();
        environment.getPropertySources().addFirst(simpleCommandLinePropertySource);
        SpringApplicationJsonEnvironmentPostProcessor jsonEnvironmentPostProcessor = new SpringApplicationJsonEnvironmentPostProcessor();
        //3、解析spring.application.json的参数，并添加至environment的systemProperties前面
        jsonEnvironmentPostProcessor.postProcessEnvironment(environment,null);
        //4、判断当前environment中的环境变量,源码实例写法
        String[] profiles = Binder.get(environment).bind("spring.profiles.active", String[].class).get();
        Arrays.stream(profiles).forEach(n-> System.out.println(n));
        //5、以下为等价写法，findFirst决定了environment的propertySouceList的次序决定了优先级
        StreamSupport.stream(environment.getPropertySources().spliterator(),false)
                .map(n->n.getProperty("spring.profiles.active"))
                .filter(Objects::nonNull)
                .findFirst().get();
        //6、以下是，spring解析完命令行参数后马上会获取环境变量的方式，
        // 注意到即使已经解析了spring.application.json，还是没有作为有效profile，同样是找到第一组（注意是组）就返回
        PropertySourcesPropertyResolver propertySourcesPropertyResolver = new PropertySourcesPropertyResolver(environment.getPropertySources());
        String property = propertySourcesPropertyResolver.getProperty("spring.profiles.active");//找到第一组就返回
        System.out.println("用命令行直接初始化"+property);
        //7、注意到systemproperties优先级低于json
        //文件入库先考虑使用findfirst()的方式获取环境变量，
        // 再考虑environment中已存在的环境变量
    }

    /**
     获取配置文件的属性值，比如spring.profiles.active
     实际启动过程，将 PropertySource、spring.profiles.active/include、spring.profiles 封装成一个Document
     */
    @Test
    public void testBindProfile() throws IOException {
        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        ClassPathResource resource = new ClassPathResource("application.yml");
        String name = "applicationConfig: [classpath:/application.yml]";
        List<PropertySource<?>> load = yamlPropertySourceLoader.load(name, resource);
        StandardServletEnvironment environment = new StandardServletEnvironment();
        load.forEach(propertySource->{
            System.out.println("属性资源："+propertySource.getName());
            Binder binder = new Binder(
                    ConfigurationPropertySources.from(propertySource),
                    new PropertySourcesPlaceholdersResolver(environment));
            Set<String> strings = binder.bind("spring.profiles.active", Bindable.of(String[].class))
                    .map((String[] profileNames) ->{
                        List<String> profiles = new ArrayList<>();
                        for (String profileName : profileNames) {
                            profiles.add(profileName);
                        }
                        Set<String> profileSet = new LinkedHashSet<>(profiles);
                        return profileSet;
                    }).orElse(Collections.emptySet());
            System.out.println("获取Profiles.active");
            strings.forEach(n-> System.out.println(n));
            System.out.println("获取Profiles");
            String[] profiles = binder.bind("spring.profiles", Bindable.of(String[].class))
                    .orElse(null);
            if (profiles != null) {
                Arrays.stream(profiles).forEach(n-> System.out.println(n));
            }


        });
    }
    /**{@link org.springframework.core.env.AbstractEnvironment#doGetActiveProfiles}
     根据判断 document的profile 是否应该加载
     获取有效profile{@link AbstractEnvironment#containsProperty(java.lang.String)}
     1、判断document的prfile[spring.profiles]是否包括当前有效profile
     2、判断environment中是否包含该profile,
     3、判断非profile情况，spring.profiles为!test时，environment的profile不为test即为有效
     相关类  {@link ConfigFileApplicationListener.Loader#getPositiveProfileFilter(org.springframework.boot.context.config.ConfigFileApplicationListener.Profile)}
     {@link org.springframework.core.env.PropertySourcesPropertyResolver#containsProperty(java.lang.String)}
     {@link org.springframework.boot.context.properties.source.ConfigurationPropertySourcesPropertySource#getProperty(java.lang.String)}
     {@link ConfigurationPropertySource}
     {@link SpringConfigurationPropertySource}
     {@link org.springframework.core.env.AbstractEnvironment#acceptsProfiles(java.lang.String...)}
     结论：
     1、同一个application.xml中，只要没有指定spring.profiles,均可以被加载，若指定则必须与active指定的值一样或如!profile，profile与当前一致
     {@link org.springframework.boot.context.config.ConfigFileApplicationListener.Loader#load(org.springframework.boot.env.PropertySourceLoader, java.lang.String, org.springframework.boot.context.config.ConfigFileApplicationListener.Profile, org.springframework.boot.context.config.ConfigFileApplicationListener.DocumentFilter, org.springframework.boot.context.config.ConfigFileApplicationListener.DocumentConsumer)}
     * */
    @Test
    public void testFilter() throws IOException {
        /*解析得到propertysource*/
        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        ClassPathResource resource = new ClassPathResource("application.yml");
        String name = "applicationConfig: [classpath:/application.yml]";
        List<PropertySource<?>> load = yamlPropertySourceLoader.load(name, resource);
        StandardServletEnvironment environment = new StandardServletEnvironment();
        load.forEach(propertySource->{
           System.out.println(propertySource.getName());
            Map<String,Object> source = (Map<String, Object>) propertySource.getSource();
            source.forEach((k,v)-> System.out.println("   "+k+"="+v));
        });
    }
}
