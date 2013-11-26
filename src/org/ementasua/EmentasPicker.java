package org.ementasua;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class EmentasPicker{

	private EmentaCantina ementasCantina;
	private String[] conteudoLinhas;

	private int santAlm;
	private int santJant;
	private int crastAlm;
	private int crastJant;
	private int snack;
	private int fimLinhas;
	
	private boolean setted=false;
	
	private static final EmentasPicker INSTANCE = new EmentasPicker();
	
	public static EmentasPicker getEmentasPicker(){
		return INSTANCE;
	}
	
	public EmentasPicker(){
	}

	public boolean Start(){
		String inputLine;
		StringBuffer conteudoUtil;
		BufferedReader in;
		URL url;
		String output;
		
		conteudoUtil = new StringBuffer();
		try {
			url = new URL("http://www2.sas.ua.pt/site/temp/alim_ementas_V2.asp");
			in = new BufferedReader( new InputStreamReader(url.openStream(),"ISO-8859-1"));

			int count=0;

			// Ignorar até encontrar a tabela com a ementa
			while((inputLine = in.readLine().trim()) != null){
				count ++;
				if(inputLine.matches(".*id=\"table1\".*"))
					break;
			}
			while ((inputLine = in.readLine()) != null){
				inputLine = inputLine.trim();
				if(inputLine.matches(".*</table>.*"))  				// Se encontrar esta tag ï¿½ porque para a frente nï¿½o interessa mais
					break;
				if(inputLine.matches(".*<tr>.*")){	  			// Inicio de uma linha
					while ((inputLine = in.readLine().trim()) != null){
						if(inputLine.matches(".*</tr>.*")) 	 		// Fim da linha
							break;
						
						inputLine = regexString("((width|height|bordercolor|style|bordercolorlight|colspan|bgcolor)=\"[^\"]*\")|(</?(font|a)[^>]*>)|(&nbsp;)|(</?p[^>]*>)|(\\|)|(<center>)|Refeitï¿½rio", inputLine);			
						conteudoUtil.append(inputLine);		// Juntar tudo para a mesma string 
					}
					conteudoUtil.append("\n");
				}
			}
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		output = regexString(" {2,}", conteudoUtil.toString()); // Retirar espaços repetidos
		output = regexString("&atilde;", output, "ã");
		output = regexString("(\\n(<td></td>)+)|</td>", output);
		//output = regexString("\\n{2,}", output, "");
		
		conteudoLinhas = output.split("\\n");

		ementasCantina = new EmentaCantina();

		String tmp;
		for(int i = 0; i<conteudoLinhas.length; i++){
			tmp = conteudoLinhas[i];
			if(tmp.matches(".*Santiago.*") && tmp.matches(".*Almoço.*")){
				santAlm = i+1;
			}
			else if(tmp.matches(".*Santiago.*") && tmp.matches(".*Jantar.*")){
				santJant = i+1;
			}
			else if(tmp.matches(".*Crasto.*") && tmp.matches(".*Almoço.*")){
				crastAlm = i+1;
			}
			else if(tmp.matches(".*Crasto.*") && tmp.matches(".*Jantar.*")){
				crastJant = i+1;
			}
			else if(tmp.matches(".*Snack-Bar.*")){
				snack = i+1;
			}
		}
		fimLinhas = conteudoLinhas.length-1;
		
		//Get Santiago - Almoço
		ementasCantina.santiago.almoco = makeEmenta(santAlm, santJant-2);
		//Get Santiago - jantar
		ementasCantina.santiago.jantar = makeEmenta(santJant, crastAlm-2);
		//Get Crasto - Almoço
		ementasCantina.crasto.almoco = makeEmenta(crastAlm, crastJant-2);
		//Get Crasto - Jantar
		ementasCantina.crasto.jantar = makeEmenta(crastJant, snack-2);
		//Get Snackbar
		ementasCantina.snackbar.almoco = makeEmenta(snack, fimLinhas);
		ementasCantina.snackbar.jantar = null;
		
		setted = true;
		return true;
	}

	public boolean getSetted(){
		return setted;
	}
	
	public EmentaCantina getEmentaCantina(){
		return ementasCantina;
	}
	
	private Ementa makeEmenta(int first, int last){
		Ementa em = new Ementa();
		em.pratos = new ArrayList<EmentasPicker.Pratos>();
		
		for(int i=first; i<=last; i++){
			Pratos p = new Pratos();
			String[] tmp = conteudoLinhas[i].split("<td>");
			// Everything is correct
			if(tmp.length==3){
				p.tipo = tmp[1];
				p.prato = tmp[2];
				em.pratos.add(p);
			}
			//Maybe the plate is not set
			else if(tmp.length==2){
				//TODO check if there is something missing
				if(em.pratos.size()>1){
					p.tipo = tmp[1];
					p.prato = "";		
					em.pratos.add(p);
				}//in this case, probably the canteen is closed
				else{
					em.texto = em.pratos.get(em.pratos.size()-1).prato;
					em.pratos = null;
					em.aberto = false;
					break;
				}
			}
		}
		return em;
	}


	public class EmentaCantina {
		long data;
		Cantina santiago;
		Cantina crasto;
		Cantina snackbar;
		
		public EmentaCantina(){
			data = System.currentTimeMillis()/1000;
			santiago = new Cantina();
			crasto = new Cantina();
			snackbar = new Cantina();
		}
	}

	public class Cantina {
		Ementa almoco = null;
		Ementa jantar = null;
	}

	public class Ementa {
		ArrayList<Pratos> pratos = null;
		boolean aberto = true;
		String texto = null;
	}
	
	public class Pratos{
		String tipo = "";
		String prato = "";
	}

	private String regexString(String regex, String str, String replace){
		return Pattern
		.compile(regex)
		.matcher(str)
		.replaceAll(replace);
	}

	private String regexString(String regex, String str){
		return regexString(regex, str, "");
	}	
}
