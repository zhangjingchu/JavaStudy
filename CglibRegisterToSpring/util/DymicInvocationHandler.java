package CglibRegisterToSpring.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.annotation.AnnotationUtils;

import CglibRegisterToSpring.annotation.DataSourceComponent;
import CglibRegisterToSpring.enums.DatasourceEnum;


public class DymicInvocationHandler implements MethodInterceptor{
	
 

	@Override
	public Object intercept(Object sub, Method method, Object[] objects,MethodProxy proxy) throws Throwable {
	
		Class<?> declaringClass = method.getDeclaringClass();
		
		DataSourceComponent declaredAnnotation = AnnotationUtils.findAnnotation(declaringClass, DataSourceComponent.class);
		
		 if(declaredAnnotation != null) {
	        	DatasourceEnum value = declaredAnnotation.DataSource();
	        	if(value == DatasourceEnum.DB1) {
	        		System.out.println("设置数据源:" + value);
	        	}else if(value == DatasourceEnum.DB2) {
	        		System.out.println("设置数据源:" + value);
	        	}
	        	
	        }else {
	        	System.out.println("设置默认数据源  ===  1");
	        }
	      
		
		Object object = proxy.invokeSuper(sub, objects);
		return object;
	}

	 
}
