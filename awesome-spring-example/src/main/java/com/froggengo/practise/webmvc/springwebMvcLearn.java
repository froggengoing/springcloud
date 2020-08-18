package com.froggengo.practise.webmvc;

import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.froggengo.practise.webmvc.arguementResolver.AnnotationUserEntity;
import com.froggengo.practise.webmvc.arguementResolver.MyArguementResolverController;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.*;
import org.springframework.web.servlet.support.WebContentGenerator;

import java.lang.reflect.Method;

public class springwebMvcLearn {


    /**
     * @see org.springframework.web.servlet.DispatcherServlet
     * @see org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
     * @see org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration.DispatcherServletConfiguration
     */
    public void test(){


    }

    /**
     * @see AbstractHandlerMethodMapping#afterPropertiesSet()
     *  @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#initHandlerMethods()
     *      @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#detectHandlerMethods(Object)
     *          @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#getMappingForMethod(Method, Class)
     *              @see RequestMappingHandlerMapping#createRequestMappingInfo(java.lang.reflect.AnnotatedElement)
     *          @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#registerHandlerMethod(Object, Method, Object)
     *              @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping.MappingRegistry#register(Object, Object, Method)//requestMappingInfo、name、method
     *                  @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#createHandlerMethod(Object, Method)
     *                      this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directUrls, name));
     * */
    @Test
    public void testHandlerMethod() throws NoSuchMethodException {
        Class<MyArguementResolverController > aClass = MyArguementResolverController .class;
        Method method = aClass.getMethod("getUseer", AnnotationUserEntity.class);
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        RequestMappingInfo.BuilderConfiguration builderConfiguration = new RequestMappingInfo.BuilderConfiguration();
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(requestMapping.path())//StringValueResolver处理路径
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces());
        builder.options(builderConfiguration).build();
        System.out.println(ReflectionToStringBuilder.toString(builder));
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition(aClass.getName(),new RootBeanDefinition(MyArguementResolverController.class));
        HandlerMethod handlerMethod = new HandlerMethod(aClass.getName(),beanFactory, method);
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#afterPropertiesSet()
     *  @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#getDefaultArgumentResolvers()
     *  @see RequestMappingHandlerAdapter#getDefaultInitBinderArgumentResolvers()
     *  @see RequestMappingHandlerAdapter#getDefaultReturnValueHandlers()
     * @see AbstractHandlerMethodAdapter#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
     *  @see RequestMappingHandlerAdapter#handleInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.web.method.HandlerMethod)
     *  @see RequestMappingHandlerAdapter#invokeHandlerMethod(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.web.method.HandlerMethod)
     *      @see RequestMappingHandlerAdapter#createInvocableHandlerMethod(org.springframework.web.method.HandlerMethod)
     *      @see ServletInvocableHandlerMethod#invokeAndHandle(org.springframework.web.context.request.ServletWebRequest, org.springframework.web.method.support.ModelAndViewContainer, Object...)
     *          @see InvocableHandlerMethod#invokeForRequest(org.springframework.web.context.request.NativeWebRequest, org.springframework.web.method.support.ModelAndViewContainer, Object...)
     *              @see HandlerMethodArgumentResolverComposite#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
     *                  @see DataBinder#convertIfNecessary(Object, Class, org.springframework.core.MethodParameter)
     *              @see InvocableHandlerMethod#doInvoke(Object...)
     *                  <Red>getBridgedMethod().invoke(getBean(), args);</Red>
     *               this.returnValueHandlers.handleReturnValue(returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
     *              @see HandlerMethodReturnValueHandlerComposite#handleReturnValue(Object, org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest)
     *                  @see AbstractMessageConverterMethodProcessor#createOutputMessage(org.springframework.web.context.request.NativeWebRequest)
     *                  @see RequestResponseBodyMethodProcessor#handleReturnValue(Object, org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest)
     *                      @see AbstractGenericHttpMessageConverter#write(Object, java.lang.reflect.Type, org.springframework.http.MediaType, org.springframework.http.HttpOutputMessage)
     *                          @see AbstractJackson2HttpMessageConverter#writeInternal(Object, java.lang.reflect.Type, org.springframework.http.HttpOutputMessage)
     *                           @see MappingJsonFactory
     *                  @see WebContentGenerator#prepareResponse(javax.servlet.http.HttpServletResponse)
     * */
    public  void testHandlerAdapter(){

    }

}
