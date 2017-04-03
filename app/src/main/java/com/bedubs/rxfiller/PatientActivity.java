package com.bedubs.rxfiller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PatientActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private ProgressDialog spinner;
    private static final String URL = "http://www2.southeastern.edu/Academics/Faculty/jburris/emr.xml";
    public static List<PatientInfo> pInfo;

//    // Whether there is a Wi-Fi connection.
//    private static boolean wifiConnected = false;
//    // Whether there is a mobile connection.
//    private static boolean mobileConnected = false;
//    // Whether the display should be refreshed.
//    public static boolean refreshDisplay = true;
//    public static String sPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DownloadXmlTask().execute(URL);
//        loadPage();
        setContentView(R.layout.activity_patient);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_PATIENT_ID = "patient_id";
        private static final String ARG_PATIENT_NAME = "patient_name";
        private static final String ARG_PATIENT_ORDERS = "patient_orders";
        private static final String ARG_MED_COUNT = "med_count";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PatientInfo patientInfo = pInfo.get(sectionNumber);
            List<PatientOrders> patientOrders = patientInfo.getOrders();
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_PATIENT_NAME, patientInfo.getName());
            args.putString(ARG_PATIENT_ID, patientInfo.getId());
            args.putInt(ARG_MED_COUNT, patientOrders.size());
//            args.putParcelableArrayList("hey", patientOrders);
            Log.println(Log.INFO, "TAG", "Patient med count: " + patientOrders.get(0).getMedicine());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_patient, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            TextView titleView = (TextView) rootView.findViewById(R.id.section_title);
            TextView bodyView = (TextView) rootView.findViewById(R.id.body);
            titleView.setText(getString(R.string.name_format, getArguments().getString(ARG_PATIENT_NAME)));
            textView.setText(getString(R.string.id_format, getArguments().getString(ARG_PATIENT_ID)));
            bodyView.setText("This is the body\n Med count is: ");
            bodyView.append(getArguments().getInt(ARG_MED_COUNT) + " ");
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return pInfo.size();
        }
    }

    // Uses AsyncTask to download the XML.
//    public void loadPage() {
//
//        if((sPref.equals(ANY)) && (wifiConnected || mobileConnected)) {
//            new DownloadXmlTask().execute(URL);
//        }
//        else if ((sPref.equals(WIFI)) && (wifiConnected)) {
//            new DownloadXmlTask().execute(URL);
//        } else {
//            // show error
//        }
//        new DownloadXmlTask().execute(URL);
//    }

    // Implementation of AsyncTask used to download XML.
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner = new ProgressDialog(PatientActivity.this);
            spinner.setMessage("Loooading...");
            spinner.setIndeterminate(false);
            spinner.setCancelable(false);
            spinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            spinner.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            spinner.dismiss();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        // Uploads XML
        private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
            InputStream stream = null;
            // Instantiate the parser
            EmrXmlParser emrXmlParser = new EmrXmlParser();
            List<PatientInfo> patients = null;

            try {
                stream = downloadUrl(urlString);
                patients = emrXmlParser.parse(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            pInfo = patients;
            Log.println(Log.INFO, "TAG", "Patient count: " + patients.size());
            return patients.toString();
        }

        // Given a string representation of a URL, sets up a connection and gets
// an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }

    }

}
