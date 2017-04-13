package com.bedubs.rxfiller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
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
    private ProgressDialog spinner;
    private static final String EMR_URL = "http://www2.southeastern.edu/Academics/Faculty/jburris/emr.xml";
    private static final String REFILL_URL = "http://www2.southeastern.edu/Academics/Faculty/jburris/rx_fill.php?";
    public static List<PatientInfo> pInfo;
    public static String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        userId = i.getStringExtra("user");
        new DownloadXmlTask().execute(EMR_URL);

        setContentView(R.layout.activity_patient);

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

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PatientInfo patientInfo = pInfo.get(sectionNumber);
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putParcelable("patientData", patientInfo);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_patient, container, false);

            TextView nameView = (TextView) rootView.findViewById(R.id.section_name);
            nameView.setTextSize(24);
            TextView pidView = (TextView) rootView.findViewById(R.id.section_pid);
            pidView.setTextSize(24);

            final PatientInfo patient = getArguments().getParcelable("patientData");
            final List<PatientOrders> orders = patient.getOrders();

            nameView.setText(getString(R.string.name_format, patient.getName()));
            pidView.setText(getString(R.string.id_format, patient.getId()));

            LinearLayout rxLayout = (LinearLayout) rootView.findViewById(R.id.rx_layout);
            TextView rxHeader = (TextView) rootView.findViewById(R.id.rx_header);
            rxHeader.setText(getString(R.string.rx_header, + patient.getOrders().size()));

            for (int i=0; i < orders.size(); i++) {
                final String meds = orders.get(i).getMedicine();
                TextView bodyText = new TextView(getContext());
                bodyText.setTextSize(24);
                bodyText.append("\n\tRX: " + orders.get(i).getMedicine());
                bodyText.append("\n\tDosage: " + orders.get(i).getDosage());
                bodyText.append("\n\tRemaining: " + orders.get(i).getRefillsRemaining());
                final Button fillBtn = new Button(getActivity());
                fillBtn.setText(getString(R.string.fill));
                fillBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        boolean refillSuccess = false;
                        String urlString = REFILL_URL + "login=" + userId
                                + "&id=" + patient.getId()
                                + "&rx=" + meds;
                        new RefillTask().execute(urlString);
//                        try {
////                            refillSuccess = refillRX(patient.getId(), meds);
//                        } catch (IOException e) {
//                            refillSuccess = false;
//                        }

                        if (refillSuccess) {
                            Toast.makeText(getContext(), "Rx Refilled", Toast.LENGTH_LONG).show();
                        } else {
                            fillBtn.setBackgroundColor(Color.RED);
                        }
                        Snackbar.make(view, "Refill Success: " + refillSuccess, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                rxLayout.addView(bodyText);
                rxLayout.addView(fillBtn);
            }

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

    }

    private static InputStream downloadUrl(String urlString) throws IOException {
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

    private static class RefillTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            InputStream is = null;
            String response = null;
            try {
                is = downloadUrl(params[0]);
                response = parse(is);
            } catch (IOException e) {
                Log.println(Log.ERROR, "Refill", e.getMessage());
            } catch (XmlPullParserException x) {
                Log.println(Log.ERROR, "xmlparser", x.getMessage());
            }

            return response;
        }

        protected void onPostExecute(){

        }

        private static final String ns = null;

        String parse(InputStream in) throws XmlPullParserException, IOException {
            String xmlValue = " ";
            try {
                XmlPullParser parser = Xml.newPullParser();
//                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//                parser.setInput(in, null);
//                parser.nextTag();
                xmlValue = parser.getText();
                return xmlValue;
            } finally {
                in.close();
            }
        }
    }

}
