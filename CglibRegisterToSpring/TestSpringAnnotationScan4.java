package CglibRegisterToSpring;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import CglibRegisterToSpring.annotation.DataSourceComponentScan;

@SpringBootApplication
@DataSourceComponentScan(basePackages="CglibRegisterToSpring.service")
public class TestSpringAnnotationScan4 {
 
	
	 public static void main(String[] args) {
		 SpringApplication app = new SpringApplication(TestSpringAnnotationScan4.class);

		
	     ConfigurableApplicationContext run = app.run(args);
	      
	}
}
