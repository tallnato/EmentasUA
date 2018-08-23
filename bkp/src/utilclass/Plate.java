package utilclass;

import java.io.Serializable;

public class Plate implements Serializable{

	private static final long serialVersionUID = -3324284628389439639L;
	private String type = "";
	private String name = "";
	
	public Plate(String tp, String nm){
		type = tp;
		name = nm; 
	}
	
	public Plate(){
		
	}
	
	public void setType(String tp){
		type = tp;
	}
	
	public void setName(String nm){
		name = nm;
	}
	
	public String getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
}
