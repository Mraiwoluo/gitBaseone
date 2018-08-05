package com.pvetec.weather.utils;

/**
 * Created by zerain on 2016/9/23.
 */

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class IPNetUtils {
    /**
     * 格式化参数 key=value&key1=value1.....
     * @param encode
     * @param params
     * @return
     */
    public static StringBuffer formatParams(String encode, Map<String, String> params) {
        StringBuffer buffer = null;
        try {
            if (null != params) {
                buffer = new StringBuffer("");
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    if (null != entry) {
                        buffer
                                .append(entry.getKey())
                                .append("=")
                                .append(URLEncoder.encode(entry.getValue(), encode));
                        if (iterator.hasNext()) {
                            buffer.append("&");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 格式化参数
     * 如a=1&b=2, formatParams("utf-8,"a,b", "1","2")
     * @param encode
     * @param keys
     * @param values
     * @return
     */
    public static StringBuffer formatParams(String encode, String keys, String ...values) {
        StringBuffer buffer = null;
        try {
            if (null != keys && keys.matches("^( *\\w+ *,)*( *\\w+ *)$")) {
                //移除空格和末尾的','
                String[] params = keys.replaceAll("( +)|(, *$)", "").split(",");
                if (null != params && null != values) {
                    int len = (params.length < values.length) ? params.length : values.length;
                    if (len > 0) {
                        buffer = new StringBuffer("");
                        String key, value;
                        for (int i = 0; i < len; i++) {
                            if (null != (key = params[i])) {
                                if (null == (value = values[i])) {
                                    value = "";
                                }
                                if (!"".equals(buffer.toString())) {
                                    buffer.append("&");
                                }
                                buffer.append(key).append("=").append(URLEncoder.encode(value, encode));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 格式化参数
     * bundle.putString("a", "1");
     * bundle.putString("b", "2");
     * 如a=1&b=2, formatParams("utf-8,"a,b", bundle)
     * @param encode
     * @param keys
     * @param values
     * @return
     */
    public static StringBuffer formatParams(String encode, String keys, Bundle values) {
        StringBuffer buffer = null;
        try {
            if (null != keys && keys.matches("^( *\\w+ *,)*( *\\w+ *)$")) {
                //移除空格和末尾的','
                String[] params = keys.replaceAll("( +)|(, *$)", "").split(",");
                if (null != params && null != values) {
                    int len = (params.length < values.size()) ? params.length : values.size();
                    if (len > 0) {
                        buffer = new StringBuffer("");
                        String key, value;
                        for (int i = 0; i < len; i++) {
                            if (null != (key = params[i])) {
                                if (null == (value = values.getString(key))) {
                                    value = "";
                                }
                                if (!"".equals(buffer.toString())) {
                                    buffer.append("&");
                                }
                                buffer.append(key).append("=").append(URLEncoder.encode(value, encode));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 创建一个客户端的socket
     * @param url 网络路径
     * @param port 端口
     * @param timeout 超时
     * @return
     * @throws IOException
     */
    /**
     * 创建一个客户端的socket
     * @param url 网络路径
     * @param port 端口
     * @param connectTimeout 连接超时
     * @param ioTimeout io操作超时,如读写
     * @return
     * @throws IOException
     */
    public static Socket createClientSocket(String url, int port, int connectTimeout, int ioTimeout) throws IOException {
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress(url, port);
        if (ioTimeout > 0) {
            /**setSotimeout(timeout)是表示如果对方连接状态timeout ms 没有收到数据的话强制断开客户端
             如果想要长连接的话，可以使用心跳包来通知服务器，也就是我没有发给你数据，但是我告诉你我还活着*/
            socket.setSoTimeout(ioTimeout);
        }
        if (connectTimeout > 0) {
            socket.connect(address, connectTimeout);
        } else {
            socket.connect(address);
        }
        return socket;
    }

    public static boolean writeSocket(Socket socket, byte[] data, int offset, int count) throws IOException {
        if (null != socket && null != data) {
            OutputStream os = socket.getOutputStream();
            if (null != os) {
                try {
                    os.write(data, offset, count);
                    return true;
                } finally {
                    os.close();
                }
            }
        }
        return false;
    }

    public static boolean writeSocket(Socket socket, byte[] data) throws IOException {
        if (null != socket && null != data) {
            OutputStream os = socket.getOutputStream();
            if (null != os) {
                try {
                    os.write(data);
                    return true;
                } finally {
                    os.close();
                }
            }
        }
        return false;
    }

    public static int readSocket(Socket socket, byte[] data, int offset, int count) throws IOException {
        if (null != socket && null != data) {
            InputStream is = socket.getInputStream();
            if (null != is) {
                try {
                    return is.read(data, offset, count);
                } finally {
                    is.close();
                }
            }
        }
        return 0;
    }

    public static int readSocket(Socket socket, byte[] data) throws IOException {
        if (null != socket && null != data) {
            InputStream is = socket.getInputStream();
            if (null != is) {
                try {
                    return is.read(data);
                } finally {
                    is.close();
                }
            }
        }
        return 0;
    }

    public static int readSocket(Socket socket) throws IOException {
        if (null != socket) {
            InputStream is = socket.getInputStream();
            if (null != is) {
                try {
                    return is.read();
                } finally {
                    is.close();
                }
            }
        }
        return 0;
    }

    {

    }


    public static StringBuffer sendGetRequest(String urlPath, String encode, int connectTimeout, int readTimeout) throws IOException {
        URL url = null;
        try {
            url = new URL(urlPath);
        } catch (MalformedURLException e) {
            log("sendGetRequest "+ e.toString());
        }
        if (null != url) {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connectTimeout > 0) {
                connection.setConnectTimeout(connectTimeout);
            }
            if (readTimeout > 0) {
                connection.setReadTimeout(readTimeout);
            }
            try {
                connection.connect();
                String str;
                StringBuffer result = new StringBuffer();
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, encode));

                while ((str = reader.readLine()) != null) {
                    result.append(str);
                    result.append("\r\n");
                }
                reader.close();
                return result;
            } catch (IOException e) {
                log(e.toString());
            }
        }
        return null;
    }

    public static StringBuffer sendGetRequest(String urlPath, String encode, int connectTimeout, int readTimeout, String params, String ...values) throws IOException {
        StringBuffer extra = formatParams(encode, params, values);
        return sendGetRequest(urlPath+((null!=extra)?extra.toString():""), encode, connectTimeout, readTimeout);
    }

    public static StringBuffer sendGetRequest(String urlPath, String encode, int connectTimeout, int readTimeout, String params, Bundle values)  throws IOException {
        StringBuffer extra = formatParams(encode, params, values);
        return sendGetRequest(urlPath+((null!=extra)?extra.toString():""), encode, connectTimeout, readTimeout);
    }

    public static StringBuffer sendRequest(String urlPath, String encode, String method, int timeout, byte[] data) throws IOException {
        if (null != urlPath && null != method && null != data && null != encode) {
            if (!"GET".equals(method) && !"get".equals(method)) {
                URL url = new URL(urlPath);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (timeout > 0) {
                    httpURLConnection.setConnectTimeout(timeout);   //设置连接超时时间
                }
                httpURLConnection.setDoInput(true);                 //打开输入流，以便从服务器获取数据
                httpURLConnection.setDoOutput(true);            //打开输出流，以便向服务器提交数据
                httpURLConnection.setRequestMethod(method);         //throw ProtocolException
                if ("POST".equals(httpURLConnection.getRequestMethod())) {
                    httpURLConnection.setUseCaches(false);              //使用Post方式不能使用缓存
                }
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); //设置请求体的类型是文本类型
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));//设置请求体的长度
                OutputStream outputStream = httpURLConnection.getOutputStream();//获得输出流，向服务器写入数据
                if (null != data) {
                    outputStream.write(data);
                }
                int response = httpURLConnection.getResponseCode();
                StringBuffer result = new StringBuffer();
                if (response == HttpURLConnection.HTTP_OK) {
                    String read;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), encode));
                    while ((read = reader.readLine()) != null) {
                        result.append(read);
                        result.append("\r\n");
                    }
                    reader.close();
                } else {
                    result.append(response);
                }
                return result;
            }
        }
        return null;
    }

    public static StringBuffer sendPostRequest(String urlPath, String encode, int timeout, byte[] data) throws IOException {
        return sendRequest(urlPath, encode, "POST", timeout, data);
    }

    public static StringBuffer sendPostRequest(String urlPath, String encode, int timeout, Map<String, String> params)  throws IOException {
        StringBuffer extra = formatParams(encode, params);
        log("http send post","url " + urlPath, "params " + params, "extra " + extra.toString());
        return sendPostRequest(urlPath, encode, timeout, (null == extra)?null:extra.toString().getBytes());
    }

    public static StringBuffer sendPostRequest(String urlPath, String encode, int timeout, String params, String ...values)  throws IOException {
        StringBuffer extra = formatParams(encode, params, values);
        log("http send post","url " + urlPath, "params " + params, "extra " + extra.toString());
        return sendPostRequest(urlPath, encode, timeout, (null == extra)?null:extra.toString().getBytes());
    }

    public static StringBuffer sendPostRequest(String urlPath, String encode, int timeout, String params, Bundle values)  throws IOException {
        StringBuffer extra = formatParams(encode, params, values);
        log("http send post","url " + urlPath, "params " + params, "extra " + extra.toString());
        return sendPostRequest(urlPath, encode, timeout, (null == extra)?null:extra.toString().getBytes());
    }

    public static InputStream sendGetRequest(String urlPath, int connectTimeout, int readTimeout){
        URL url = null;
        try {
            url = new URL(urlPath);
        } catch (MalformedURLException e) {
            log("sendGetRequest "+ e.toString());
        }
        if (null != url) {
            try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connectTimeout > 0) {
                connection.setConnectTimeout(connectTimeout);
            }
            if (readTimeout > 0) {
                connection.setReadTimeout(readTimeout);
            }

             connection.connect();
             if (connection.getResponseCode() == 200) {
                    InputStream is = connection.getInputStream();
                    return is;
              }
            } catch (IOException e) {
                log(e.toString());
            }
        }
        return null;
    }

    public static void log(String ...msgs) {
        String str = "";
        if (null != msgs && msgs.length > 0) {
            for (String msg: msgs) {
                if (null != msg) {
                    str += " ";
                    str += msg;
                }
            }
        }
        Log.d(TAG, str);
    }
    private static final String TAG = IPNetUtils.class.getSimpleName();
}