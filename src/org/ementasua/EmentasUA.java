package org.ementasua;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.ementasua.EmentasPicker.Cantina;
import org.ementasua.EmentasPicker.Ementa;
import org.ementasua.EmentasPicker.Pratos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class EmentasUA extends Activity
{
	private EmentasPicker ep;
	private Handler mHandler; // Need handler for callbacks to the UI thread
	private ProgressDialog pPialog;
	private boolean mostraTudo = true;
	private final String nomeFicheiro = "EmentasUA";
	private final static String TAG = "EmentasUA";
	private static boolean santiago = true, crasto = false, snackbar = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void showSantiago(View v)
	{
		santiago = true;
		crasto = false;
		snackbar = false;

		if(v != null) {
			TextView tv = (TextView) v;
			tv.setBackgroundResource(R.drawable.top_button);
			tv.setTextColor(getResources().getColor(R.color.prato));
		}

		TextView tv_1 = (TextView) this.findViewById(R.id.Title_Crasto);
		TextView tv_2 = (TextView) this.findViewById(R.id.Title_SnackBar);

		tv_1.setBackgroundResource(0);
		tv_2.setBackgroundResource(0);

		tv_1.setTextColor(getResources().getColor(R.color.prato));
		tv_2.setTextColor(getResources().getColor(R.color.prato));

		FrameLayout fl = (FrameLayout) this.findViewById(R.id.contents);
		fl.removeAllViewsInLayout();
		LayoutInflater.from(getApplicationContext()).inflate(R.layout.santiago, fl, true);

		printSantiago();
	}

	public void showCrasto(View v)
	{
		santiago = false;
		crasto = true;
		snackbar = false;

		if(v != null) {
			TextView tv = (TextView) v;
			tv.setBackgroundResource(R.drawable.top_button);
			tv.setTextColor(getResources().getColor(R.color.prato));
		}

		TextView tv_1 = (TextView) this.findViewById(R.id.Title_Santiago);
		TextView tv_2 = (TextView) this.findViewById(R.id.Title_SnackBar);

		tv_1.setBackgroundResource(0);
		tv_2.setBackgroundResource(0);

		tv_1.setTextColor(getResources().getColor(R.color.prato));
		tv_2.setTextColor(getResources().getColor(R.color.prato));

		FrameLayout fl = (FrameLayout) this.findViewById(R.id.contents);
		fl.removeAllViewsInLayout();
		LayoutInflater.from(getApplicationContext()).inflate(R.layout.crasto, fl, true);

		printCrasto();
	}

	public void showSnackBar(View v)
	{
		santiago = false;
		crasto = false;
		snackbar = true;

		if(v != null) {
			TextView tv = (TextView) v;
			tv.setBackgroundResource(R.drawable.top_button);
			tv.setTextColor(getResources().getColor(R.color.prato));
		}
		TextView tv_1 = (TextView) this.findViewById(R.id.Title_Crasto);
		TextView tv_2 = (TextView) this.findViewById(R.id.Title_Santiago);

		tv_1.setBackgroundResource(0);
		tv_2.setBackgroundResource(0);

		tv_1.setTextColor(getResources().getColor(R.color.prato));
		tv_2.setTextColor(getResources().getColor(R.color.prato));

		FrameLayout fl = (FrameLayout) this.findViewById(R.id.contents);
		fl.removeAllViewsInLayout();
		LayoutInflater.from(getApplicationContext()).inflate(R.layout.snackbar, fl, true);

		printSnackBar();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setContentView(R.layout.main);

		TextView data = (TextView) findViewById(R.id.Title_Data);
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
		String currentTime = formatter.format(new Date());

		data.setText(currentTime);

		mHandler = new Handler();
		if(ep == null) {
			ep = getEmentasFile(getFilesDir());
			Calendar cal = Calendar.getInstance();
			if(ep == null
					|| (ep.getEmentaCantina().date.getDate() != cal.get(Calendar.DATE)
							|| ep.getEmentaCantina().date.getMonth() != cal.get(Calendar.MONTH) || (ep
							.getEmentaCantina().date.getYear() + 1900) != cal.get(Calendar.YEAR))) {
				ep = EmentasPicker.getEmentasPicker();
				if(!checkWiFi3G()) {
					makeDialog();
					return;
				}
			}
		}
		startParseOfEmentas();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mostraTudo = sharedPreferences.getBoolean("info", true);

		if(santiago) {
			this.findViewById(R.id.Title_Santiago).setBackgroundResource(R.drawable.top_button);
			showSantiago(null);
		}
		if(crasto) {
			this.findViewById(R.id.Title_Crasto).setBackgroundResource(R.drawable.top_button);
			showCrasto(null);
		}
		if(snackbar) {
			this.findViewById(R.id.Title_SnackBar).setBackgroundResource(R.drawable.top_button);
			showSnackBar(null);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.refresh:
				File f = new File(this.getFilesDir(), nomeFicheiro);
				f.delete();

				mHandler = new Handler();
				ep = getEmentasFile(getFilesDir());
				Calendar cal = Calendar.getInstance();
				if(ep == null
						|| (ep.getEmentaCantina().date.getDate() != cal.get(Calendar.DATE)
								|| ep.getEmentaCantina().date.getMonth() != cal.get(Calendar.MONTH) || (ep
								.getEmentaCantina().date.getYear() + 1900) != cal.get(Calendar.YEAR))) {
					ep = EmentasPicker.getEmentasPicker();
					if(!checkWiFi3G()) {
						makeDialog();
						return true;
					}
				}
				updateResultsInUi();
				startParseOfEmentas();
				return true;
			case R.id.about:
				startActivity(new Intent(this, About.class));
				return true;
			case R.id.prefs:
				startActivity(new Intent(this, Prefs.class));
				return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	private boolean getEmentas()
	{
		if(!ep.Start()) {
			pPialog.cancel();
			return false;
		} else {
			writeEmentasFile(getFilesDir());
			return true;
		}
	}

	private void printSantiago()
	{
		if(!santiago || ep == null || ep.getEmentaCantina() == null || ep.getEmentaCantina().snackbar == null)
			return;

		TableLayout tl;

		Cantina santiago = ep.getEmentaCantina().santiago;

		// Obter o TableLayout da view Santiago
		tl = (TableLayout) this.findViewById(R.id.tablelayoutS);

		printEmenta(tl, santiago.almoco, 1 + tl.indexOfChild(tl.findViewById(R.id.linetopS)));
		printEmenta(tl, santiago.jantar, 1 + tl.indexOfChild(tl.findViewById(R.id.linebottomS)));
	}

	private void printCrasto()
	{
		if(!crasto || ep == null || ep.getEmentaCantina() == null || ep.getEmentaCantina().crasto == null)
			return;

		TableLayout tl;

		Cantina crasto = ep.getEmentaCantina().crasto;

		// Obter o TableLayout da view Santiago
		tl = (TableLayout) this.findViewById(R.id.tablelayoutC);

		printEmenta(tl, crasto.almoco, 1 + tl.indexOfChild(tl.findViewById(R.id.linetopC)));
		printEmenta(tl, crasto.jantar, 1 + tl.indexOfChild(tl.findViewById(R.id.linebottomC)));
	}

	private void printSnackBar()
	{
		if(!snackbar || ep == null || ep.getEmentaCantina() == null || ep.getEmentaCantina().snackbar == null)
			return;

		TableLayout tl;

		Cantina snackbar = ep.getEmentaCantina().snackbar;

		// Obter o TableLayout da view 
		tl = (TableLayout) this.findViewById(R.id.tablelayoutSB);

		printEmenta(tl, snackbar.almoco, 1 + tl.indexOfChild(tl.findViewById(R.id.linetopSB)));
	}

	private EmentasPicker getEmentasFile(File dir)
	{
		File f = new File(dir, nomeFicheiro);
		EmentasPicker ep = EmentasPicker.read(f);
		if(ep == null)
			return null;
		return ep;
	}

	private boolean writeEmentasFile(File dir)
	{
		File f = new File(dir, nomeFicheiro);
		return ep.write(f);
	}

	protected void startParseOfEmentas()
	{
		if(!ep.getSetted())
			pPialog = ProgressDialog.show(this, "", "A carregar...", true);

		// Fire off a thread to do some work that we shouldn't do directly in the UI thread
		Thread t = new Thread() {
			public void run()
			{
				if(!ep.getSetted()) {
					getEmentas();
				}
				mHandler.post(mUpdateResults);
			}
		};
		t.start();
	}

	// Create runnable for posting
	private final Runnable mUpdateResults = new Runnable() {
		public void run()
		{
			if(ep.getSetted()) {
				updateResultsInUi();
			} else {
				pPialog.cancel();
				makeDialog();
			}
		}
	};

	private void updateResultsInUi()
	{
		if(santiago) {
			showSantiago(null);
		}
		if(crasto) {
			showCrasto(null);
		}
		if(snackbar) {
			showSnackBar(null);
		}

		if(pPialog != null && pPialog.isShowing())
			pPialog.cancel();
	}

	private void makeDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.notNet)).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id)
					{
						//((Activity) ct).finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private boolean checkWiFi3G()
	{
		ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return(connec.getActiveNetworkInfo() != null && connec.getActiveNetworkInfo().isAvailable() && connec
				.getActiveNetworkInfo().isConnected());
	}

	private TableRow getRow(String str)
	{
		TableRow tr = new TableRow(this);
		TextView text = new TextView(this);

		text.setText(str);
		text.setPadding(15, 10, 5, 20);
		text.setTextColor(getResources().getColor(R.color.prato));
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = 2;

		tr.addView(text, params);

		return tr;
	}

	private TableRow getRow(Pratos pratos, TableLayout tl)
	{
		TableRow tr;
		TextView tv_tipo;
		TextView tv_prato;

		//tr = new TableRow(this);

		tr = (TableRow) LayoutInflater.from(this).inflate(R.layout.tr_layout, tl, false);

		tv_tipo = new TextView(this);
		tv_prato = new TextView(this);

		tv_tipo.setText(pratos.tipo);
		tv_tipo.setTextColor(getResources().getColor(R.color.tipo));
		tv_prato.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		tv_tipo.setPadding(4, 2, 5, 0);

		tv_prato.setText(pratos.prato);
		tv_prato.setTextColor(getResources().getColor(R.color.prato));
		tv_prato.setPadding(15, 5, 5, 0);
		tv_prato.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		tv_prato.setSingleLine(false);
		tv_prato.setId(55568);
		tv_prato.setBackgroundResource(R.drawable.tr_style);

		tr.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v)
			{
				TextView tv = (TextView) v.findViewById(55568);
				if(tv.getText().equals("") || tv.getText().length() == 0)
					return true;
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(tv.getText());
				Toast.makeText(getApplicationContext(), "Prato copiado para a área de transferência...",
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});

		tr.addView(tv_tipo);
		tr.addView(tv_prato);

		return tr;
	}

	private void printEmenta(TableLayout tl, Ementa ement, int index)
	{
		if(ement.aberto) {
			ArrayList<Pratos> list = ement.pratos;
			int total = 0;
			for(Pratos p : list) {
				if(!mostraTudo) {
					if(p.tipo.contains("Prato") == false || p.prato.equals("")) {
						continue;
					}
				}
				tl.addView(getRow(p, tl), index + total++);
			}
		} else {
			tl.addView(getRow(ement.texto), index);
		}
	}

}