package com.circlesquare.delccustomer;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.circlesquare.data.SPTechnician;
import com.circlesquare.data.ServiceCenter;
import com.circlesquare.data.ServicePartner;
import com.circlesquare.util.DelCCustomer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TechnicianDetails extends AppCompatActivity implements View.OnClickListener {
    String technician_Name, serviceCenter_Name;
    long technicianId;
    long spId;
    long serviceCenterId;
    long servicePartnerId;
    TextView technicianName, serviceCenterName, rating, servicedRequests, experience, technicianMobile, spName, spContactNo;
    Bundle bundleanimation;
    SPTechnician technicianAssigned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbar));
        }
        bundleanimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation, R.anim.animation2).toBundle();
        Button button = (Button) findViewById(R.id.buttonOK);
        button.setOnClickListener(this);
        technicianName = (TextView) findViewById(R.id.technicianName);
        serviceCenterName = (TextView) findViewById(R.id.serviceCenterName);
        rating = (TextView) findViewById(R.id.technicianRating);
        servicedRequests = (TextView) findViewById(R.id.requestServiced);
        technicianMobile = (TextView) findViewById(R.id.technicianMobile);
        experience = (TextView) findViewById(R.id.experiance);
        spName = (TextView) findViewById(R.id.spName);
        spContactNo = (TextView) findViewById(R.id.spContactNo);
        //I am retrieving the string like this from an intent, convert java object to JSON and assign to String.
        String jsonString = getIntent().getStringExtra(DelCCustomer.TECHNICIAN);
        Log.e("technician", "" + jsonString);
        //Convert to JSON to java object,read it from a file
        technicianAssigned = new Gson().fromJson(jsonString, SPTechnician.class);
        technicianId = technicianAssigned.getSPTechnicianid();
        getTechnicianRating(technicianId);
        getServicePartnerDetails(technicianAssigned.getServicePartnerId());
        TextView technicianExperienceInfo = (TextView) findViewById(R.id.experiance);
        String string = "He has more than " + technicianAssigned.getExperience() + " years experience in servicing electronic appliance like "+technicianAssigned.getSkills()+".He has done " + technicianAssigned.getEducation() + ".";
        technicianExperienceInfo.setText(string);
        technician_Name = technicianAssigned.getSPTechnicianName();
        serviceCenterId = technicianAssigned.getServicePartnerId();
        //spName=technicianAssigned.getSpName();
        //spContactNo=technicianAssigned.getSpContactNo();
        technicianName.setText(technician_Name);
        if (technicianAssigned.getMobileNo() != null)
            technicianMobile.setText(technicianAssigned.getMobileNo());
        if (serviceCenterId != 0) {
            new ServiceCenterInfo().execute(serviceCenterId);
        } else {
            serviceCenterName.setVisibility(View.GONE);
        }
        ImageView technicianPhoto = (ImageView) findViewById(R.id.technicianPhoto);
        String invoiceURLorPath;

        ProgressDialog progressDialog = new ProgressDialog(TechnicianDetails.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (technicianAssigned.getPhoto() != null) {
            invoiceURLorPath = getTechnicianPhotoURL(technicianAssigned.getPhoto());
            DelCCustomer.setImageViewUsingURL(this, invoiceURLorPath, technicianPhoto);
        }

        if (progressDialog.isShowing())
            progressDialog.dismiss();
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    public String getTechnicianPhotoURL(@NonNull String photoPath) {
        String url = DelCCustomer.serverURL;
        int index = photoPath.indexOf("/");
        int i = 0;
        String subURL = "";
        while (index >= 0) {
            i++;
            if (i == 5) {    //to know file name
                subURL = photoPath.substring(index + 1);
                break;
            } else
                index = photoPath.indexOf("/", index + 1);
        }
        url = url.concat(subURL);
        return url;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonOK)
            onBackPressed();
          // startActivity(new Intent(this, UserHomeScreen.class),bundleanimation);
    }

    ProgressDialog progressDialog;

    private class ServiceCenterInfo extends AsyncTask<Long, Void, Integer> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TechnicianDetails.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Long... params) {
            publishProgress();
            long scId = params[0];
            int response = getUserDetails(scId);
            publishProgress();
            return response;
        }

        @Override
        protected void onPostExecute(Integer params) {
            int response = params;
            if (progressDialog.isShowing())
                progressDialog.dismiss();
//            Log.e("response",""+response);
            if (response == 0 || response == 200 || response == 204) {
                serviceCenterName.setText(serviceCenter.getName());
            }
        }
    }

    public int getUserDetails(long Id) {
        int response = 600;
        try {
            URL url;
            HttpURLConnection urlConnection = null;
//            http://52.38.105.226:8080/support1/rest/Product/product/90
//            {"userid":40,"userName":"Manju Rudra","mailId":"","mobileNo":"9492633797","address":"","city":"Bangalore","pincode":560102,"userType":0}
            url = new URL(DelCCustomer.IP + "/ServiceCenter/byId/" + Id);
            // url=new URL(DelCCustomer.IP+"ratingof techinician/{sPTechnicianId}");
// Log.e("url in adapter", DelCCustomer.IP + "/ServiceCenter/byId/"+Id);
            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(25000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            response = urlConnection.getResponseCode();
            InputStream inputStream = urlConnection.getInputStream();
//            Log.i("onHandleIntent", " execute : " + response);
//            Log.i("onHandleIntent", " execute : " + inputStream);
            if (inputStream != null && (response == 200 || response == 0)) {
//                Log.i("onHandleIntent", " execute :inputStream not null 00 ");
                readJsonStream(inputStream);
//                Log.e("Bundle Data Rudra", "read Json completed");
            }
        } catch (IOException ioe) {
//            Log.i("IOException", " IOException : " + ioe.getMessage());
            ioe.printStackTrace();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }
        return response;
    }

    public ServiceCenter readJsonStream(InputStream in)
            throws IOException {
        Gson gson = new Gson();
//        Log.e("json","stream called");
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

//        usersList = new ArrayList<ServiceCenter>();
        JsonParser parser = new JsonParser();

        JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
//        Log.e("json object",""+jsonObject.toString());
        ServiceCenter serviceItemDetails = gson.fromJson(jsonObject, ServiceCenter.class);
        reader.close();
        serviceCenter = serviceItemDetails;
//        Log.d("readJsonStream", " kkk : " + usersList.size());
        return serviceItemDetails;
    }

    ServiceCenter serviceCenter;

    public void displayTechnicianRating(String responseString) {
        String rating = " ";
        String requestsServed = " ";
        if (responseString.contains(",")) {
            if (responseString.contains("[")) {
                int commaIndex = responseString.indexOf(",");
                int openArrayIndex = responseString.indexOf("[");
                int closedArrayIndex = responseString.indexOf("]");
                if (commaIndex != -1 && openArrayIndex != -1) {
                    requestsServed = responseString.substring(openArrayIndex + 1, commaIndex);
                    int dotIndex = requestsServed.indexOf(".");
                    if (dotIndex != -1) {
                        requestsServed = requestsServed.substring(0, dotIndex);
                    }

                }
                if (commaIndex != -1 && closedArrayIndex != -1) {
                    rating = responseString.substring(commaIndex + 1, closedArrayIndex);
                }
                TextView ratingTextView = (TextView) findViewById(R.id.technicianRating);
                ratingTextView.setText(rating);
                TextView requestServedTextView = (TextView) findViewById(R.id.requestServiced);
                requestServedTextView.setText(requestsServed);
            }
        } else {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void getTechnicianRating(final long technicianId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = DelCCustomer.IP + "/spsr/ratingoftechnician/" + technicianId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                displayTechnicianRating(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TechnicianDetails.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);

    }

    public void displaySPDetails(String responseString) {
        String spName = "";
        String spContactNo = "";
        String jsonString = getIntent().getStringExtra(DelCCustomer.TECHNICIAN);
        Log.e("technician", "" + jsonString);
                TextView spNameTextView = (TextView) findViewById(R.id.spName);
        String name =technicianAssigned.getSpName();
                spNameTextView.setText(spName);
                TextView spContactNoTextView = (TextView) findViewById(R.id.spContactNo);
        int string=technicianAssigned.getSpContactNo();
                spContactNoTextView.setText(spContactNo);
    }
    public void getspName(final long spId){
        RequestQueue queue=Volley.newRequestQueue(this);
        String url=DelCCustomer.IP+"/sp/byId/"+spId;
        StringRequest stringRequest=new StringRequest(Request.Method.GET,url,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                displaySPDetails(response);
            }
            },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TechnicianDetails.this, "something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
            }
    public void getServicePartnerDetails(int servicePartnerId){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = DelCCustomer.IP+"/sp/byId/"+servicePartnerId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ServicePartner sp = new Gson().fromJson(response.toString(), ServicePartner.class);
                displayServicePartnerDetails(sp);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TechnicianDetails.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }
    public void displayServicePartnerDetails(ServicePartner sp){
        TextView spNameTextView = (TextView) findViewById(R.id.spName);
        TextView spContactNo = (TextView) findViewById(R.id.spContactNo);
        spNameTextView.setText(sp.getName());
        spContactNo.setText(sp.getMobile());
    }
        }







