package utilclass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ementasua.EmentasHandler;
import org.ementasua.R;
import org.ementasua.Start.EmentasTask;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import android.util.Log;

public class EmentasGetter {

	private static MenuUA menuUA = null;
	private static String fileName = "EmentasUA";
	private static File dirPath = null;
	
	public static MenuUA getEmentasUA() throws Exception{
		if(menuUA == null)
			getEmentas();
		return menuUA;
	}
	
	public static void start(EmentasTask task) throws Exception{
		if(task != null)
			task.updateProgress(R.string.start_read_file);
		readEmentas();
		if(menuUA == null){
			if(task != null)
				task.updateProgress(R.string.start_read_web);
			retriveFromWeb();
		}else{
			if(menuUA.retrieveDate.equals((new SimpleDateFormat("dd/MM/yyyy", Locale.UK)).format(new Date()))){
				return;
			}else{
				if(task != null)
					task.updateProgress(R.string.start_read_web);
				try{
					retriveFromWeb();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void setDirPath(File dir){
		dirPath = dir;
	}
	
	private static void getEmentas() throws Exception{
		Log.d("EmentasUA", "getEmentas()");
		if(menuUA == null){
			readEmentas();
			if(menuUA != null){
				Log.d("EmentasUA", "ementas not null");
				return;
			}
			else{
				retriveFromWeb();
			}
		}
	}
	
	private static void retriveFromWeb() throws Exception{
		Log.d("EmentasUA", "retriveFromWeb()");
		MenuUA temp = null;
		try {
			URL url = new URL("http://services.web.ua.pt/sas/ementas?date=week");
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			XMLReader xr = sp.getXMLReader();
			EmentasHandler ementasHandler = new EmentasHandler();
			xr.setContentHandler(ementasHandler);

			xr.parse(new InputSource(url.openStream()));
			temp = ementasHandler.getMenuUA();
			
			menuUA = temp;
			removeOld();
		} catch (Exception e) {
			e.printStackTrace();
			if(temp == null && menuUA == null)
				throw new Exception("Error getting data from web...");
		}
	}
	
	private static void removeOld(){
		Date d = new Date();
		if(menuUA == null)
			return;
		
		removeOld(menuUA.getSantiago().iterator(), d);
		removeOld(menuUA.getCrasto().iterator(), d);
		removeOld(menuUA.getSnackBar().iterator(), d);
		
		if(menuUA.getSantiago().size() == 0 || menuUA.getCrasto().size() == 0 || menuUA.getSnackBar().size() == 0)
			menuUA = null;
		else
			writeEmentas();
	}
	
	private static void writeEmentas(){
		Log.d("EmentasUA", "Writing ementas to file");
		ObjectOutputStream objectOut;
		try {
			objectOut = new ObjectOutputStream(new FileOutputStream(new File(dirPath,fileName)));
			objectOut.writeObject(menuUA);
			objectOut.close();
		} catch (Exception e) {
			Log.d("EmentasUA", "Failed writing to file");
			e.printStackTrace();
		}
	}
	
	private static void readEmentas(){
		Log.d("EmentasUA", "Reading ementas from file");
		ObjectInputStream objectIn;
		try {
			objectIn = new ObjectInputStream(new FileInputStream(new File(dirPath,fileName)));
			menuUA = (MenuUA) objectIn.readObject();
			objectIn.close();
			removeOld();
		} catch (Exception e) {
			Log.d("EmentasUA", "Reading ementas from file unsucesseful");
			menuUA = null;
			deleteFile();
		}
	}
	
	public static void deleteFile(){
		new File(dirPath,fileName).delete();
	}
	
	private static void removeOld(Iterator<Canteen> i, Date d){
		while(i.hasNext()){
			Canteen c = i.next();
			if(checkOld(d, c.date)){
				i.remove();
			}
		}
	}
	
	private static boolean checkOld(Date today, Date d){
		if(today.getYear() > d.getYear()){
			return true;
		}else{
			if(today.getMonth() > d.getMonth()){
				return true;
			}else{
				if(today.getDate() > d.getDate()){
					return true;
				}else{
					return false;
				}
			}
		}
	}
}
