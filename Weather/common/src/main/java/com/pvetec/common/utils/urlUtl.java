package com.pvetec.common.utils;

import com.pvetec.common.Constants;

public class urlUtl {

    public static String buildAppUrl(String path) {
        return Constants.HOST + "/" + Constants.PATH_FETCH_FILE_BY_PATH +  path;
    }

    public static String buildImageUrl(String path) {
        return Constants.HOST + "/" + Constants.PATH_FETCH_FILE_BY_PATH + path;
    }
    
}
