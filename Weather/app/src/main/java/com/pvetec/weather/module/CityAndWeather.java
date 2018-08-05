package com.pvetec.weather.module;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class CityAndWeather {

	public String mCityName;
	public String mTime;
	public String mWeatherIcon;
	public String mWindDirection;
	public String mWindSpeed;
	public String mWind;
	public String mTemperatureRange;
	public String mCurrentTemperature;
	public ArrayList<Forecast> mForecastList;
	public String mUpdateInfo;
	public String mLastUpdateTime;

	public CityAndWeather() {
		mWeatherIcon = "0";
		mLastUpdateTime = "0";
		mCurrentTemperature = null;
		mForecastList = new ArrayList<Forecast>();
		for (int i = 0; i < 4; i++) {
			Forecast forecast = new Forecast();
			mForecastList.add(forecast);
		}

	}

	public static class Forecast {
		public String obsdate;
		public String daycode;
		public String weathericon;
		public String temperaturerange;
		public String windspeed;
		public String winddirection;
		public String wind;
		public String hightemperature;
		public String lowtemperature;

		public Forecast() {

		}

	}

	public static final class Columns {
		public static final String cityName = "cityname";
		public static final String time = "time";
		public static final String weatherIcon = "weathericon";
		public static final String windDirection = "winddirection";
		public static final String windSpeed = "windspeed";
		public static final String wind = "wind";
		public static final String temperatureRange = "temperaturerange";
		public static final String updateInfo = "updateinfo";
		public static final String currentTemperature = "currenttemperature";
		public static final String lastUpdateTime = "lastupdatetime";

		public static final String forecastDay1Obsdate = "forecastday1obsdate";
		public static final String forecastDay1Daycode = "forecastday1daycode";
		public static final String forecastDay1WeatherIcon = "forecastday1weathericon";
		public static final String forecastDay1HighTemperature = "forecastday1hightemperature";
		public static final String forecastDay1LowTemperature = "forecastday1lowtemperature";
		public static final String forecastDay1TemperatureRange = "forecastday1temperaturerange";
		public static final String forecastDay1WindSpeed = "forecastday1windspeed";
		public static final String forecastDay1WindDirection = "forecastday1winddirection";
		public static final String forecastDay1Wind = "forecastday1wind";

		public static final String forecastDay2Obsdate = "forecastday2obsdate";
		public static final String forecastDay2Daycode = "forecastday2daycode";
		public static final String forecastDay2WeatherIcon = "forecastday2weathericon";
		public static final String forecastDay2HighTemperature = "forecastday2hightemperature";
		public static final String forecastDay2LowTemperature = "forecastday2lowtemperature";
		public static final String forecastDay2TemperatureRange = "forecastday2temperaturerange";
		public static final String forecastDay2WindSpeed = "forecastday2windspeed";
		public static final String forecastDay2WindDirection = "forecastday2winddirection";
		public static final String forecastDay2Wind = "forecastday2wind";

		public static final String forecastDay3Obsdate = "forecastday3obsdate";
		public static final String forecastDay3Daycode = "forecastday3daycode";
		public static final String forecastDay3WeatherIcon = "forecastday3weathericon";
		public static final String forecastDay3HighTemperature = "forecastday3hightemperature";
		public static final String forecastDay3LowTemperature = "forecastday3lowtemperature";
		public static final String forecastDay3TemperatureRange = "forecastday3temperaturerange";
		public static final String forecastDay3WindSpeed = "forecastday3windspeed";
		public static final String forecastDay3WindDirection = "forecastday3winddirection";
		public static final String forecastDay3Wind = "forecastday3wind";

		public static final String forecastDay4Obsdate = "forecastday4obsdate";
		public static final String forecastDay4Daycode = "forecastday4daycode";
		public static final String forecastDay4WeatherIcon = "forecastday4weathericon";
		public static final String forecastDay4HighTemperature = "forecastday4hightemperature";
		public static final String forecastDay4LowTemperature = "forecastday4lowtemperature";
		public static final String forecastDay4TemperatureRange = "forecastday4temperaturerange";
		public static final String forecastDay4WindSpeed = "forecastday4windspeed";
		public static final String forecastDay4WindDirection = "forecastday4winddirection";
		public static final String forecastDay4Wind = "forecastday4wind";
	}

	public CityAndWeather(Cursor c) {

	}

	public static CityAndWeather getCityAndWeatherData(Context mContext,String mCity) {

		CityAndWeather data = null;

		//初始化参数
		if (data == null) {
			data = new CityAndWeather();
		}

		return data;
	}

	private static CityAndWeather getDataByCorse(Cursor c) {
		CityAndWeather data = new CityAndWeather();
		if(c==null) return data;

		data.mCityName = c.getString(c.getColumnIndexOrThrow(Columns.cityName));
		data.mTime = c.getString(c.getColumnIndexOrThrow(Columns.time));
		data.mWeatherIcon = c.getString(c.getColumnIndexOrThrow(Columns.weatherIcon));
		data.mWindDirection =  c.getString(c.getColumnIndexOrThrow(Columns.windDirection));
		data.mWindSpeed = c.getString(c.getColumnIndexOrThrow(Columns.windSpeed));
		data.mWind =  c.getString(c.getColumnIndexOrThrow(Columns.wind));
		data.mTemperatureRange =  c.getString(c.getColumnIndexOrThrow(Columns.temperatureRange));
		data.mCurrentTemperature =  c.getString(c.getColumnIndexOrThrow(Columns.currentTemperature));
		data.mUpdateInfo =  c.getString(c.getColumnIndexOrThrow(Columns.updateInfo));
		data.mLastUpdateTime =  c.getString(c.getColumnIndexOrThrow(Columns.lastUpdateTime));

		ArrayList<Forecast> list=new ArrayList<Forecast>();

		Forecast forecast1 = new Forecast();
		forecast1.obsdate =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay1Obsdate));
		forecast1.daycode =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay1Daycode));
		forecast1.weathericon =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay1WeatherIcon));
		forecast1.temperaturerange =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay1TemperatureRange));
		forecast1.windspeed =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay1WindSpeed));
		forecast1.winddirection =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay1WindDirection));
		forecast1.wind =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay1Wind));
		forecast1.hightemperature =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay1HighTemperature));
		forecast1.lowtemperature =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay1LowTemperature));
		list.add(forecast1);

		Forecast forecast2 = new Forecast();
		forecast2.obsdate =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay2Obsdate));
		forecast2.daycode =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay2Daycode));
		forecast2.weathericon =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay2WeatherIcon));
		forecast2.temperaturerange =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay2TemperatureRange));
		forecast2.windspeed =  c.getString(c.getColumnIndexOrThrow( Columns.forecastDay2WindSpeed));
		forecast2.winddirection =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay2WindDirection));
		forecast2.wind =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay2Wind));
		forecast2.hightemperature =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay2HighTemperature));
		forecast2.lowtemperature =  c.getString(c.getColumnIndexOrThrow(Columns.forecastDay2LowTemperature));
		list.add(forecast2);

		Forecast forecast3 = new Forecast();
		forecast3.obsdate =c.getString(c.getColumnIndexOrThrow(Columns.forecastDay3Obsdate));
		forecast3.daycode = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay3Daycode));
		forecast3.weathericon = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay3WeatherIcon));
		forecast3.temperaturerange = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay3TemperatureRange));
		forecast3.windspeed = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay3WindSpeed));
		forecast3.winddirection = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay3WindDirection));
		forecast3.wind = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay3Wind));
		forecast3.hightemperature = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay3HighTemperature));
		forecast3.lowtemperature = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay3LowTemperature));
		list.add(forecast3);

		Forecast forecast4 = new Forecast();
		forecast4.obsdate = c.getString(c.getColumnIndexOrThrow( Columns.forecastDay4Obsdate));
		forecast4.daycode = c.getString(c.getColumnIndexOrThrow( Columns.forecastDay4Daycode));
		forecast4.weathericon = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay4WeatherIcon));
		forecast4.temperaturerange = c.getString(c.getColumnIndexOrThrow( Columns.forecastDay4TemperatureRange));
		forecast4.windspeed = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay4WindSpeed));
		forecast4.winddirection = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay4WindDirection));
		forecast4.wind = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay4Wind));
		forecast4.hightemperature = c.getString(c.getColumnIndexOrThrow(Columns.forecastDay4HighTemperature));
		forecast4.lowtemperature = c.getString(c.getColumnIndexOrThrow( Columns.forecastDay4LowTemperature));
		list.add(forecast4);

		data.mForecastList=list;
		return data;
	}

	public void setForecastDayWeathericon(int forcastDay, int icon) {
		mForecastList.get(forcastDay).weathericon = String.valueOf(icon);
	}

	public void setForecastDayObsdate(int forcastDay, String obsdate) {
		mForecastList.get(forcastDay).obsdate = obsdate;
	}

	public void setForecastDayDaycode(int forcastDay, String daycode) {
		mForecastList.get(forcastDay).daycode = daycode;
	}

	public void setForecastDayWindSpeed(int forcastDay, String speed) {
		mForecastList.get(forcastDay).windspeed = speed;
	}

	public void setForecastDayWindDirection(int forcastDay, String direction) {
		mForecastList.get(forcastDay).winddirection = direction;
	}

	public void setForecastDayHightemperature(int forcastDay, int hightemperature) {
		mForecastList.get(forcastDay).hightemperature = String.valueOf(hightemperature);
	}

	public void setForecastDayLowtemperature(int forcastDay, int lowtemperature) {
		mForecastList.get(forcastDay).lowtemperature = String.valueOf(lowtemperature);
	}



}
