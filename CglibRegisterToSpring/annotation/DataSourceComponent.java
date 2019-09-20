package CglibRegisterToSpring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import CglibRegisterToSpring.enums.DatasourceEnum;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceComponent {

	/**
	 * 数据源
	 * @return
	 */
	DatasourceEnum DataSource() default DatasourceEnum.DB1;
	
    /**
     * 是否要将标识此注解的类注册为Spring的Bean
     *
     * @return
     */
    boolean registerBean() default false;

}

 
