import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

 class Employee {
  String name;
  double salary;
  Integer id;
  String designation;
  public String insuranceScheme;

  public Employee(String name, double salary, Integer id, String designation) {
    //parameterized constructor
  }
  public static void main(String args[]){
      
  }

  public String getInsuranceScheme(double salary) {
	  String scheme ="";
   if(salary<5000){
	   scheme ="no scheme";
   }else if(salary>=5000 && salary < 20000){
	   scheme ="scheme c";
   }else if(salary>=20000 && salary<40000){
	   scheme ="scheme b";
   }else if(salary>=40000  ){
	   scheme ="scheme a";
   }
   return scheme;
  }
}


class EmployeeServiceImpl {
	Map<Integer, Employee> employeeMap = new HashMap<Integer, Employee>();
  //Declare a Hashmap here where key is an Integer and the value is Employee object

  public void addEmployee(Employee emp) {
    //write your code here to add employee to the hashmap 
	  try{
		  if(employeeMap!= null){
			  employeeMap.put(emp.id, emp);
		  }
	  
	  }catch(Exception ex){}
  }

  public boolean deleteEmployee(int id){
	//write your code here for returning true if the employee deleted wrt the id passed else false
	   boolean status = false;
	  try{
		  if(employeeMap!= null && employeeMap.size()>0 &&  employeeMap.containsKey(id)){
			  employeeMap.remove(id);
		  }
	  
	  status= true;
	  }catch(Exception ex){}
	  return status;
    
  }

  public String showEmpDetails(String insuranceScheme) {
	  StringBuffer str = new  StringBuffer();
	  if(employeeMap!= null && employeeMap.size()>0){
		  for(Entry<Integer, Employee> a: employeeMap.entrySet()){
			  Employee emp =  a.getValue();
			  String scheme = ((Employee) a).getInsuranceScheme(emp.salary);
			  if(scheme.equalsIgnoreCase(insuranceScheme)){
				  str.append("Name: "+emp.name+" Id: "+emp.id+" Salary: "+emp.salary+" Designation: "+emp.designation+" InsuranceScheme: "+emp.insuranceScheme+" \n");
			  }
		  }
	  }
	  return str.toString();
    /*Write your code here to return a string i.e the employee details according to the insurance scheme
    Format: Name: name Id: id Salary: salary Designation: Designation InsuranceScheme: InsuranceScheme
    If multiple results they should be in a different line*/
  }
}