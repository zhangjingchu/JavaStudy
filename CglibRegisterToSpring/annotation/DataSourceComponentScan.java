package CglibRegisterToSpring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan.Filter;

import CglibRegisterToSpring.register.BeanDefinitionRegistrar;

import org.springframework.context.annotation.Import;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(BeanDefinitionRegistrar.class)
public @interface DataSourceComponentScan {

    /**
     * @return
     */
    String[] value() default {};

    /**
     * 扫描包
     *
     * @return
     */
    String[] basePackages() default {};

    /**
     * 扫描的基类
     *
     * @return
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 包含过滤器
     *
     * @return
     */
    Filter[] includeFilters() default {};

    /**
     * 排斥过滤器
     *
     * @return
     */
    Filter[] excludeFilters() default {};
    
}
