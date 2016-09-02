package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import model.City;
import model.County;
import model.Province;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import db.CoolWeatherDB;

public class Utility {
	

	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, List<String> idlist, List<String> namelist){
		if(idlist != null && namelist != null){
			Iterator<String> iterator_id = idlist.iterator();
			Iterator<String> iterator_name = namelist.iterator();
			while(iterator_id.hasNext() && iterator_name.hasNext()){
				Province province = new Province();
				province.setProvinceCode(iterator_id.next());
				province.setProvinceName(iterator_name.next());
				coolWeatherDB.saveProvince(province);
			}
			return true;
		}
		return false;
	}
	


	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, List<String> idlist, List<String> namelist, int provinceId){
		if(idlist != null && namelist != null){
			Iterator<String> iterator_id = idlist.iterator();
			Iterator<String> iterator_name = namelist.iterator();
			while(iterator_id.hasNext() && iterator_name.hasNext()){
				City city = new City();
				city.setCityCode(iterator_id.next());
				city.setCityName(iterator_name.next());
				city.setProvinceId(provinceId);
				coolWeatherDB.saveCity(city);
			}
			return true;
		}

		return false;
	}
	

/*	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String c : allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);

					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}*/
	

	public static void handleweatherResponse(Context context,String response){
		try{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}


	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	
}
