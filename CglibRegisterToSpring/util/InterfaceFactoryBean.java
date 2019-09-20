package CglibRegisterToSpring.util;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.Scanner;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;

import CglibRegisterToSpring.annotation.DataSourceComponent;
import CglibRegisterToSpring.enums.DatasourceEnum;
import DynamicProxy.CglibDynamicProxy.Intercepter.MyMethodIntercepter;
import DynamicProxy.interfaces.HelloService;
import DynamicProxy.interfaces.HelloServiceImpl;
import lombok.Data;
@Data
public class InterfaceFactoryBean<T>  implements FactoryBean<T>{

	   private Class<T> interfaceClass;

	   private Enum value;
	   
	    /**
	     * 新建bean
	     * @return
	     * @throws Exception
	     */
	    @Override
	    public T getObject() throws Exception {
	    	 // 检查 h 不为空，否则抛异常
	        Objects.requireNonNull(interfaceClass);
	        
	        return (T) Enhancer.create(interfaceClass,new DymicInvocationHandler());
	    }

	    /**
	     * 获取bean
	     * @return
	     */
	    @Override
	    public Class<?> getObjectType() {
	        return interfaceClass;
	    }

	    @Override
	    public boolean isSingleton() {
	        return true;
	    }
}
