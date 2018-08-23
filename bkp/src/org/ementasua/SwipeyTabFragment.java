/*
 * Copyright 2011 Peter Kuterna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ementasua;

import java.util.ArrayList;
import java.util.Calendar;

import utilclass.Canteen;
import utilclass.EmentasGetter;
import utilclass.Menu;
import utilclass.MenuUA;
import utilclass.Plate;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

public class SwipeyTabFragment extends Fragment {
	
	private MenuUA menu = null;
	private LayoutInflater inflat = null;
	private boolean show_next_day;
	
	public static Fragment newInstance(String title) {
		SwipeyTabFragment f = new SwipeyTabFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflat = inflater;
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.tab_layout, null);
		
		try {
			menu = EmentasGetter.getEmentasUA();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TableLayout tl = (TableLayout) root.findViewById(R.id.tableLayout);
		for(Canteen cant : getCanteens())
			buildDetails(inflater, tl, cant);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		show_next_day = sharedPreferences.getBoolean("mostra", true);

		
		return root;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		
		TableLayout v = (TableLayout) getView().findViewById(R.id.tableLayout);
		v.removeAllViews();
		
		for(Canteen cant : getCanteens())
			buildDetails(inflat, v, cant);
	}
	
	private ArrayList<Canteen> getCanteens(){
		String title = getArguments().getString("title");
		ArrayList<Canteen> list = null;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean varias = sharedPreferences.getBoolean("varias", false);
		
		ArrayList<Canteen> listOrigin = null;
		
		if(title.equals("Santiago")){
			listOrigin = menu.getSantiago();
		}
		else if(title.equals("Crasto")){
			listOrigin = menu.getCrasto();
		}
		else if(title.equals("SnackBar")){
			listOrigin = menu.getSnackBar();
		}
		
		if(varias)
			return listOrigin;
		else{
			list = new ArrayList<Canteen>();
			list.add(listOrigin.get(0));
		}
		
		return list;
	}
	
	private void buildDetails(final LayoutInflater inflater, TableLayout tl, final Canteen cant){
		final TableLayout body = (TableLayout)inflater.inflate(R.layout.table_menu_title, null);
		final TableLayout body_list = (TableLayout) body.findViewById(R.id.menu_title_list);

		tl.addView(body);
		
		final Button btL = (Button) body.findViewById(R.id.bt_title_lunch);
		final Button btD = (Button) body.findViewById(R.id.bt_title_dinner);
		TextView tvDate = (TextView) body.findViewById(R.id.tv_title_date);
		final ImageView ib = (ImageView) body.findViewById(R.id.img_share);

		
		tvDate.setText(cant.getMicroDate());
		tvDate.setGravity(Gravity.RIGHT); 
		
		if(show_next_day){ 
			Calendar c = Calendar.getInstance(); 
			int hour = c.get(Calendar.HOUR_OF_DAY);
			
			if(hour >= 0 && hour < 16){
				buildList(inflater, body_list, cant.lunch, false, btL, btD, ib);
			}
			else{
				buildList(inflater, body_list, cant.dinner, true, btL, btD, ib);
			}
			show_next_day = false;
		}else{	
			buildList(inflater, body_list, cant.lunch, false, btL, btD, ib);
		}
		
		btL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buildList(inflater, body_list, cant.lunch, false, btL, btD, ib);
			}
		});
		btD.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buildList(inflater, body_list, cant.dinner, true, btL, btD, ib);
			} 
		});
	}
	
	private void buildList(LayoutInflater inflater, TableLayout tl, Menu menu, boolean what,  Button lunch, Button dinner, ImageView ib){
		View v = null;
		boolean t = true;
		String tmp = "";
		final String shareMsg;
		
		if(!what){
			lunch.setBackgroundResource(R.drawable.table_menu_title_select);
			dinner.setBackgroundResource(R.drawable.table_menu_title_deselect);
			lunch.setClickable(false);
			dinner.setClickable(true);
			tmp = "Almoo:\n";
		}else{
			lunch.setBackgroundResource(R.drawable.table_menu_title_deselect);
			dinner.setBackgroundResource(R.drawable.table_menu_title_select);
			lunch.setClickable(true);
			dinner.setClickable(false);
			tmp = "Jantar:\n";
		}
		
		Animation animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
		animation.setDuration(1000);
		
		tl.startAnimation(animation);
		tl.removeAllViews();
		
		if(!menu.isDisabled()) {
			for(Plate p : menu.getPlates()) {
				if(p.getName().length() > 0 && showPlate(p)){
					v = buildRow(inflater, p);
					if(t)
						v.setBackgroundResource(R.drawable.border1);
					else
						v.setBackgroundResource(R.drawable.border2);
					t = !t;
					tl.addView(v);
				}
				if(p.getType().contains("Prato") && p.getName() != ""  && p.getName().length()>0){
					tmp += p.getName() + "; "; 
				}
			}
			ib.setVisibility(View.VISIBLE);
		} else {
			v = buildRow(inflater, new Plate("",menu.getDisabledText()));
			v.setBackgroundResource(R.drawable.border1);
			tl.addView(v);
			ib.setVisibility(View.GONE);
		}
		
		shareMsg = tmp;
		
		ib.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ementas do "+getArguments().getString("title") );
				String shareMessage = getArguments().getString("title") +" - "+ shareMsg;
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
				startActivity(Intent.createChooser(shareIntent, "Partilhar para onde:"));
			}
		});
	}
	
	private View buildRow(LayoutInflater inflater, Plate plate){
		View v = inflater.inflate(R.layout.table_menu_item, null);
		
		TextView tv_type = (TextView) v.findViewById(R.id.tx_type);
		TextView tv_name = (TextView) v.findViewById(R.id.tx_name);
		
		tv_type.setText(plate.getType());
		tv_name.setText(plate.getName());
		
		return v;
	}

	private boolean showPlate(Plate p){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean mostraTudo = sharedPreferences.getBoolean("info", true);
		
		if(!mostraTudo){
			if(p.getType().contains("Prato") || p.getType().contains("Sopa")){
				return true;
			}
			else{
				return false;
			}
		}else{
			return true;
		}
	}
	
}
