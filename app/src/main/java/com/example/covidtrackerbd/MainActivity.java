package com.example.covidtrackerbd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.WindowManager;

import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.covidtrackerbd.databinding.ActivityMainBinding;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String url = "https://corona.lmao.ninja/v2/";
    ActivityMainBinding am;
    PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        am = ActivityMainBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(am.getRoot());

        if (!CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {
            checkInternetEntering();
        }


        LottieAnimationView lottieAnimationView = findViewById(R.id.splash);
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);
        lottieAnimationView.setSpeed((float) 1.0);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        myApi api = retrofit.create(myApi.class);

        Call<List<Countries>> coutries = api.getContries();

        coutries.enqueue(new Callback<List<Countries>>() {
            @Override
            public void onResponse(Call<List<Countries>> call, Response<List<Countries>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Wasn't a succesfull fectching", Toast.LENGTH_SHORT).show();
                }

                List<Countries> data = response.body(); // you will the get whole data. in a list

                for (int i = 0; i < data.size(); i++) {
                    String d = data.get(i).getCountry();
                    if (d.equals("Bangladesh")) {
                        int id = i;
                        int cases = data.get(id).getCases();
                        int todayCases = data.get(id).getTodayCases();

                        int recoverd = data.get(id).getRecovered();
                        int todayRecoverd = data.get(id).getTodayRecovered();

                        int active = data.get(id).getActive();

                        int death = data.get(id).getDeaths();
                        int todayDeath = data.get(id).getTodayDeaths();


                        int tests = data.get(id).getTests();


                        am.caseConfirmed.setText(getFormatedAmount(cases) + "");
                        am.caseConfirmedToday.setText("(+" + todayCases + ")");

                        am.caseRecovered.setText(getFormatedAmount(recoverd) + "");
                        am.caseRecoveredToday.setText("(+" + todayRecoverd + ")");

                        am.caseActive.setText(getFormatedAmount(active) + "");

                        am.caseDeath.setText(getFormatedAmount(death) + "");
                        am.caseDeathToday.setText("(+" + todayDeath + ")");

                        am.caseTest.setText(getFormatedAmount(tests) + "");

                        Date date = new Date((long) data.get(id).getUpdated());
                        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                        am.amTxtTime.setText(" Last Updated at : " + dateFormat.format(date));


                        mPieChart = (PieChart) findViewById(R.id.piechart);
                        mPieChart.addPieSlice(new PieModel("Confirm", cases, Color.parseColor("#FFB701")));
                        mPieChart.addPieSlice(new PieModel("Recoverd", recoverd, Color.parseColor("#FF03DAC5")));
                        mPieChart.addPieSlice(new PieModel("Active", active * 20, Color.parseColor("#FFBB86FC")));
                        mPieChart.addPieSlice(new PieModel("Death", death * 10, Color.parseColor("#f55c47")));

                        mPieChart.startAnimation();
                        //txt.setText(cases+"");
                    }
                    //  txt.append(
                    //" User Id : "+data.get(i).getCases()+"\n"
                                    /*"ID: "+myUsers.get(i).getId()+"\n"+
                                    "Title : "+myUsers.get(i).getTitle()+"\n"+
                                    "TEXT : "+myUsers.get(i).getText()+"\n\n"*///);
                }
            }

            @Override
            public void onFailure(Call<List<Countries>> call, Throwable t) {
               Toast.makeText(getApplicationContext(),  "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFormatedAmount(int amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }


    void checkInternetEntering()
    {
        //if there is no internet do this
        setContentView(R.layout.activity_main);

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.coronavirus)//alert the person knowing they are about to close
                .setTitle("No internet connection available")
                .setMessage("Please Check you're Mobile data or Wifi network.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

}

class CheckNetwork {

    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isInternetAvailable(Context context)
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null)
        {
            Log.d(TAG,"no internet connection");
            return false;
        }
        else
        {
            if(info.isConnected())
            {
                Log.d(TAG," internet connection available...");
                return true;
            }
            else
            {
                Log.d(TAG," internet connection");
                return true;
            }

        }
    }
}
