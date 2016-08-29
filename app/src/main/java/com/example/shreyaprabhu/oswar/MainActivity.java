package com.example.shreyaprabhu.oswar;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private android.support.v7.widget.Toolbar toolbar;
    private FloatingActionButton fab;
    //speech
    private String help1 ="help";
    private String help2 ="Help";
    private String yelp1 ="yelp";
    private String yelp2 ="Yelp";
    private String helpm1 ="help me";
    private String helpm2 ="Help me";
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    //contacts
    static final int PICK_CONTACT = 1;
    private Button contact_button;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<ContactModel> contactList;
    Cursor cursor;
    ListView listview;
    SimpleCursorAdapter cursorAdapter;
    EventsDbHandler eventsDbHelper;

    //Accelerometer
    //Accelerometer
    private SensorManager senSensorManager;
    private Sensor senaccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 2000;
    Button b1;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        contactList = new ArrayList<>();
        adapter = new ContactsAdapter(MainActivity.this, contactList);
        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        listview = (ListView) findViewById(R.id.list_view);
        /*recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
        recyclerView.setAdapter(adapter);*/
        eventsDbHelper = new EventsDbHandler(this);

        cursor = eventsDbHelper.getAllEvents();
        String[] columns = new String[]{
                EventsDbHandler.EVENT_COLUMN_NAME,
                EventsDbHandler.EVENT_COLUMN_PHONE

        };


        int[] widgets = new int[]{
                R.id.contact_name,
                R.id.contact_phone
        };

        cursorAdapter = new SimpleCursorAdapter(this, R.layout.contact_item,
                cursor, columns, widgets, 0);
        listview.setAdapter(cursorAdapter);


        contact_button = (Button)findViewById(R.id.contact_button);
        contact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }
                else{*/
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
                // }

            }
        });
        b1 = (Button) findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent activityChangeIntent = new Intent(MainActivity.this, MapsActivity.class);

                // currentContext.startActivity(activityChangeIntent);

                MainActivity.this.startActivity(activityChangeIntent);
            }
        });


        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtSpeechInput.setText("");
                promptSpeechInput();
            }
        });


        toolbar = (Toolbar)findViewById(R.id.app_bar);
        (MainActivity.this).setSupportActionBar(toolbar);

        context = this;
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senaccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senaccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "FAB CLICKED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    ContentResolver contentResolver = getContentResolver();
                    Cursor c = contentResolver.query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String cNumber = new String();

                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {

                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));

                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        Log.d("number", cNumber);
                        Log.d("name", name);
                        ContactModel contactModel  = new ContactModel();
                        contactModel.setName(name);
                        contactModel.setPhone(cNumber);
                        contactList.add(contactModel);
                        if (eventsDbHelper.insertEvent(contactModel.getName(),contactModel.getPhone())) {
                            Toast.makeText(getApplicationContext(), "Event Added", Toast.LENGTH_SHORT).show();
                            cursorAdapter.notifyDataSetChanged();
                            cursor.requery();

                        } else {
                            Toast.makeText(getApplicationContext(), "Could not Add Event", Toast.LENGTH_SHORT).show();
                        }
                        //adapter.notifyDataSetChanged();

                    }
                }
                break;

            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.get(0).equals(help1)||result.get(0).equals(help2)||result.get(0).equals(yelp1)||result.get(0).equals(yelp2)||result.get(0).equals(helpm1)||result.get(0).equals(helpm2)) {
                        txtSpeechInput.setText(result.get(0));

                        String phoneNo = "9663985049";
                        String sms = "Help";

                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                            Toast.makeText(getApplicationContext(), "SMS Sent!",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "SMS faild, please try again later!",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }

        }
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType()== Sensor.TYPE_ACCELEROMETER){
            float x=sensorEvent.values[0];
            float y=sensorEvent.values[1];
            float z=sensorEvent.values[2];
            long curTime=System.currentTimeMillis();
            if ((curTime-lastUpdate)>100){
                long diffTime=(curTime-lastUpdate);
                lastUpdate=curTime;
                float speed=Math.abs(x+y+z-last_x-last_y-last_z)/diffTime*10000;
                if (speed > SHAKE_THRESHOLD){
                    txtSpeechInput.setText("");
                    promptSpeechInput();
                    Toast.makeText(context,"Shake Detected",Toast.LENGTH_SHORT).show();

                }
                last_x=x;
                last_y=y;
                last_z=z;
            }

        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senaccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
