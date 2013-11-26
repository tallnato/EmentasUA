package org.example.ementasua;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.regex.Pattern;

import android.util.Log;

public class EmentasPicker{
	private float unixTime;
	private String inputLine;
	private StringBuffer conteudoUtil;
	private BufferedReader in;
	private URL url;
	protected EmentaCantina ementasCantina;
	private Cantina[] cantina;
	private String[] conteudoLinhas;
	private String output;
	private boolean isOk=true;
	
	public EmentasPicker(){
		Log.d("cenas", "inicio");
		conteudoUtil = new StringBuffer();
	}
	
	public boolean getOk(){
		return isOk;
	}

	public boolean Start(){
		/*
		try {
			if ( InetAddress.getByName("www.google.com").isReachable(2) ){
			    //no network is available
			    return false;
			}
		} catch (Exception e) {
			return false;
		} */
		/*
		try {
			if ( !InetAddress.getByName("www.google.pt").isReachable(3) ){
			    //no network is available
				Log.d("cenas", "no network available");
			    return false;
			}
			
			url = new URL("http://www2.sas.ua.pt/site/temp/alim_ementas_V2.asp");
			in = new BufferedReader( new InputStreamReader(url.openStream(),"ISO-8859-1"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}*/
		
		unixTime = System.currentTimeMillis();
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
				if(inputLine.matches(".*</table>.*"))  				// Se encontrar esta tag � porque para a frente n�o interessa mais
					break;
				if(inputLine.matches(".*<tr>.*")){	  			// Inicio de uma linha
					while ((inputLine = in.readLine().trim()) != null){
						if(inputLine.matches(".*</tr>.*")) 	 		// Fim da linha
							break;
						
						inputLine = regexString("((width|height|bordercolor|style|bordercolorlight|colspan|bgcolor)=\"[^\"]*\")|(</?(font|a)[^>]*>)|(&nbsp;)|(</?p[^>]*>)|(\\|)|(<center>)|Refeit�rio", inputLine);			
						conteudoUtil.append(inputLine);		// Juntar tudo para a mesma string 
					}
					conteudoUtil.append("\n");
				}
			}
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		output = regexString(" {2,}", conteudoUtil.toString(),""); // Retirar espaços repetidos
		output = regexString("&atilde;", output, "ã");
		
		conteudoLinhas = output.split("\\n");


		ementasCantina = new EmentaCantina();

		//Tirar dia 
		ementasCantina.data = regexString("</?td>", conteudoLinhas[0]);
		cantina = new Cantina[5];

		for(int i=0, k=2; i<5; i++){
			Ementa[] prato;

			cantina[i] = new Cantina();

			//tirar nome da cantina
			String tmpL;
			tmpL = regexString("</?td>", conteudoLinhas[k]);
			tmpL = regexString("Refeitório", tmpL);

			if(tmpL.matches(".*Jantar.*"))
				cantina[i].hora = "Jantar";
			else if (tmpL.matches(".*Almoco.*"))
				cantina[i].hora = "Almoco";
			else
				cantina[i].hora = "";
			cantina[i].nome = regexString("Jantar|Almoco", tmpL);

			prato = new Ementa[10];

			int h = ((i==2)||(i==3)?9:10);
			for(int j=0; j < h; j++){
				prato[j] = new Ementa();	
				String tmp;

				tmp = regexString("</td>", conteudoLinhas[k+2+j]);
				String tmpArr[] = tmp.split("<td>");
				//TODO melhorar isto	
				if(tmpArr.length==2){
					prato[j].tipo = tmpArr[1];
					prato[j].prato = "";
				}else					
					if(!tmpArr[2].matches("(Refeições servidas no Refeitório de Santiago)|(Refeições servidas no Snack Bar)")){
						prato[j].tipo = tmpArr[1];
						prato[j].prato = tmpArr[2];
					}
					else{
						cantina[i].aberto = false;
						cantina[i].texto  = tmpArr[2];
						break;
					}
			}
			k += ((i<2)?13:12);
			cantina[i].ementa = prato;
		}
		ementasCantina.cantina = cantina;

		unixTime = ((System.currentTimeMillis() - unixTime));
		Log.d("cenas","fim");
		return true;
	}

	public Cantina getCantina(int i){
		return this.ementasCantina.cantina[i];
	}

	public float getTempo(){
		return unixTime;
	}

	public class EmentaCantina {
		String data;
		Cantina[] cantina = new Cantina[5];
	}

	public class Cantina {
		String nome;
		String hora;
		Ementa[] ementa;
		boolean aberto = true;
		String texto;
	}

	public class Ementa {
		String tipo;
		String prato;
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
