package org.example.ementasua;

import org.example.ementasua.EmentasPicker.*;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class EmentasUA extends TabActivity {
	private final EmentasPicker ep = new EmentasPicker();
	private Handler mHandler; 			// Need handler for callbacks to the UI thread
	private ProgressDialog pPialog;
	private final Context ct = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mHandler = new Handler();
		createTabs();

		startParseOfEmentas();    
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.refresh:
			getEmentas();
			return true;
		case R.id.set_about:
			startActivity(new Intent(this ,About.class));
			return true;
		}	
		return false;
	}    

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	

	private void createTabs(){
		Resources rec = getResources();
		TabHost mTabHost = getTabHost();

		mTabHost.addTab(mTabHost.newTabSpec("santiago").setIndicator("Santiago", rec.getDrawable(R.drawable.ic_tab_artists)).setContent(R.id.santiago));
		mTabHost.addTab(mTabHost.newTabSpec("crasto").setIndicator("Crasto", rec.getDrawable(R.drawable.ic_tab_artists)).setContent(R.id.crasto));
		mTabHost.addTab(mTabHost.newTabSpec("snackbar").setIndicator("SnackBar", rec.getDrawable(R.drawable.ic_tab_artists)).setContent(R.id.snackbar));

		mTabHost.setCurrentTab(0);
	}




	private void getEmentas(){
		Log.d("CENAS", "setted=="+ep.getSetted());
		if( !ep.getSetted()){
			if(!ep.Start()){
				pPialog.cancel();
				//Toast.makeText(ct, "Erro a obter ementas", Toast.LENGTH_SHORT).show();
			}
		}		
	}

	private void printSantiago() {
		TableLayout tl;
		
		Cantina santiago = ep.getEmentaCantina().santiago;

		// Obter o TableLayout da view Santiago
		tl = (TableLayout) this.findViewById(R.id.tablelayoutS);

		printEmenta(tl, santiago.almoco,1+ tl.indexOfChild(tl.findViewById(R.id.linetopS)));
		printEmenta(tl, santiago.jantar,1+ tl.indexOfChild(tl.findViewById(R.id.linebottomS)));
	}

	private void printCastro() {
		TableLayout tl;

		Cantina crasto = ep.getEmentaCantina().crasto;
		
		// Obter o TableLayout da view Santiago
		tl = (TableLayout) this.findViewById(R.id.tablelayoutC);

		printEmenta(tl, crasto.almoco,1+ tl.indexOfChild(tl.findViewById(R.id.linetopC)));
		printEmenta(tl, crasto.jantar,1+ tl.indexOfChild(tl.findViewById(R.id.linebottomC)));
	}

	private void printSnackBar() {
		TableLayout tl;
		
		Cantina snackbar = ep.getEmentaCantina().snackbar;

		// Obter o TableLayout da view 
		tl = (TableLayout) this.findViewById(R.id.tablelayoutSB);

		printEmenta(tl, snackbar.almoco,1+ tl.indexOfChild(tl.findViewById(R.id.linetopSB)));
	}	

	private void printEmenta(TableLayout tl, Ementa ement, int index){
		if(ement.aberto){
			for(int i=0; i<ement.pratos.size(); i++){
				Pratos p = ement.pratos.get(i);
				tl.addView( getRow(p) , index+i);
			}
		}else{
			tl.addView( getRow(ement.texto) , index);
		} 
	}

	private TableRow getRow(String str){
		TableRow tr = new TableRow(this);
		TextView text = new TextView(this);
		
		text.setText(str);
		text.setPadding(15, 10, 5, 20);
		text.setTextColor(getResources().getColor(R.color.prato));
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);

		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = 2;
		
		tr.addView(text, params);
		
		return tr;
	}
	
	private TableRow getRow(Pratos pratos){
		TableRow tr ;
		TextView tv_tipo;
		TextView tv_prato ;

		tr = new TableRow(this);
		tv_tipo = new TextView(this);
		tv_prato = new TextView(this);
		


		tv_tipo.setText( pratos.tipo);
		tv_tipo.setTextColor(getResources().getColor(R.color.tipo));
		tv_prato.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		tv_tipo.setPadding(4, 2, 5, 0);
		//tv_tipo.setShadowLayer(3, 0, 0, Color.parseColor("#FFFFFF"));


		tv_prato.setText( pratos.prato);
		tv_prato.setTextColor(getResources().getColor(R.color.prato));
		tv_prato.setPadding(15, 5, 5, 0);
		tv_prato.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		//tv_prato.setTypeface(Typeface.DEFAULT_BOLD);
		tv_prato.setSingleLine(false);
		//tv_prato.setShadowLayer(3, 0, 0, Color.parseColor("#000000"));

		tr.addView(tv_tipo);
		tr.addView(tv_prato);

		
		Log.d("cenas", pratos.tipo+" > "+pratos.prato);

		return tr;
	}	 


	protected void startParseOfEmentas() {		
			pPialog = ProgressDialog.show(this, "", "A carregar...", true);
			// Fire off a thread to do some work that we shouldn't do directly in the UI thread
			Thread t = new Thread() {
				public void run() {
					getEmentas();
					mHandler.post(mUpdateResults);
				}
			};
			t.start();
			
	}

	
	
	
	// Create runnable for posting
	private final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateResultsInUi();
		}
	};
	
	private void updateResultsInUi() {

		printSantiago();
		printCastro();
		printSnackBar();
		pPialog.cancel();
	}	 


}