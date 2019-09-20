package CglibRegisterToSpring.service.impl2;

import org.springframework.stereotype.Component;

import CglibRegisterToSpring.annotation.DataSourceComponent;
import CglibRegisterToSpring.enums.DatasourceEnum;
import CglibRegisterToSpring.service.TestService;

@DataSourceComponent(registerBean = true,DataSource=DatasourceEnum.DB1)
public class TestImpl2 implements TestService{

	@Override
	public String test1(String a) {
		System.out.println("TestImpl2:" + a );
		return a;
	}
	public String test2(String a) {
		System.out.println("TestImpl2:" + a);
		return a;
	}
}
