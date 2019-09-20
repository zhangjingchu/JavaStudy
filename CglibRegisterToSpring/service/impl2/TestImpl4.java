package CglibRegisterToSpring.service.impl2;

import CglibRegisterToSpring.annotation.DataSourceComponent;
import CglibRegisterToSpring.enums.DatasourceEnum;
import CglibRegisterToSpring.service.TestService;
@DataSourceComponent(registerBean = true,DataSource=DatasourceEnum.DB2)
public class TestImpl4 implements TestService{

	@Override
	public String test1(String a) {
		System.out.println("TestImpl4:" + a );
		return a;
	}
	public String test2(String a) {
		System.out.println("TestImpl4:" + a);
		return a;
	}
}
