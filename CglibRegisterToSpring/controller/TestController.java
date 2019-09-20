package CglibRegisterToSpring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import CglibRegisterToSpring.service.TestService;
import CglibRegisterToSpring.service.impl.TestImpl;
import CglibRegisterToSpring.service.impl.TestImpl3;
import CglibRegisterToSpring.service.impl2.TestImpl4;

@Controller
public class TestController {

	@Autowired
	@Qualifier("testImpl4")
	private   TestService  testService;
	
	@Autowired
	@Qualifier("testImpl2")
	private   TestService  testService2;
	
	
	@GetMapping("/test")
	@ResponseBody
	public String get() {
	
		
		 testService.test1("hhh");
		 
		 
		 testService2.test1("我是第二个TestImpl");
		 return "success";
	}
	
}
