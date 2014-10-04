LOCAL_PATH := $(call my-dir)

SDK_PATH := $(LOCAL_PATH)/../..
SRC_PATH := .
LIB_PATH := libraries
BSL_PATH := security
NSP_PATH := nsp

APP_PLATFORM := android-8
NDK_DEBUG=1

include $(CLEAR_VARS)

LOCAL_C_INCLUDES:= $(LOCAL_PATH)/$(NSP_PATH)
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(NSP_PATH)/gen-ncl
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(LIB_PATH)/concurrency
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(LIB_PATH)
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(BSL_PATH)
LOCAL_C_INCLUDES+= $(SRC_PATH)
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(LIB_PATH)/SDL2_net-2.0.0/android_include
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(LIB_PATH)/SDL2-2.0.3/android_include
	
LOCAL_CFLAGS := -DANDROID $(LOCAL_CFLAGS)
LOCAL_CFLAGS := -DNCL_H_INCLUDED $(LOCAL_CFLAGS)
LOCAL_CFLAGS := -DNCL_DEBUG_STREAM $(LOCAL_CFLAGS)
#LOCAL_CFLAGS := -D__cplusplus $(LOCAL_CFLAGS)

LOCAL_MODULE := _nymi_sdk

LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL \
				-DNCL_ANDROID \
				-DNCL_DEBUG_STREAM

LOCAL_SRC_FILES := $(SRC_PATH)/callbacks.c\
$(SRC_PATH)/error.c\
$(SRC_PATH)/nclX.c\
$(SRC_PATH)/android/callbacks.c\
$(SRC_PATH)/android/fromBle.c\
$(SRC_PATH)/android/ncl.c\
$(SRC_PATH)/android/nclAndroid.c\
$(SRC_PATH)/android/toBle.c\
$(LIB_PATH)/concurrency/concurrency.c\
$(NSP_PATH)/gen-ncl/deserialize.c\
$(NSP_PATH)/gen-ncl/serialize.c\
$(BSL_PATH)/cryptographic_tools.c\
$(BSL_PATH)/ECCP-256/ECC.c\
$(BSL_PATH)/ECCP-256/ECCp256.c\
$(BSL_PATH)/ECCP-256/GFp256.c\
$(BSL_PATH)/ECCP-256/SHA256.c\
$(BSL_PATH)/ECCP-256/Zn256.c\
$(BSL_PATH)/polarssl/aes.c\
$(BSL_PATH)/SHA256/sha2.c\
$(BSL_PATH)/SHA256/SHA256Wrapper.c\
$(BSL_PATH)/prg/linux/prg.c\

LOCAL_LDLIBS := -ldl -llog


include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := SDL
LOCAL_SRC_FILES := compiled_sdl/$(TARGET_ARCH_ABI)/libSDL.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := sdl_net
LOCAL_SRC_FILES := compiled_sdl/$(TARGET_ARCH_ABI)/libsdl_net.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES:= $(LOCAL_PATH)/$(NSP_PATH)
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(NSP_PATH)/gen-ncl
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(LIB_PATH)/concurrency
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(LIB_PATH)
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(BSL_PATH)
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(SRC_PATH)
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(LIB_PATH)/SDL2_net-2.0.0/android_include
LOCAL_C_INCLUDES+= $(LOCAL_PATH)/$(LIB_PATH)/SDL2-2.0.3/android_include
	
LOCAL_CFLAGS := -DANDROID $(LOCAL_CFLAGS)
LOCAL_CFLAGS := -DNCL_H_INCLUDED $(LOCAL_CFLAGS)
#LOCAL_CFLAGS := -D__cplusplus $(LOCAL_CFLAGS)

LOCAL_MODULE := _nymi_sdk_net

LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL \
				-DNCL_ANDROID

LOCAL_SRC_FILES := $(SRC_PATH)/callbacks.c\
$(SRC_PATH)/error.c\
$(SRC_PATH)/nclX.c\
$(SRC_PATH)/nclNet.c\
$(LIB_PATH)/gobs/gobs.c\
$(SRC_PATH)/android/callbacks.c\
$(SRC_PATH)/android/ncl.c\
$(SRC_PATH)/android/toBle.c\
$(LIB_PATH)/concurrency/concurrency.c\
$(NSP_PATH)/gen-ncl/deserialize.c\
$(NSP_PATH)/gen-ncl/serialize.c\
$(BSL_PATH)/cryptographic_tools.c\
$(BSL_PATH)/ECCP-256/ECC.c\
$(BSL_PATH)/ECCP-256/ECCp256.c\
$(BSL_PATH)/ECCP-256/GFp256.c\
$(BSL_PATH)/ECCP-256/SHA256.c\
$(BSL_PATH)/ECCP-256/Zn256.c\
$(BSL_PATH)/polarssl/aes.c\
$(BSL_PATH)/SHA256/sha2.c\
$(BSL_PATH)/SHA256/SHA256Wrapper.c\
$(BSL_PATH)/prg/linux/prg.c\

LOCAL_STATIC_LIBRARIES += SDL
LOCAL_STATIC_LIBRARIES += sdl_net


LOCAL_LDLIBS := -ldl -llog


include $(BUILD_SHARED_LIBRARY)
