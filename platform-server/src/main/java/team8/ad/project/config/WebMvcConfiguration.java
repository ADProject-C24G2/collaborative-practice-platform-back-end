package team8.ad.project.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import team8.ad.project.web.SessionUserInterceptor;

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    /* ---------- Knife4j/Swagger ---------- */
    @Bean
    public Docket docket() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Collaborative Practice Platform Interface Documentation")
                .version("1.0")
                .description("Collaborative Practice Platform Interface Documentation")
                .build();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("team8.ad.project.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /* ---------- 静态资源/文档 ---------- */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        // 如需兼容 swagger-ui.html：
        // registry.addResourceHandler("/swagger-ui.html")
        //         .addResourceLocations("classpath:/META-INF/resources/");
    }

    /* ---------- 会话拦截器注册 ---------- */
    @Bean
    public SessionUserInterceptor sessionUserInterceptor() {
        return new SessionUserInterceptor();
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionUserInterceptor())
                .addPathPatterns("/student/**")       // 需要登录的接口
                .excludePathPatterns(
                        "/student/login",            // 登录放行
                        "/error",
                        "/student/register",
                        "student/recommendQuestion",
                        // 文档/静态资源放行
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/swagger-ui.html",
                        "/favicon.ico"
                );
    }
}
