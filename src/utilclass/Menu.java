package utilclass;

import java.io.Serializable;
import java.util.ArrayList;

import utilclass.Plate;

public class Menu  implements Serializable{

	private static final long serialVersionUID = -8851856690845737857L;
	private boolean disabled = true;
	private String disabledText = "Encerrado";
	private ArrayList<Plate> plates;
	
	public Menu(){
		plates = new ArrayList<Plate>();
	}
	
	public void setDisable(String text){
		disabled = true;
		disabledText = text;
		plates = null;
	}
	
	public void addPlate(String type, String name){
		addPlate(new Plate(type, name));
	}
	
	public void addPlate(Plate plate){
		disabled = false;
		plates.add(plate);
	}
	
	public Plate getPlate(int index){
		if(disabled)
			return null;
		return plates.get(index);
	}
	
	public boolean isDisabled(){
		return disabled;
	}
	
	public String getDisabledText(){
		return disabledText;
	}
	
	public ArrayList<Plate> getPlates(){
		return plates;
	}
}
