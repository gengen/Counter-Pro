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
        
        //intent����I�����ڂ����o��
        Bundle extras = getIntent().getExtras();
        dbid = extras.getInt("id");

        //�J�E���g���̏ڍ׉�ʕ\��
        setDetailDisplay();
    }

    private void setDetailDisplay(){
    	setContentView(R.layout.tabs);

    	// TabHost�̃C���X�^���X���擾
    	TabHost tabs = getTabHost();
    	tabs.setOnTabChangedListener(this);
        
    	TabSpec tab1 = tabs.newTabSpec("tab1");
    	tab1.setIndicator(getString(R.string.ds_counter), getResources().getDrawable(R.drawable.tab_circle));
    	tab1.setContent(R.id.table1_1);
    	tabs.addTab(tab1);
    	// �����\���̃^�u�ݒ�
    	tabs.setCurrentTab(0);

    	//�f�[�^�擾
    	getDetailData();

    	if(button.equals("1")){
    		if(count.equals("0")){
    			timecount = "Unknown 0";
    		}
    		
    		String[] str = timecount.split(",");
    	
    		//TableLayout�`���Ŏ����ƃJ�E���g����\��
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
        	//�{�^��B�p�̃^�u�ݒ�
        	TabSpec tab2 = tabs.newTabSpec("tab2");
        	tab2.setIndicator(getString(R.string.ds_counter), getResources().getDrawable(R.drawable.tab_cross));
        	tab2.setContent(R.id.table2_1);
        	tabs.addTab(tab2);
        	
    		if(count.equals("0,0")){
    			timecount = "Unknown 0:::Unknown 0";
    		}
    		
    		//�܂��{�^��A�AB�̕ۑ��f�[�^�𕪂���
    		String[] button_str = timecount.split(":::");

    		if(button_str[0].equals("")){
    			button_str[0] = "Unknown 0";
    		}
    		//button_str[1].equals("")���Ɨ�O�������������߁A�ȉ��Ƃ���
    		else if(button_str.length == 1){
    			timecount = button_str[0] + ":::" + "Unknown 0";
    			button_str = timecount.split(":::");
    		}
    		
    		//���Ƀ{�^��A�AB���ꂼ��̃f�[�^��\������
    		String[] str_a = button_str[0].split(",");
    		//TableLayout�`���Ŏ����ƃJ�E���g����\��
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

    		//�{�^��B
    		String[] str_b = button_str[1].split(",");
    		//TableLayout�`���Ŏ����ƃJ�E���g����\��
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
    	//DB���擾
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

    //�I�v�V�������j���[�̍쐬
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //�I�v�V�������j���[���ڍ쐬(�u�G�N�X�|�[�g�v)
        MenuItem saveItem = menu.add(0, MENU_EXPORT, 0 ,R.string.option_export);
        saveItem.setIcon(android.R.drawable.ic_menu_upload);

        //�I�v�V�������j���[���ڍ쐬(�u�O���t�쐬�v)
        MenuItem chartItem = menu.add(0, MENU_CHART, 0 ,R.string.option_chart);
        chartItem.setIcon(android.R.drawable.ic_menu_report_image);

        //�I�v�V�������j���[���ڍ쐬(�u�폜�v)
        MenuItem clearItem = menu.add(0, MENU_REMOVE, 0 ,R.string.option_remove);
        clearItem.setIcon(android.R.drawable.ic_menu_delete);

        return true;
    }
    
    //�I�v�V�������j���[�I�����̃��X�i
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
    		//�������Ȃ�
    	}

    	return true;
    }
    
    private void export(){
    	//CSV�܂��̓��[���ŃG�N�X�|�[�g
		new AlertDialog.Builder(DataState.this)
		.setTitle("\"" + title + "\"" + getString(R.string.dm_dialog_export))
		.setItems(R.array.dm_howtoexport, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int item) {
				switch(item){
				case 0://���[���ő��M
					exportByMail();
					break;
				case 1://SD�ɏ�����
					exportToSD();
					break;
				case 2://�L�����Z��
					//�������Ȃ�
					break;
				}
			}
		}).show();    	
    }

    
    private void exportByMail(){
		/*������CSV��\�肵�Ă������AGmail��CSV�t�@�C���̓Y�t�ɑΉ����Ă��炸�A
		 * ���M���ɓY�t����Ȃ����ۂ������������߁A�f�O�B
    	FileOutputStream fos = null;
    	BufferedWriter out = null;
    	
    	try{
    		//CSV�t�@�C���̍쐬
    		fos = this.openFileOutput("data.csv", 0);
    		out = new BufferedWriter(new OutputStreamWriter(fos));
    		out.write("�薼,�J�E���g��,����");
    		out.write(title + "," + count + "," + date);
    		out.write(System.getProperty("line.separator"));
    		out.flush();

    	}catch(FileNotFoundException fe){
    		//�Ƃ肠�����ȗ�
        	new AlertDialog.Builder(this)
        	.setTitle("�t�@�C���쐬���s")
        	.setMessage("�t�@�C���쐬�Ɏ��s���܂���")
        	.setPositiveButton("�͂�", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			//�������Ȃ�
        		}
        	})
    		.show();        		

    	}catch(IOException ie){
    		//�������ȗ�
        	new AlertDialog.Builder(this)
        	.setTitle("�t�@�C���쐬���s")
        	.setMessage("�t�@�C���쐬�Ɏ��s���܂���")
        	.setPositiveButton("�͂�", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			//�������Ȃ�
        		}
        	})
    		.show();        		
    	}
    	
    	//CSV�t�@�C�������[���ő��M
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.putExtra(Intent.EXTRA_SUBJECT, "send CSV file");
    	intent.putExtra(Intent.EXTRA_TEXT, "send " + "[" + title + "]");
    	intent.setType("text/csv");
    	intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///data/data/com.sample.counter/files/data.csv"));
    	startActivity(intent);
    	*/
    	
    	String sendstr = getExportString();
		
		//�f�[�^�����[���ő��M
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	//���A�h���ݒ��ʂŐݒ肳��Ă���ꍇ�͐ݒ�
    	String ad = CounterPreference.getMailAddress(this);
    	if(ad != null){
    		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ad});
    	}
    	intent.putExtra(Intent.EXTRA_SUBJECT, "send \"" + title + "\" data");
    	intent.putExtra(Intent.EXTRA_TEXT, sendstr);
    	//���ꂪ�Ȃ��Ɨ�����
    	intent.setType("plain/text");
    	try{
    		startActivity(intent);
    	}catch(ActivityNotFoundException e){
    		new AlertDialog.Builder(this)
    		.setTitle(R.string.dm_error)
    		.setMessage(R.string.dm_missing_mailer)
    		.setPositiveButton(R.string.cnt_ok, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				//�������Ȃ�
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
        	//SD�ւ̏�����
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

			//�{�^�����̋�؂�
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
        	//�{�^��1
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
            //�{�^��2
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
        //Google Chart API�p��URL�쐬
        String[] button1_array = countstr1.split(",");
        //��ԑ傫�����l������<-�O���t�̍ő�l�ɗ��p
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

            //��ԑ傫�����l������
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

            //�{�^��1��2�̂����A�J�E���g���������ق����Z�o
            //���Ȃ��ق���0�Ŗ��߂�
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

        //�J�E���g����Google Chart API�̊g���`��(chd=e)�ɒl��ύX
        String data1 = encodeChartData(button1_array, max1);
        String datastr = "chd=e:" + data1;
        if(button.equals("2")){
            String data2 = encodeChartData(button2_array, max1);
            datastr += "," + data2;
        }

        //x���̃��x�����쐬(���ԕ\��)
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
            + "&chxr=1,0," + max1   //y���̃��x�����쐬(�O���t�̍ő�l���J�E���g���̍ő�l�Ƃ���)
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
        
        //intent�쐬
        Intent i = new Intent(this, Chart.class);
        //�I�����ڂ�DB��ID��n��
        i.putExtra("url", url);
        startActivity(i);
    }
    
    private void remove(){
    	//�m�F�_�C�A���O�̕\��
    	new AlertDialog.Builder(this)
    	.setTitle(R.string.dm_delete)
    	.setMessage("\"" + title + "\" " + getString(R.string.dm_delete_confirm))
    	.setPositiveButton(R.string.cnt_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//�ۑ��f�[�^�폜����
				removeData();

				//Activity���I�����A�O��ʂɖ߂�
				finish();
			}
		})
		.setNegativeButton(R.string.cnt_ng, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//�������Ȃ�
			}
		})
		.show();
    }
    
    private void removeData(){
    	//DB�擾
    	DatabaseHelper helper = new DatabaseHelper(this);
    	SQLiteDatabase db = helper.getWritableDatabase();
    	//�f�[�^���폜
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
        //1�����̏ꍇ��0������
        if(month.length() == 1){
        	month = "0" + month;
        }

        String day = Integer.toString(d);
        //1�����̏ꍇ��0������
        if(day.length() == 1){
        	day = "0" + day;
        }

        String hour = Integer.toString(h);
        //1�����̏ꍇ��0������
        if(hour.length() == 1){
        	hour = "0" + hour;
        }
        String minute = Integer.toString(min); 
        //1�����̏ꍇ��0������
        if(minute.length() == 1){
        	minute = "0" + minute;
        }
        
        String second = Integer.toString(sec);
        //1���b�̏ꍇ��0������
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
