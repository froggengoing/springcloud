package com.froggengo.practise.webmvc;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.util.Map;

public class testUriVariables {


    /**
     * @see RequestMappingInfoHandlerMapping#handleMatch(org.springframework.web.servlet.mvc.method.RequestMappingInfo, String, javax.servlet.http.HttpServletRequest)
     * requestMapping处理参数时，先使用AntPathMatcher提取path中的参数值，
     * @see AbstractNamedValueMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
     * 转由 HandlerMethodArgumentResolverComposite 处理参数时使用，比如带有@PathVariable,
     * 负责提取参数值（此时为String类型）,WebDataBinder复制类型转换，可以自定义propertyEditor，或者conversionService
     */
    @Test
    public void getUriVariavles(){
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Map<String, String> map = antPathMatcher.extractUriTemplateVariables("deptno/{deptno}/user/{name}", "deptno/123/user/fly");
        System.out.println(map);
        //request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, bestPattern);
        //UrlPathHelper urlPathHelper = new UrlPathHelper();
        //HttpServletRequest httpServletRequest = new ShiroHttpServletRequest(null,null,false);
        //Map<String, String> stringStringMap1 = urlPathHelper.decodePathVariables(httpServletRequest, stringStringMap);


    }
}
