LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files) 

LOCAL_RESOURCE_DIR += \
	$(LOCAL_PATH)/res \
	frameworks/support/v7/cardview/res	\
	

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.cardview \
    --extra-packages android.support.v7.recyclerview \
    --extra-packages android.support.v7.appcompat \
	--extra-packages android.support.v4

# 定义引用的jar包：名字自定义，后面会针对名字进行路径说明  
LOCAL_STATIC_JAVA_LIBRARIES :=carweather-litepal-1.5.1 \
		carweather-glide-3.7.0 \
		carweather-okhttp-3.2.0 \
		carweather-okio-1.6.0 \
		carweather-AMap_Location_V3.6.1_20171012 \
		carweather-AMap_Search_V5.3.1_20170817 \
		carweather-support_design \
		carweather-fastjson-1.2.2 \
		carweather-crashLog \
        android-support-v7-appcompat \
        android-support-v4

LOCAL_PROGUARD_ENABLED:= disabled  
LOCAL_PACKAGE_NAME := Weather
#LOCAL_SDK_VERSION := current

LOCAL_CERTIFICATE := platform
##LOCAL_PRIVILEGED_MODULE := true

include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
# 定义jar包 aar包的路径 :后面跟的是相对Android.mk文件的相对路径  
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := carweather-glide-3.7.0:../../libs/glide-3.7.0.jar \
										carweather-okhttp-3.2.0:../../libs/okhttp-3.2.0.jar \
										carweather-okio-1.6.0:../../libs/okio-1.6.0.jar \
										carweather-AMap_Location_V3.6.1_20171012:../../libs/AMap_Location_V3.6.1_20171012.jar \
										carweather-AMap_Search_V5.3.1_20170817:../../libs/AMap_Search_V5.3.1_20170817.jar \
										carweather-support_design:../../libs/support_design.jar \
										carweather-fastjson-1.2.2:../../libs/fastjson-1.2.2.jar \
										carweather-litepal-1.5.1:../../libs/litepal-1.5.1.jar \
										carweather-crashLog:../../libs/crashLog.jar
										
										
# 指明合并后的AndroidManifest.xml的路径 （一般不用指定）   
LOCAL_MANIFEST_FILE := $(LOCAL_PATH)/AndroidManifest.xml
include $(BUILD_MULTI_PREBUILT)
include $(call all-makefiles-under, $(LOCAL_PATH))

