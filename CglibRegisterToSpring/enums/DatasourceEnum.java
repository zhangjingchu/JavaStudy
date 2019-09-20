package CglibRegisterToSpring.enums;

public enum  DatasourceEnum {


	DB1("db1"),DB2("db2");

    private String value;

    private DatasourceEnum(String value){
    	this.value=value;
    }

    public String getValue() {
        return value;
    }
	
	
}
