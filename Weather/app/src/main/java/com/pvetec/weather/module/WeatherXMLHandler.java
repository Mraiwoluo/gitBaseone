package com.pvetec.weather.module;

import android.content.Context;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WeatherXMLHandler extends DefaultHandler {
	private CityAndWeather mCityAndWeather;
	private String mPreTag;
	private boolean mIsForcastInfo;
	private boolean mIsDayTimeForcast;
	private int mForecastDayIndex;
	private int mDayHighTemperature;
	private int mDayLowTemperature;
	private int mNightHighTemperature;
	private int mNightLowTemperature;
	private int mHighTemperature;
	private int mLowTemperature;
	private boolean mIsDataValid;
	private String mCityId;
	private String mCity;
	public static final int FORCAST_DAY = 4;

	private Context mContext;

	public WeatherXMLHandler(Context mContext,String mCity) {
		this.mCity = mCity;
		this.mContext=mContext.getApplicationContext();
	}

	@Override
	public void startDocument() throws SAXException {
		mCityAndWeather = CityAndWeather.getCityAndWeatherData(mContext,mCity);
		mIsForcastInfo = false;
		mIsDayTimeForcast = true;
		mIsDataValid = true;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("forecast".equals(localName)) {
			mIsForcastInfo = true;
		} else if ("day".equals(localName)) {
			mForecastDayIndex = Integer.parseInt(attributes.getValue("number"));
			mForecastDayIndex--;
		}
		mPreTag = localName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (!mIsDataValid) {
			return;
		}
		String data = new String(ch, start, length);
		if (!mIsForcastInfo) {
			if ("url".equals(mPreTag)) {
				if (data.contains("cityId=")) {
					mIsDataValid = checkWeatherDataValid(data);
				}
			} else if ("time".equals(mPreTag)) {
				mCityAndWeather.mTime = data;
			} else if ("temperature".equals(mPreTag)) {
				mCityAndWeather.mCurrentTemperature = data;
			} else if ("weathericon".equals(mPreTag)) {
				mCityAndWeather.mWeatherIcon = data;
			} else if ("windspeed".equals(mPreTag)) {
				mCityAndWeather.mWindSpeed = data;
			} else if ("winddirection".equals(mPreTag)) {
				mCityAndWeather.mWindDirection = data;
			}
		} else {
			if ("obsdate".equals(mPreTag)
					&& mForecastDayIndex <FORCAST_DAY) {
				mCityAndWeather.setForecastDayObsdate(mForecastDayIndex, data);
			} else if ("daycode".equals(mPreTag)
					&& mForecastDayIndex < FORCAST_DAY) {
				mCityAndWeather.setForecastDayDaycode(mForecastDayIndex, data);
			} else if ("daytime".equals(mPreTag)) {
				mIsDayTimeForcast = true;
			} else if ("nighttime".equals(mPreTag)) {
				mIsDayTimeForcast = false;
			} else if ("weathericon".equals(mPreTag)
					&& mForecastDayIndex < FORCAST_DAY) {
				if (mIsDayTimeForcast) {
					mCityAndWeather.setForecastDayWeathericon(
							mForecastDayIndex, Integer.parseInt(data));
				}
			} else if ("hightemperature".equals(mPreTag)
					&& mForecastDayIndex < FORCAST_DAY) {
				if (mIsDayTimeForcast) {
					mDayHighTemperature = Integer.parseInt(data);
				} else {
					mNightHighTemperature = Integer.parseInt(data);
					mHighTemperature = (mDayHighTemperature > mNightHighTemperature) ? mDayHighTemperature
							: mNightHighTemperature;
					mCityAndWeather.setForecastDayHightemperature(
							mForecastDayIndex, mHighTemperature);
				}
			} else if ("lowtemperature".equals(mPreTag)
					&& mForecastDayIndex < FORCAST_DAY) {
				if (mIsDayTimeForcast) {
					mDayLowTemperature = Integer.parseInt(data);
				} else {
					mNightLowTemperature = Integer.parseInt(data);
					mLowTemperature = (mNightLowTemperature < mDayLowTemperature) ? mNightLowTemperature
							: mDayLowTemperature;
					mCityAndWeather.setForecastDayLowtemperature(
							mForecastDayIndex, mLowTemperature);
				}
			} else if ("windspeed".equals(mPreTag)
					&& mForecastDayIndex < FORCAST_DAY) {
				if (mIsDayTimeForcast) {
					mCityAndWeather.setForecastDayWindSpeed(mForecastDayIndex,
							data);
				}
			} else if ("winddirection".equals(mPreTag)
					&& mForecastDayIndex < FORCAST_DAY) {
				if (mIsDayTimeForcast) {
					mCityAndWeather.setForecastDayWindDirection(
							mForecastDayIndex, data);
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		mPreTag = null;
	}

	public CityAndWeather getCityAndWeather() {
		return mCityAndWeather;
	}

	private boolean checkWeatherDataValid(String urlString) {
		if (mCityId == null) {
			return true;
		}
		String[] stringArray;
		stringArray = urlString.split("=");
		if (stringArray.length > 1 && mCityId.equals(stringArray[1])) {
			return true;
		}
		return false;
	}
}
