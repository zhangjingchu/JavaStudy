package CglibRegisterToSpring.service.impl;

import org.springframework.stereotype.Component;

import CglibRegisterToSpring.service.TestService;
@Component
public class TestImpl implements TestService{

	@Override
	public String test1(String a) {
		System.out.println("TestImpl:" + a);
		return a;
	}
	public String test2(String a) {
		System.out.println("TestImpl:" + a);
		return a;
	}
}
