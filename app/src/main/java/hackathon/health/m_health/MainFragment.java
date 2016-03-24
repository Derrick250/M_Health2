package hackathon.health.m_health;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView tvPressure;
    private TextView tvTemperature;
    private TextView tvHumidity;
    Toolbar mToolbar;
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        tvPressure = (TextView)view.findViewById(R.id.tvPressure);
        tvTemperature = (TextView)view.findViewById(R.id.tvTemperature);
        tvHumidity = (TextView)view.findViewById(R.id.tvHumidity);
        new LongOperation().execute();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }














    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=Nairobi,KE&APPID=%s";

    public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
    private static final String APIKEY = "9650efc4b0d43c9911bd1dc861c88958";

    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            System.out.println(">>>>>>>>>>>>>Backgound");
            JSONObject a = new JSONObject();
            String url = String.format(OPEN_WEATHER_MAP_API,APIKEY);
            try{
                a.put("x-api-key",APIKEY);
                String json = a.toString();
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(">>>>>>>>>>>>>After");
            System.out.println(result);
            double temp;
            double max_temp;
            double min_temp;
            double wind;
            double value;
            double humidity;
            double pressure;
            try{
                JSONObject data = new JSONObject(result);
                JSONObject main = data.getJSONObject("main");
                temp = main.getDouble("temp")- 273.15;
                max_temp = main.getDouble("temp_max")- 273.15;
                min_temp = main.getDouble("temp_min") - 273.15;
                wind = data.getJSONObject("wind").getDouble("speed");
                humidity = main.getDouble("humidity");
                pressure = main.getDouble("pressure");

                value = (- 15.2 + (0.0071 * max_temp) + (0.0179 * min_temp) - (0.0067 * temp) + (0.676 *pressure)  - (0.00424 * humidity) + (0.0135 * wind))/10;
                tvPressure.setText("" + pressure);
                tvTemperature.setText(String.format("%02f",temp));
                tvHumidity.setText("" + humidity + "%");
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>The value is " + value);
                if(humidity > 20){
                    notice(humidity);
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
        public void notice(double perc){
            Notification n  = new Notification.Builder(context)
                    .setContentTitle("Warning")
                    .setContentText("Humidity level is at " + perc + "%")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        }
    }

}
