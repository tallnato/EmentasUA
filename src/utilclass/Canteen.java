package utilclass;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Canteen  implements Serializable{

	private static final long serialVersionUID = 3460790638210057186L;
	public String weekDay = null;
	public Date date = null;
	public Menu lunch;
	public Menu dinner;
	public int weekDayNr = -1;
	public boolean selected;
	
	public Canteen(){
		lunch = new Menu();
		dinner = new Menu();
	}
	
	public String getMicroDate(){
		return (new SimpleDateFormat("EEE, dd MMM")).format(date);
	}
}
