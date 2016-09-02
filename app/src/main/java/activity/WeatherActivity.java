package activity;

import java.security.PublicKey;
import java.util.List;

import db.CoolWeatherDB;
import service.AutoUpdateService;
import util.CodeCallbackListener;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

import com.example.coolweather.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	
	private LinearLayout weatherInfoLayout;
	

	private TextView cityNameText;

	private TextView publishText;

	private TextView weatherDespText;

	private TextView temp1Text;

	private TextView temp2Text;

	private TextView currentDateText;

	private Button switchCity;

	private Button refreshWeather;

	private CoolWeatherDB coolWeatherDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		String cityCode = getIntent().getStringExtra("city_code");
		if(!TextUtils.isEmpty(cityCode)){
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherInfo(cityCode);
		} else {
			showWeather();
		}
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
			default:
				break;
		}
		
	}
	

/*	private void queryWeatherCode(String cityCode){
		queryFromServer(cityCode,"cityCode");
	}*/
	

	private void queryWeatherInfo(String cityCode){
		String address = "http://www.weather.com.cn/data/cityinfo/" + cityCode + ".html";
		queryFromServer(address,"weatherCode");
	}
	

	private void queryFromServer(final String address,final String type){
		if("weatherCode".equals(type)) {
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				@Override
				public void onFinish(final String response) {
						Utility.handleweatherResponse(WeatherActivity.this, response);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showWeather();

							}
						});
				}

				@Override
				public void onError(Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							publishText.setText("同步失败");

						}
					});

				}
			});
		}

	}
	

	
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText(prefs.getString("今天" + "publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}

}
