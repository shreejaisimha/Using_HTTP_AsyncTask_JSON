/*
 * Copyright (C) 2017 Shree Jaisimha
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

package com.jaisimhas.usgs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&endtime=2016-05-02&minfelt=50&minmagnitude=5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network_connection);

        //Check if network connectivity is available
        ////ConnectivityManager cm =
                //(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
       //To do: Confirm on access
       // NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //if (netInfo != null && netInfo.isConnectedOrConnecting()) {
        //if(cm!=null)
        if(true)
        {
            //if the connectivity is available, then launch Async Task
            // Create an {@link AsyncTask} to perform the HTTP request to the given URL
            // on a background thread. When the result is received on the main UI thread,
            // then update the UI.
            //note best that the async task is defined here as an inner class.
            setContentView(R.layout.activity_main);
            EarthquakeAsyncTask task = new EarthquakeAsyncTask();
            task.execute(USGS_REQUEST_URL);
        }
        else
        {
           // Show No internet connection
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

        }


    }

    /**
     * Update the UI with the given earthquake information.
     */
    private void updateUi(Event earthquake) {


        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(earthquake.title);

        TextView tsunamiTextView = (TextView) findViewById(R.id.number_of_people);
        tsunamiTextView.setText(getString(R.string.num_people_felt_it, earthquake.numOfPeople));

        TextView magnitudeTextView = (TextView) findViewById(R.id.perceived_magnitude);
        magnitudeTextView.setText(earthquake.perceivedStrength);
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, Event> {

        /**
         * This method is invoked (or called) on a background thread, so we can perform
         * long-running operations like making a network request.
         *
         * It is NOT okay to update the UI from a background thread, so we just return an
         * {@link Event} object as the result.
         */
        protected Event doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            Event result = Utils.fetchEarthquakeData(urls[0]);
            return result;
        }

        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         *
         * It IS okay to modify the UI within this method. We take the {@link Event} object
         * (which was returned from the doInBackground() method) and update the views on the screen.
         */
        protected void onPostExecute(Event result) {
            // If there is no result, do nothing.
            if (result == null) {
                return;
            }

            // Hide loading indicator because the data has been loaded
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            updateUi(result);
        }
    }
}

//since the async class is an inner class all variables from main activity task as visible.
//if async task is kept as an outer class then we need to ensure we can pass data through messaing.