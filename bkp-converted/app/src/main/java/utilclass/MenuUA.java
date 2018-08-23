package utilclass;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MenuUA implements Serializable{

	private static final long serialVersionUID = 5153160942448985737L;
	private ArrayList<Canteen> santiago;
	private ArrayList<Canteen> crasto;
	private ArrayList<Canteen> snackbar;
	public String retrieveDate;
	
	public MenuUA(){
		santiago = new ArrayList<Canteen>();
		crasto = new ArrayList<Canteen>();
		snackbar = new ArrayList<Canteen>();
		retrieveDate = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
	}
	
	public ArrayList<Canteen> getSantiago(){
		return santiago;
	}
	
	public ArrayList<Canteen> getCrasto(){
		return crasto;
	}
	
	public ArrayList<Canteen> getSnackBar(){
		return snackbar;
	}
}