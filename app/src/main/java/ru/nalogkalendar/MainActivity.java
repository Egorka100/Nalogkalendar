package ru.nalogkalendar;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextMessage;
    private ListView eventsLv;
    private Handler mHandler;
    private static final int ACTIVITY_NUM = 0;
    private Context mContext = MainActivity.this;
    SimpleAdapter adapter;

    DBHelper dbHelper;
    String urlStr = "https://www.nalog.ru/opendata/7707329152-kalendar/data-01012017-structure-02282014.xml";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        setupBottomNavigationView();

        adapter = new SimpleAdapter(this, listEvent, android.R.layout.simple_list_item_2,
                new String[]{"text1", "text2"},
                new int[]{android.R.id.text1, android.R.id.text2});

        dbHelper = new DBHelper(this);
        mHandler = new Handler(Looper.myLooper());
        eventsLv = findViewById(R.id.message);
        try {
            parse();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mHandler.post(FillList);

    }


    @Override
    public void onClick(View view) {

    }

    String numStr,
            typeStr,
            cdataStr,
            yearStr,
            monthStr;
    String b = "";

    @RequiresPermission(Manifest.permission.INTERNET)
    public void parse() throws XmlPullParserException, IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor c = db.query("calendarhtml", null, null, null, null, null, null);
                    int countRows = c.getCount();
                    if (countRows == 0) {
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xpp = factory.newPullParser();
                        URL urlXML = new URL(urlStr);
                        InputStream stream = urlXML.openStream();
                        xpp.setInput(stream, null);

                        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                            switch (xpp.getEventType()) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    if (xpp.getName().equals("year")) {
                                        yearStr = xpp.getAttributeValue(0);
                                    }
                                    if (xpp.getName().equals("month")) {
                                        monthStr = xpp.getAttributeValue(0);
                                    }
                                    if (xpp.getName().equals("day")) {
                                        numStr = xpp.getAttributeValue(0);
                                        if (xpp.getAttributeValue(1).equals("event")) {
                                            xpp.nextToken();
                                            cdataStr = xpp.getText();
                                            cv.put("date", monthStr + " " + numStr + ", " + yearStr);
                                            cv.put("cdata", cdataStr);
                                            db.insert("calendarhtml", null, cv);
                                        }
                                    }
                                    break;
                                case XmlPullParser.END_TAG:
                                    break;
                                case XmlPullParser.END_DOCUMENT:
                                    break;
                            }
                            xpp.next();
                        }
                    }
                    /*ContentValues cv = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor c = db.query("Calendar", null, null, null, null, null, null);
                    int countRows=c.getCount();
                    if(countRows==0) {
                        //Сосём хмл
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xpp = factory.newPullParser();
                        URL urlXML = new URL(urlStr);
                        InputStream stream = urlXML.openStream();
                        xpp.setInput(stream, null);

                        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                            switch (xpp.getEventType()) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    if (xpp.getName().equals("year")) {
                                        yearStr = xpp.getAttributeValue(0);
                                    }
                                    if (xpp.getName().equals("month")) {
                                        monthStr = xpp.getAttributeValue(0);
                                    }
                                    if (xpp.getName().equals("day")) {
                                        numStr = xpp.getAttributeValue(0);
                                        //typeStr = xpp.getAttributeValue(1);
                                        if (xpp.getAttributeValue(1).equals("event")) {
                                            xpp.nextToken();
                                            cdataStr = xpp.getText();
                                            cdataStr=cdataStr.replaceAll("(<p.*?>|<strong>|</p>|</strong>|<a.*?>|</a>|\\r)",""); //Удаляем html
                                            String[] splitedcdataStr = cdataStr.split("(<br>)");
                                            for(int i=0;i<splitedcdataStr.length;i++){
                                                String[] splittwicecdataStr=splitedcdataStr[i].split("(:\\s-&nbsp;|:-&nbsp;|:\\s-)");
                                               // splittwicecdataStr[1].replaceAll("(&nbsp;)","");
                                                    cv.put("year", yearStr);
                                                    cv.put("month", monthStr);
                                                    cv.put("day", numStr);
                                                    cv.put("nametaxe", splittwicecdataStr[0]);
                                                   // cv.put("whopay", splittwicecdataStr[1]);
                                                    db.insert("Calendar", null, cv);

                                            }
                                           // splitedcdataStr=null;

                                        }
                                    }

                                    break;
                                case XmlPullParser.END_TAG:
                                    break;
                                case XmlPullParser.END_DOCUMENT:
                                    break;
                            }
                            xpp.next();
                        }
                    }
                */
                    c.close();
                    mHandler.post(FillList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }

    private ArrayList<HashMap<String, String>> listEvent = new ArrayList<>();
    HashMap<String, String> map;

    Runnable FillList = new Runnable() {
        @Override
        public void run() {
            //public void fillListView(){
            listEvent.clear();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor c = db.query("calendarhtml", null, null, null, null, null, null);
            if (c.moveToFirst()) {
                int dataCollIndex = c.getColumnIndex("date");
                    /*int yearCollIndex=c.getColumnIndex("year");
                    int monthCollIndex=c.getColumnIndex("month");
                    int dayCollIndex=c.getColumnIndex("day");
                    int nametaxeCollIndex=c.getColumnIndex("nametaxe");*/
                //int whopayCollIndex=c.getColumnIndex("whopay");
                //String yearevent,monthevent,dayevent,nametaxeevent,whopayevent;
                String data;
                //int i=0;
                do {
                    data = c.getString(dataCollIndex);

                       /* yearevent=c.getString(yearCollIndex);
                        monthevent=c.getString(monthCollIndex);
                        dayevent=c.getString(dayCollIndex);
                        nametaxeevent=c.getString(nametaxeCollIndex);*/
                    //whopayevent=c.getString(whopayCollIndex);
                    map = new HashMap<>();
                    //map.put("text1", nametaxeevent);
                    //map.put("text2", dayevent+"."+monthevent+"."+yearevent);
                    map.put("text1", data);
                    map.put("text2", data);
                    listEvent.add(map);
                        /*listEvent.add(i,dayevent+"."+monthevent+"."+yearevent+"\n"+nametaxeevent
                                //+"\r*"+whopayevent
                        );
                        i++;*/
                }
                while (c.moveToNext());

                eventsLv.setAdapter(adapter);
            }
            c.close();
        }
        //}
    };


    /**
     * Устанавливаем нижнее меню
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNafigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
