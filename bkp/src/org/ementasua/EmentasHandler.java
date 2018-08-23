package org.ementasua;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.format.DateFormat;

import utilclass.*;

public class EmentasHandler  extends DefaultHandler{
	private MenuUA menu;
	
	private boolean in_result = false;
	private boolean in_menus = false;
	private boolean in_menu = false;
	private boolean in_items = false;
	private boolean in_item = false;	
	
	private ArrayList<Canteen> actualList;
	private Canteen actualCanteen;
	private Menu actualMenu;
	private Plate actualPlate;
		
	public MenuUA getMenuUA() {
		return this.menu;
	}
	
	@Override
	public void startDocument() throws SAXException {
		this.menu = new MenuUA();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}
	
	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	public void startElement(String namespaceURI, String localName,	String qName, Attributes atts) throws SAXException {

		if (localName.equals("result")) {
			this.in_result = true;
		} else if (localName.equals("menus")) {
			this.in_menus = true;
		} else if (localName.equals("menu")) {
			this.in_menu = true;
			String canteen = atts.getValue("canteen");
			String meal = atts.getValue("meal");
			String date = atts.getValue("date").replaceAll("\\p{Cntrl}", "");
			
			String weekday = atts.getValue("weekday");
			String disabled = atts.getValue("disabled");
			int weekNr = Integer.parseInt(atts.getValue("weekdayNr"));
			
			if(canteen.contains("Santiago")){
				actualList = menu.getSantiago();
			}else if(canteen.contains("Crasto")){
				actualList = menu.getCrasto();
			}else if(canteen.contains("Snack-Bar")){
				actualList = menu.getSnackBar();
			}
			
			for(Canteen c : actualList){
				if(c.date.equals(new Date(date))){
					actualCanteen = c;
				}
			}
			
			if(actualCanteen == null){
				actualCanteen = new Canteen();
				 
				actualCanteen.date = new Date(date);
				actualCanteen.date.setHours(0);
				actualCanteen.date.setMinutes(0);
				actualCanteen.date.setSeconds(0);
				actualCanteen.weekDay = weekday;
				actualCanteen.weekDayNr = weekNr;
				actualList.add(actualCanteen);
			}
		
			
			if(meal.equals("Almoço")){
				actualMenu = actualCanteen.lunch;
			}else if(meal.equals("Jantar")){
				actualMenu = actualCanteen.dinner;
			}
			
			if(!disabled.equals("0")){
				actualMenu.setDisable(disabled);
			}
		} else if (localName.equals("items")) {
			this.in_items = true;
		} else if (localName.equals("item")) {
			this.in_item = true;
			actualPlate = new Plate();
			actualPlate.setType(atts.getValue("name"));
			actualMenu.addPlate(actualPlate);
		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (localName.equals("result")) {
			this.in_result = false;
		} else if (localName.equals("menus")) {
			this.in_menus = false;
		} else if (localName.equals("menu")) {
			this.in_menu = false;
			actualCanteen = null;
		} else if (localName.equals("items")) {
			this.in_items = false;
		} else if (localName.equals("item")) {
			this.in_item = false;
			actualPlate = null;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		String tmp = (new String(ch, start,length)).trim();
		
		if (this.in_item && tmp.length()>0) {
			actualPlate.setName(tmp);
		}
	}
}
