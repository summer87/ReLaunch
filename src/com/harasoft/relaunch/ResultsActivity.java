package com.harasoft.relaunch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.harasoft.relaunch.ReLaunch.ViewHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ResultsActivity extends Activity {
	final String                  TAG = "Results";
	final int                     CNTXT_MENU_RMFAV = 1;
	final int                     CNTXT_MENU_RMFILE = 2;
	final int                     CNTXT_MENU_CANCEL = 3;
	final int                     CNTXT_MENU_MOVEUP = 4;
	final int                     CNTXT_MENU_MOVEDOWN = 5;
	final int                     CNTXT_MENU_MARK_FINISHED = 6;
	final int                     CNTXT_MENU_MARK_READING = 7;
	final int                     CNTXT_MENU_MARK_FORGET = 8;
	ReLaunchApp                   app;
    HashMap<String, Drawable>     icons;
    String                        listName;
    String                        title;
    Boolean                       rereadOnStart = false;
    SharedPreferences             prefs;
    FLSimpleAdapter               adapter;
    ListView                      lv;
	List<HashMap<String, String>> itemsArray = new ArrayList<HashMap<String, String>>();

    static class ViewHolder {
        TextView  tv1;
        TextView  tv2;
        ImageView iv;
    }
	class FLSimpleAdapter extends ArrayAdapter<HashMap<String, String>> {
    	FLSimpleAdapter(Context context, int resource, List<HashMap<String, String>> data)
    	{
    		super(context, resource, data);
    	}
    	
    	@Override
		public int getCount() {
    		return itemsArray.size();
		}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		ViewHolder holder;
            View       v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.results_item, null);
                holder = new ViewHolder();
                holder.tv1 = (TextView) v.findViewById(R.id.res_dname);
                holder.tv2 = (TextView) v.findViewById(R.id.res_fname);
                holder.iv  = (ImageView) v.findViewById(R.id.res_icon);
                v.setTag(holder);
            }
            else
            	holder = (ViewHolder) v.getTag();

        	TextView  tv1 = holder.tv1;
        	TextView  tv2 = holder.tv2;
        	ImageView iv = holder.iv;

            if (position >= itemsArray.size())
            {
            	v.setVisibility(View.INVISIBLE);
            	tv1.setVisibility(View.INVISIBLE);
            	tv2.setVisibility(View.INVISIBLE);
            	iv.setVisibility(View.INVISIBLE);
            	return v;
            }
            HashMap<String, String> item = itemsArray.get(position);
            if (item != null) {
            	String    fname = item.get("fname");
            	String    dname = item.get("dname");
            	String    fullName = dname + "/" + fname;
            	boolean   setBold = false;
            	boolean   useFaces  = prefs.getBoolean("showNew", true);

        		if (useFaces)
        		{
        			if (app.history.containsKey(fullName))
        			{
        				if (app.history.get(fullName) == app.READING)
        				{
        					tv1.setBackgroundColor(getResources().getColor(R.color.file_reading_bg));
        					tv1.setTextColor(getResources().getColor(R.color.file_reading_fg));				
        					tv2.setBackgroundColor(getResources().getColor(R.color.file_reading_bg));
        					tv2.setTextColor(getResources().getColor(R.color.file_reading_fg));				
        				}
        				else if (app.history.get(fullName) == app.FINISHED)
        				{
        					tv1.setBackgroundColor(getResources().getColor(R.color.file_finished_bg));
        					tv1.setTextColor(getResources().getColor(R.color.file_finished_fg));	
        					tv2.setBackgroundColor(getResources().getColor(R.color.file_finished_bg));
        					tv2.setTextColor(getResources().getColor(R.color.file_finished_fg));	
        				}
        				else
        				{
        					tv1.setBackgroundColor(getResources().getColor(R.color.file_unknown_bg));
        					tv1.setTextColor(getResources().getColor(R.color.file_unknown_fg));	
           					tv2.setBackgroundColor(getResources().getColor(R.color.file_unknown_bg));
        					tv2.setTextColor(getResources().getColor(R.color.file_unknown_fg));	
        				}
        			}
        			else
        			{
        				tv1.setBackgroundColor(getResources().getColor(R.color.file_new_bg));
        				tv1.setTextColor(getResources().getColor(R.color.file_new_fg));
        				tv2.setBackgroundColor(getResources().getColor(R.color.file_new_bg));
        				tv2.setTextColor(getResources().getColor(R.color.file_new_fg));
        				if (getResources().getBoolean(R.bool.show_new_as_bold))
        					setBold = true;
        			}
        		}

            	String rdrName = app.readerName(fname);
            	if (rdrName.equals("Nope"))
            		iv.setImageDrawable(getResources().getDrawable(R.drawable.file_notok));
            	else
            	{
            		if (icons.containsKey(rdrName))
            			iv.setImageDrawable(icons.get(rdrName));
            		else
            			iv.setImageDrawable(getResources().getDrawable(R.drawable.file_ok));
            	}
            	
        		if (useFaces)
        		{
        			SpannableString s = new SpannableString(fname);
        			s.setSpan(new StyleSpan(setBold ? Typeface.BOLD : Typeface.NORMAL), 0, fname.length(), 0);
        			tv1.setText(dname);
        			tv2.setText(s);
        		}
        		else
        		{
    				tv1.setBackgroundColor(getResources().getColor(R.color.normal_bg));
    				tv1.setTextColor(getResources().getColor(R.color.normal_fg));
    				tv2.setBackgroundColor(getResources().getColor(R.color.normal_bg));
    				tv2.setTextColor(getResources().getColor(R.color.normal_fg));
    				tv1.setText(dname);
        			tv2.setText(fname);
        		}
            }
            return v;
    	}
    }

	private void redrawList()
	{
		List<HashMap<String, String>> newItemsArray = new ArrayList<HashMap<String, String>>();

		for (HashMap<String, String> item : itemsArray)
		{
			if (app.filterFile(item.get("dname"), item.get("fname")))
					newItemsArray.add(item);
		}
		itemsArray = newItemsArray;
		adapter.notifyDataSetChanged();
	}

	private void start(Intent i)
    {
    	if (i != null)
    		startActivity(i);
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.results_layout);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	app = ((ReLaunchApp)getApplicationContext());
    	icons = app.getIcons();
    	
        // Recreate readers list
        final Intent data = getIntent();
        if (data.getExtras() == null)
        {
        	setResult(Activity.RESULT_CANCELED);
        	finish();
        }
        listName = data.getExtras().getString("list");
        title = data.getExtras().getString("title");
        rereadOnStart = data.getExtras().getBoolean("rereadOnStart");        

    	((ImageButton)findViewById(R.id.results_btn)).setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) { finish(); }});

    	lv = (ListView) findViewById(R.id.results_list);
    	((TextView)findViewById(R.id.results_title)).setText(title + " (" + app.getList(listName).size() + ")");

    	//Log.d(TAG, "listname=" + listName + " title=" + title);
    	for (String[] n : app.getList(listName))
    	{
    		if (app.filterFile(n[0], n[1]))
    		{
    			HashMap<String, String> item = new HashMap<String, String>();
	    		item.put("dname", n[0]);
	    		item.put("fname", n[1]);
	    		//Log.d(TAG, n[0] + ":" + n[1]);
	    		itemsArray.add(item);
    		}
    	}
    	adapter = new FLSimpleAdapter(this, R.layout.results_item, itemsArray);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
             	HashMap<String, String> item = itemsArray.get(position);      
 
             	String fullName = item.get("dname") + "/" + item.get("fname");
             	String fileName = item.get("fname");
             	if (!app.readerName(fileName).equals("Nope"))
            	{
            		// Launch reader
            		if (app.askIfAmbiguous)
            		{
            			List<String> rdrs = app.readerNames(item.get("fname"));
            			if (rdrs.size() < 1)
            				return;
            			else if (rdrs.size() == 1)
            				start(app.launchReader(rdrs.get(0), fullName));
            			else
            			{
            			   	final CharSequence[] applications = rdrs.toArray(new CharSequence[rdrs.size()]);
            			   	final String rdr1 = fullName;
    						AlertDialog.Builder builder = new AlertDialog.Builder(ResultsActivity.this);
    						builder.setTitle("Select application");
    						builder.setSingleChoiceItems(applications, -1, new DialogInterface.OnClickListener() {
    						    public void onClick(DialogInterface dialog, int i) {
    						    	start(app.launchReader((String)applications[i], rdr1));
    		            			dialog.dismiss();
    						    }
    						});
    						AlertDialog alert = builder.create();
    						alert.show();

            			}
            		}
            		else
            			start(app.launchReader(app.readerName(fileName), fullName));
            	}
 			}});
	}

	@Override
	protected void onStart() {
		if (rereadOnStart)
		{
			itemsArray = new ArrayList<HashMap<String, String>>();
			for (String[] n : app.getList(listName))
			{
	    		if (app.filterFile(n[0], n[1]))
	    		{
	    			HashMap<String, String> item = new HashMap<String, String>();
	    			item.put("dname", n[0]);
	    			item.put("fname", n[1]);
	    			//Log.d(TAG, n[0] + ":" + n[1]);
	    			itemsArray.add(item);
	    		}
			}
		}
		redrawList();
		super.onStart();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
		int pos = info.position;
		HashMap<String, String> i = itemsArray.get(pos);
		final String dr = i.get("dname");
		final String fn = i.get("fname");
		String fullName = dr + "/" + fn;

		if (listName.equals("favorites"))
		{
			if (pos > 0)
				menu.add(Menu.NONE, CNTXT_MENU_MOVEUP, Menu.NONE, "Move one position up");
			if (pos < (itemsArray.size()-1))
				menu.add(Menu.NONE, CNTXT_MENU_MOVEDOWN, Menu.NONE, "Move one position down");
    		menu.add(Menu.NONE, CNTXT_MENU_RMFAV, Menu.NONE, "Remove from favorites");
    		menu.add(Menu.NONE, CNTXT_MENU_RMFILE, Menu.NONE, "Delete file");
    		menu.add(Menu.NONE, CNTXT_MENU_CANCEL, Menu.NONE, "Cancel");
		}
		else if (listName.equals("lastOpened"))
		{
			if (app.history.containsKey(fullName))
			{
				if (app.history.get(fullName) == app.READING)
					menu.add(Menu.NONE, CNTXT_MENU_MARK_FINISHED, Menu.NONE, "Mark as read");
				else if (app.history.get(fullName) == app.FINISHED)
					menu.add(Menu.NONE, CNTXT_MENU_MARK_READING, Menu.NONE, "Remove \"read\" mark");
				menu.add(Menu.NONE, CNTXT_MENU_MARK_FORGET, Menu.NONE, "Forget all marks");
			}
			else
				menu.add(Menu.NONE, CNTXT_MENU_MARK_FINISHED, Menu.NONE, "Mark as read");
			menu.add(Menu.NONE, CNTXT_MENU_RMFILE, Menu.NONE, "Delete file");
    		menu.add(Menu.NONE, CNTXT_MENU_CANCEL, Menu.NONE, "Cancel");			
		}
		else if (listName.equals("searchResults"))
		{
			if (pos > 0)
				menu.add(Menu.NONE, CNTXT_MENU_MOVEUP, Menu.NONE, "Move one position up");
			if (pos < (itemsArray.size()-1))
				menu.add(Menu.NONE, CNTXT_MENU_MOVEDOWN, Menu.NONE, "Move one position down");
			if (app.history.containsKey(fullName))
			{
				if (app.history.get(fullName) == app.READING)
					menu.add(Menu.NONE, CNTXT_MENU_MARK_FINISHED, Menu.NONE, "Mark as read");
				else if (app.history.get(fullName) == app.FINISHED)
					menu.add(Menu.NONE, CNTXT_MENU_MARK_READING, Menu.NONE, "Remove \"read\" mark");
				menu.add(Menu.NONE, CNTXT_MENU_MARK_FORGET, Menu.NONE, "Forget all marks");
			}
			else
				menu.add(Menu.NONE, CNTXT_MENU_MARK_FINISHED, Menu.NONE, "Mark as read");

    		menu.add(Menu.NONE, CNTXT_MENU_RMFILE, Menu.NONE, "Delete file");
    		menu.add(Menu.NONE, CNTXT_MENU_CANCEL, Menu.NONE, "Cancel");			
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (item.getItemId() == CNTXT_MENU_CANCEL)
			return true;

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final int pos = info.position;
		HashMap<String, String> i = itemsArray.get(pos);
		final String dname = i.get("dname");
		final String fname = i.get("fname");
		String fullName = dname + "/" + fname;

		switch (item.getItemId())
		{
		case CNTXT_MENU_MARK_READING:
			app.history.put(fullName, app.READING);
			redrawList();
			break;
		case CNTXT_MENU_MARK_FINISHED:
			app.history.put(fullName, app.FINISHED);
			redrawList();
			break;
		case CNTXT_MENU_MARK_FORGET:
			app.history.remove(fullName);
			redrawList();
			break;
		case CNTXT_MENU_RMFAV:
			app.removeFromList("favorites", dname, fname);
			itemsArray.remove(pos);
			redrawList();   
			break;
		case CNTXT_MENU_MOVEUP:
			if (pos > 0)
			{
				List<String[]>          f = app.getList(listName);
				HashMap<String, String> it = itemsArray.get(pos);
				String[]                fit = f.get(pos);

				itemsArray.remove(pos);
				f.remove(pos);
				itemsArray.add(pos-1, it);
				f.add(pos-1, fit);
				app.setList(listName, f);
				redrawList();
			}
			break;
		case CNTXT_MENU_MOVEDOWN:
			if (pos < (itemsArray.size()-1))
			{
				List<String[]>          f = app.getList(listName);
				HashMap<String, String> it = itemsArray.get(pos);
				String[]                fit = f.get(pos);

				int size = itemsArray.size();
				itemsArray.remove(pos);
				f.remove(pos);
				if (pos+1 >= size-1)
				{
					itemsArray.add(it);
					f.add(fit);
				}
				else
				{
					itemsArray.add(pos+1, it);
					f.add(pos+1, fit);
				};
				app.setList(listName, f);
				redrawList();				
			}
			break;
		case CNTXT_MENU_RMFILE:
			if (prefs.getBoolean("confirmFileDelete", true))
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Are you sure to delete file \"" + fname + "\"?");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss();
							if (app.removeFile(dname, fname))
							{
								itemsArray.remove(pos);
								redrawList();
							}
						}});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
            			dialog.dismiss();
					}});
				builder.show();
			}
			else if (app.removeFile(dname, fname))
			{
				itemsArray.remove(pos);
				redrawList();
			}
			break;
		}
		return true;
	}
}
