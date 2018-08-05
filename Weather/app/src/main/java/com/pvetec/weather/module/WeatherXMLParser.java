package com.pvetec.weather.module;

import android.content.Context;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class WeatherXMLParser {
	public static CityAndWeather parseWeatherInfoXML(InputStream inStream, Context mContext,String mCity)
			throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser();
		WeatherXMLHandler handler = new WeatherXMLHandler(mContext,mCity);
		saxParser.parse(inStream, handler);
		inStream.close();
		return handler.getCityAndWeather();
	}
}
