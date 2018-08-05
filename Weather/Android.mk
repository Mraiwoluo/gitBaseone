LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
#include $(CLEAR_VARS)
include $(call first-makefiles-under,$(LOCAL_PATH))
