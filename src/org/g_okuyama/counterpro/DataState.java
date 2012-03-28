package org.g_okuyama.counterpro;

import java.io.*;
import java.net.URLEncoder;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class DataState extends TabActivity implements OnTabChangeListener{
	private static final int MENU_EXPORT = 0;
    private static final int MENU_CHART = 1;
    private static final int MENU_REMOVE = 2;
	
    public static final String EXTENDED_MAP = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.";
    int EXTENDED_MAP_LENGTH = EXTENDED_MAP.length();
    
	static int dbid = -1;
	
	String title = "";
	String count = "";
	String date = "";
	//String place = "";
	String timecount = "";
	String button = "";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //intentから選択項目を取り出す
        Bundle extras = getIntent().getExtras();
        dbid = extras.getInt("id");

        //カウント情報の詳細画面表示
        setDetailDisplay();
    }

    private void setDetailDisplay(){
    	setContentView(R.layout.tabs);

    	// TabHostのインスタンスを取得
    	TabHost tabs = getTabHost();
    	tabs.setOnTabChangedListener(this);
        
    	TabSpec tab1 = tabs.newTabSpec("tab1");
    	tab1.setIndicator(getString(R.string.ds_counter), getResources().getDrawable(R.drawable.tab_circle));
    	tab1.setContent(R.id.table1_1);
    	tabs.addTab(tab1);
    	// 初期表示のタブ設定
    	tabs.setCurrentTab(0);

    	//データ取得
    	getDetailData();

    	if(button.equals("1")){
    		if(count.equals("0")){
    			timecount = "Unknown 0";
    		}
    		
    		String[] str = timecount.split(",");
    	
    		//TableLayout形式で時刻とカウント数を表示
    		for(String s: str){
    			String[] str2 = s.split(" ");
    		
    			TableLayout tl = (TableLayout)findViewById(R.id.table1_1);
    			TableRow tr = new TableRow(this);
    			TextView tv1 = new TextView(this);
    			tv1.setText(str2[0]);
    			tv1.setPadding(10, 0, 0, 0);
    			TextView tv2 = new TextView(this);
    			tv2.setText(str2[1]);
    			tv2.setPadding(0, 0, 10, 0);
    			tr.addView(tv1);
    			tr.addView(tv2);
    			tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
    														LayoutParams.WRAP_CONTENT));
    		}

    	}else{	/*button=2*/
        	//ボタンB用のタブ設定
        	TabSpec tab2 = tabs.newTabSpec("tab2");
        	tab2.setIndicator(getString(R.string.ds_counter), getResources().getDrawable(R.drawable.tab_cross));
        	tab2.setContent(R.id.table2_1);
        	tabs.addTab(tab2);
        	
    		if(count.equals("0,0")){
    			timecount = "Unknown 0:::Unknown 0";
    		}
    		
    		//まずボタンA、Bの保存データを分ける
    		String[] button_str = timecount.split(":::");

    		if(button_str[0].equals("")){
    			button_str[0] = "Unknown 0";
    		}
    		//button_str[1].equals("")だと例外が発生したため、以下とする
    		else if(button_str.length == 1){
    			timecount = button_str[0] + ":::" + "Unknown 0";
    			button_str = timecount.split(":::");
    		}
    		
    		//次にボタンA、Bそれぞれのデータを表示する
    		String[] str_a = button_str[0].split(",");
    		//TableLayout形式で時刻とカウント数を表示
    		for(String s: str_a){
    			String[] str_a2 = s.split(" ");

    			TableLayout tl = (TableLayout)findViewById(R.id.table1_1);
    			TableRow tr = new TableRow(this);
    			TextView tv1 = new TextView(this);
    			tv1.setText(str_a2[0]);
    			tv1.setPadding(20, 0, 0, 0);
    			TextView tv2 = new TextView(this);
    			tv2.setText(str_a2[1]);
    			tv2.setPadding(0, 0, 20, 0);
    			tr.addView(tv1);
    			tr.addView(tv2);
    			tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
    														LayoutParams.WRAP_CONTENT));
    		}

    		//ボタンB
    		String[] str_b = button_str[1].split(",");
    		//TableLayout形式で時刻とカウント数を表示
    		for(String s: str_b){
    			String[] str_b2 = s.split(" ");
    		
    			TableLayout tl = (TableLayout)findViewById(R.id.table2_1);
    			TableRow tr = new TableRow(this);
    			TextView tv1 = new TextView(this);
    			tv1.setText(str_b2[0]);
    			tv1.setPadding(20, 0, 0, 0);
    			TextView tv2 = new TextView(this);
    			tv2.setText(str_b2[1]);
    			tv2.setPadding(0, 0, 20, 0);
    			tr.addView(tv1);
    			tr.addView(tv2);
    			tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
    														LayoutParams.WRAP_CONTENT));
    		}
    	}
    }
    
    private void getDetailData(){
    	//DBを取得
    	DatabaseHelper helper = new DatabaseHelper(this);
    	SQLiteDatabase db = helper.getWritableDatabase();
    	String query = "select * from counter where rowid = ?;";
    	Cursor c = db.rawQuery(query, new String[]{Integer.toString(dbid)});

    	c.moveToFirst();
		title = c.getString(1);
		count = c.getString(2);
		date = c.getString(3);
		//place = c.getString(4);
		timecount = c.getString(5);
		button = c.getString(6);
		
		c.close();
    }

    //オプションメニューの作成
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //オプションメニュー項目作成(「エクスポート」)
        MenuItem saveItem = menu.add(0, MENU_EXPORT, 0 ,R.string.option_export);
        saveItem.setIcon(android.R.drawable.ic_menu_upload);

        //オプションメニュー項目作成(「グラフ作成」)
        MenuItem chartItem = menu.add(0, MENU_CHART, 0 ,R.string.option_chart);
        chartItem.setIcon(android.R.drawable.ic_menu_report_image);

        //オプションメニュー項目作成(「削除」)
        MenuItem clearItem = menu.add(0, MENU_REMOVE, 0 ,R.string.option_remove);
        clearItem.setIcon(android.R.drawable.ic_menu_delete);

        return true;
    }
    
    //オプションメニュー選択時のリスナ
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case MENU_EXPORT:
    		export();
    		break;
    		
        case MENU_CHART:
            makeChart();
            break;

        case MENU_REMOVE:
            remove();
            break;

    	default:
    		//何もしない
    	}

    	return true;
    }
    
    private void export(){
    	//CSVまたはメールでエクスポート
		new AlertDialog.Builder(DataState.this)
		.setTitle("\"" + title + "\"" + getString(R.string.dm_dialog_export))
		.setItems(R.array.dm_howtoexport, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int item) {
				switch(item){
				case 0://メールで送信
					exportByMail();
					break;
				case 1://SDに書込み
					exportToSD();
					break;
				case 2://キャンセル
					//何もしない
					break;
				}
			}
		}).show();    	
    }

    
    private void exportByMail(){
		/*当初はCSVを予定していたが、GmailがCSVファイルの添付に対応しておらず、
		 * 送信時に添付されない現象が発生したため、断念。
    	FileOutputStream fos = null;
    	BufferedWriter out = null;
    	
    	try{
    		//CSVファイルの作成
    		fos = this.openFileOutput("data.csv", 0);
    		out = new BufferedWriter(new OutputStreamWriter(fos));
    		out.write("題名,カウント数,日時");
    		out.write(title + "," + count + "," + date);
    		out.write(System.getProperty("line.separator"));
    		out.flush();

    	}catch(FileNotFoundException fe){
    		//とりあえず省略
        	new AlertDialog.Builder(this)
        	.setTitle("ファイル作成失敗")
        	.setMessage("ファイル作成に失敗しました")
        	.setPositiveButton("はい", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			//何もしない
        		}
        	})
    		.show();        		

    	}catch(IOException ie){
    		//同じく省略
        	new AlertDialog.Builder(this)
        	.setTitle("ファイル作成失敗")
        	.setMessage("ファイル作成に失敗しました")
        	.setPositiveButton("はい", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			//何もしない
        		}
        	})
    		.show();        		
    	}
    	
    	//CSVファイルをメールで送信
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.putExtra(Intent.EXTRA_SUBJECT, "send CSV file");
    	intent.putExtra(Intent.EXTRA_TEXT, "send " + "[" + title + "]");
    	intent.setType("text/csv");
    	intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///data/data/com.sample.counter/files/data.csv"));
    	startActivity(intent);
    	*/
    	
    	String sendstr = getExportString();
		
		//データをメールで送信
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	//メアドが設定画面で設定されている場合は設定
    	String ad = CounterPreference.getMailAddress(this);
    	if(ad != null){
    		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ad});
    	}
    	intent.putExtra(Intent.EXTRA_SUBJECT, "send \"" + title + "\" data");
    	intent.putExtra(Intent.EXTRA_TEXT, sendstr);
    	//これがないと落ちる
    	intent.setType("plain/text");
    	try{
    		startActivity(intent);
    	}catch(ActivityNotFoundException e){
    		new AlertDialog.Builder(this)
    		.setTitle(R.string.dm_error)
    		.setMessage(R.string.dm_missing_mailer)
    		.setPositiveButton(R.string.cnt_ok, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				//何もしない
    			}
    		}).show();
    	}
    }
    
    private void exportToSD(){
    	String exstr = getExportString();
        File file = new File(Environment.getExternalStorageDirectory(), "/Counter");

        try{
            file.mkdir();
            File savefile = new File(file.getPath(), getCurrentDate() + ".txt");
        	FileOutputStream fos = new FileOutputStream(savefile);
        	OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        	BufferedWriter bw = new BufferedWriter(osw);
        	//SDへの書込み
        	bw.write(exstr);
        	bw.flush();
        	bw.close();
     	
        	Toast.makeText(this, R.string.dm_export_sd_ok, Toast.LENGTH_SHORT).show();

        }catch(Exception e){
        	Toast.makeText(this, R.string.dm_export_sd_ng, Toast.LENGTH_SHORT).show();        	
        }
    }
    
    private String getExportString(){
		String crlf = System.getProperty("line.separator");

		String exstr;
		if(button.equals("2")){
			String[] cstr = count.split(",");
			exstr = getString(R.string.dm_export_format) + crlf
					+ title + ","
					+ cstr[0] + " " + cstr[1] + ","
					+ date;
					/*
					+ ","
					+ place;
					*/

			exstr += crlf + crlf;

			//ボタン数の区切り
			String[] bstr = timecount.split(":::");
			for(int i = 0; i < bstr.length; i++){

				exstr += getString(R.string.dm_button) + Integer.toString(i+1) + crlf;
				exstr += getString(R.string.dm_time_count) + crlf;

				String[] str = bstr[i].split(",");

				for(String s: str){
					String tmp = s.replace(" ", ",");
					exstr += tmp + crlf;
				}
			}
			
		}else{
			exstr = getString(R.string.dm_export_format) + crlf
			+ title + "," + count + "," + date;
			/*
			+ "," + place;
			*/

			exstr += crlf + crlf;
			exstr += getString(R.string.dm_time_count) + crlf;

			String[] str = timecount.split(",");

			for(String s: str){
				String tmp = s.replace(" ", ",");
				exstr += tmp + crlf;
			}
		}

		return exstr;
    }
    
    private void makeChart(){
        String timestr1 = "";
        String countstr1 = "";
        String timestr2 = "";
        String countstr2 = "";
        
        if(button.equals("1")){
            String[] str = timecount.split(",");
            
            for(String s: str){
                String[] str2 = s.split(" ");
                if(str2[0].equals("Unknown")){
                    finish();
                }
                
                if(timestr1.equals("")){
                    timestr1 = str2[0];
                    countstr1 = str2[1];
                }
                else{
                    timestr1 += "," + str2[0];
                    countstr1 += "," + str2[1];
                }
            }
        }
        else if(button.equals("2")){
        	if(count.equals("0,0")){
        		finish();
        	}
        	String[] bstr = timecount.split(":::");
        	//ボタン1
            String[] str_a = bstr[0].split(",");
            for(String s: str_a){
                String[] str2 = s.split(" ");

                if(timestr1.equals("")){
                    timestr1 = str2[0];
                    countstr1 = str2[1];
                }
                else{
                    timestr1 += "," + str2[0];
                    countstr1 += "," + str2[1];
                }
            }            
            //ボタン2
            String[] str_b = bstr[1].split(",");
            for(String s: str_b){
                String[] str2 = s.split(" ");

                if(timestr2.equals("")){
                    timestr2 = str2[0];
                    countstr2 = str2[1];
                }
                else{
                    timestr2 += "," + str2[0];
                    countstr2 += "," + str2[1];
                }
            }
        }
        else{
            return;
        }
        //Google Chart API用のURL作成
        String[] button1_array = countstr1.split(",");
        //一番大きい数値を検索<-グラフの最大値に利用
        int max1 = 0;
        for(String s: button1_array){
            int tmp = Integer.parseInt(s);
            if(max1 < tmp){
                max1 = tmp;
            }
        }

        String[] button2_array = null;
        String[] zero = {"0"};
        if(button.equals("2")){
            button2_array = countstr2.split(",");

            //一番大きい数値を検索
            int max2 = 0;
            for(String s: button2_array){
                int tmp = Integer.parseInt(s);
                if(max2 < tmp){
                    max2 = tmp;
                }
            }
            if(max2 > max1){
                max1 = max2;
            }

            //ボタン1と2のうち、カウント数が多いほうを算出
            //少ないほうを0で埋める
            if(button1_array.length > button2_array.length){
            	for(int i=button2_array.length;i<button1_array.length;i++){
            		countstr2 += ",0";
            	}
            	button2_array = countstr2.split(",");
            }
            else if(button1_array.length < button2_array.length){
            	for(int i=button1_array.length;i<button2_array.length;i++){
            		countstr1 += ",0";
            	}
            	button1_array = countstr1.split(",");
            }
        }

        //カウント数をGoogle Chart APIの拡張形式(chd=e)に値を変更
        String data1 = encodeChartData(button1_array, max1);
        String datastr = "chd=e:" + data1;
        if(button.equals("2")){
            String data2 = encodeChartData(button2_array, max1);
            datastr += "," + data2;
        }

        //x軸のラベルを作成(時間表示)
        String labelstr = "chxl=0:|";
        String[] time_a = timestr1.split(",");
        String[] time_b = timestr2.split(",");
        String primary;
        if(time_a.length >= time_b.length){
        	primary = timestr1;
        }
        else{
        	primary = timestr2;
        }
        labelstr += primary.replace(",", "|");

        String t = "";
        try {
            t = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        String url = "";
        url = "http://chart.apis.google.com/chart?"
        	+ "chs=540x250"
        	+ "&" + datastr
        	+ "&cht=lc"
        	+ "&chtt=" + t
        	+ "&chxt=x,y"
            + "&chxr=1,0," + max1   //y軸のラベルを作成(グラフの最大値をカウント数の最大値とする)
        	+ "&" + labelstr
        	+ "&chco=ff0000,0000ff"
        	+ "&chg=50,50"
        	+ "&chf=bg,s,EFEFEF|c,lg,0,ffffff,0,ececec,0.5,ffffff,1";
        
        if(button.equals("2")){
            String b1 = "";
            String b2 = "";
            try {
                b1 = URLEncoder.encode(getString(R.string.ds_button_o), "UTF-8");
                b2 = URLEncoder.encode(getString(R.string.ds_button_x), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }            
            url += "&chdl=" + b1 + "|" + b2;
        }
        
        Log.d("Counter", "URL = " + url);
        
        //intent作成
        Intent i = new Intent(this, Chart.class);
        //選択項目のDBのIDを渡す
        i.putExtra("url", url);
        startActivity(i);
    }
    
    private void remove(){
    	//確認ダイアログの表示
    	new AlertDialog.Builder(this)
    	.setTitle(R.string.dm_delete)
    	.setMessage("\"" + title + "\" " + getString(R.string.dm_delete_confirm))
    	.setPositiveButton(R.string.cnt_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//保存データ削除処理
				removeData();

				//Activityを終了し、前画面に戻る
				finish();
			}
		})
		.setNegativeButton(R.string.cnt_ng, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//何もしない
			}
		})
		.show();
    }
    
    private void removeData(){
    	//DB取得
    	DatabaseHelper helper = new DatabaseHelper(this);
    	SQLiteDatabase db = helper.getWritableDatabase();
    	//データを削除
    	db.delete("counter", "rowid = ?", new String[]{Integer.toString(dbid)});
    }
    
    String getCurrentDate(){
    	Calendar cal1 = Calendar.getInstance();
        int year = cal1.get(Calendar.YEAR);
        int mon = cal1.get(Calendar.MONTH) + 1;
        int d = cal1.get(Calendar.DATE);
        int h = cal1.get(Calendar.HOUR_OF_DAY);
        int min = cal1.get(Calendar.MINUTE);
        int sec = cal1.get(Calendar.SECOND);
        
        String month = Integer.toString(mon);
        //1桁月の場合は0を入れる
        if(month.length() == 1){
        	month = "0" + month;
        }

        String day = Integer.toString(d);
        //1桁日の場合は0を入れる
        if(day.length() == 1){
        	day = "0" + day;
        }

        String hour = Integer.toString(h);
        //1桁時の場合は0を入れる
        if(hour.length() == 1){
        	hour = "0" + hour;
        }
        String minute = Integer.toString(min); 
        //1桁分の場合は0を入れる
        if(minute.length() == 1){
        	minute = "0" + minute;
        }
        
        String second = Integer.toString(sec);
        //1桁秒の場合は0を入れる
        if(second.length() == 1){
        	second = "0" + second;
        }

        return Integer.toString(year) + month + day + hour + minute + second;
    }
    
    String encodeChartData(String[] arrVals, int maxVal) {
        int EXTENDED_MAP_LENGTH = EXTENDED_MAP.length();
        //String chartData = "e:";
        String chartData = "";

        int len = arrVals.length;
        for(int i = 0; i < len; i++) {
            int numericVal = Integer.parseInt(arrVals[i]);
            double scaledVal = Math.floor(EXTENDED_MAP_LENGTH * EXTENDED_MAP_LENGTH * numericVal / maxVal);

            if(scaledVal > (EXTENDED_MAP_LENGTH * EXTENDED_MAP_LENGTH) - 1) {
                chartData += "..";
            } else if (scaledVal < 0) {
                chartData += "__";
            } else {
                double quotient = Math.floor(scaledVal / EXTENDED_MAP_LENGTH);
                double remainder = scaledVal - EXTENDED_MAP_LENGTH * quotient;
                chartData += String.valueOf(EXTENDED_MAP.charAt((int)quotient))
                        + String.valueOf(EXTENDED_MAP.charAt((int)remainder));
            }
        }
        return chartData;
    }

	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub
		
	}
}
