package CglibRegisterToSpring.service.impl;

import CglibRegisterToSpring.annotation.DataSourceComponent;
import CglibRegisterToSpring.enums.DatasourceEnum;
import CglibRegisterToSpring.service.TestService;

@DataSourceComponent(DataSource=DatasourceEnum.DB2,registerBean=true)
public class TestImpl3 implements TestService{

	@Override
	public String test1(String a) {
		System.out.println("TestImpl3:" + a);
		return a;
	}
	public String test2(String a) {
		System.out.println("TestImpl3:" + a);
		return a;
	}
}
