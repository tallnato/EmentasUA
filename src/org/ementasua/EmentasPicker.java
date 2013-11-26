package org.ementasua;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class EmentasPicker implements Serializable
{

	private static final long serialVersionUID = -3412540332492401068L;
	private EmentaCantina ementasCantina;
	private static final EmentasPicker INSTANCE = new EmentasPicker();

	private boolean setted = false;

	public EmentasPicker()
	{
	}

	public static EmentasPicker getEmentasPicker()
	{
		return INSTANCE;
	}

	public boolean Start()
	{
		String[] conteudoLinhas;

		int santAlm = 0;
		int santJant = 0;
		int crastAlm = 0;
		int crastJant = 0;
		int snack = 0;
		int fimLinhas = 0;

		String inputLine;
		StringBuffer conteudoUtil;
		BufferedReader in;
		URL url;
		String output;

		conteudoUtil = new StringBuffer();
		try {
			url = new URL("http://www2.sas.ua.pt/site/temp/alim_ementas_V2.asp");
			in = new BufferedReader(new InputStreamReader(url.openStream(), "ISO-8859-1"));

			int count = 0;

			// Ignorar até encontrar a tabela com a ementa
			while((inputLine = in.readLine().trim()) != null) {
				count++;
				if(inputLine.matches(".*id=\"table1\".*"))
					break;
			}
			while((inputLine = in.readLine()) != null) {
				inputLine = inputLine.trim();
				if(inputLine.matches(".*</table>.*")) // Não interessa mais para a frente
					break;
				if(inputLine.matches(".*<tr>.*")) { // Inicio de uma linha
					while((inputLine = in.readLine().trim()) != null) {
						if(inputLine.matches(".*</tr>.*")) // Fim da linha
							break;

						inputLine = regexString(
								"((width|height|bordercolor|style|bordercolorlight|colspan|bgcolor)=\"[^\"]*\")|(</?(font|a)[^>]*>)|(&nbsp;)|(</?p[^>]*>)|(\\|)|(<center>)|Refeitï¿½rio",
								inputLine);
						conteudoUtil.append(inputLine); // Juntar tudo na mesma string
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

		conteudoLinhas = output.split("\\n");

		ementasCantina = new EmentaCantina();

		String tmp;
		for(int i = 0; i < conteudoLinhas.length; i++) {
			tmp = conteudoLinhas[i];
			if(tmp.matches(".*Santiago.*") && tmp.matches(".*Almoço.*")) {
				santAlm = i + 1;
			} else if(tmp.matches(".*Santiago.*") && tmp.matches(".*Jantar.*")) {
				santJant = i + 1;
			} else if(tmp.matches(".*Crasto.*") && tmp.matches(".*Almoço.*")) {
				crastAlm = i + 1;
			} else if(tmp.matches(".*Crasto.*") && tmp.matches(".*Jantar.*")) {
				crastJant = i + 1;
			} else if(tmp.matches(".*Snack-Bar.*")) {
				snack = i + 1;
			}
		}
		fimLinhas = conteudoLinhas.length - 1;

		// Get Santiago - Almoço
		ementasCantina.santiago.almoco = makeEmenta(conteudoLinhas, santAlm, santJant - 2);
		// Get Santiago - jantar
		ementasCantina.santiago.jantar = makeEmenta(conteudoLinhas, santJant, crastAlm - 2);
		// Get Crasto - Almoço
		ementasCantina.crasto.almoco = makeEmenta(conteudoLinhas, crastAlm, crastJant - 2);
		// Get Crasto - Jantar
		ementasCantina.crasto.jantar = makeEmenta(conteudoLinhas, crastJant, snack - 2);
		// Get Snackbar
		ementasCantina.snackbar.almoco = makeEmenta(conteudoLinhas, snack, fimLinhas);
		ementasCantina.snackbar.jantar = null;

		setted = true;
		return true;
	}

	public boolean getSetted()
	{
		return setted;
	}

	public EmentaCantina getEmentaCantina()
	{
		return ementasCantina;
	}

	private Ementa makeEmenta(String cL[], int first, int last)
	{
		int count = 0;
		Ementa em = new Ementa();
		em.pratos = new ArrayList<EmentasPicker.Pratos>();

		for(int i = first; i <= last; i++) {
			Pratos p = new Pratos();
			String[] tmp = cL[i].split("<td>");
			// Everything is correct
			if(tmp.length == 3) {
				p.tipo = tmp[1];
				p.prato = tmp[2];
				em.pratos.add(p);
			}
			// Maybe the plate is not set
			else if(tmp.length == 2) {
				// TODO check if there is something missing
				if(em.pratos.size() > 1) {
					p.tipo = tmp[1];
					p.prato = "";
					em.pratos.add(p);
				}// in this case, probably the canteen is closed
				else {
					count++;
					if(count > 4) {
						em.texto = em.pratos.get(em.pratos.size() - 1).prato;
						em.pratos = null;
						em.aberto = false;
						break;
					}
				}
			}
		}
		return em;
	}

	public boolean write(File f)
	{
		ObjectOutputStream objectOut;
		try {
			objectOut = new ObjectOutputStream(new FileOutputStream(f));
			objectOut.writeObject(INSTANCE);
			objectOut.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public static EmentasPicker read(File f)
	{
		EmentasPicker ep = null;
		ObjectInputStream objectIn;
		try {
			objectIn = new ObjectInputStream(new FileInputStream(f));
			ep = (EmentasPicker) objectIn.readObject();
			objectIn.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
		return ep;

	}

	public class EmentaCantina implements Serializable
	{
		private static final long serialVersionUID = -6012995717073742362L;
		Date date = new Date();
		Cantina santiago = new Cantina();
		Cantina crasto = new Cantina();
		Cantina snackbar = new Cantina();
	}

	public class Cantina implements Serializable
	{
		private static final long serialVersionUID = 3274442136255349057L;
		Ementa almoco = null;
		Ementa jantar = null;
	}

	public class Ementa implements Serializable
	{
		private static final long serialVersionUID = 308546961712387555L;
		ArrayList<Pratos> pratos = null;
		boolean aberto = true;
		String texto = null;
	}

	public class Pratos implements Serializable
	{
		private static final long serialVersionUID = 6076456261604364921L;
		String tipo = "";
		String prato = "";
	}

	private String regexString(String regex, String str, String replace)
	{
		return Pattern.compile(regex).matcher(str).replaceAll(replace);
	}

	private String regexString(String regex, String str)
	{
		return regexString(regex, str, "");
	}
}
