# Build for arm only
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    	:= libmplame
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include/
LOCAL_SRC_FILES 	:= ./libmplame/bitstream.c \
	./libmplame/encoder.c \
	./libmplame/fft.c \
	./libmplame/gain_analysis.c \
	./libmplame/id3tag.c \
	./libmplame/lame.c \
	./libmplame/mpglib_interface.c \
	./libmplame/newmdct.c \
	./libmplame/presets.c \
	./libmplame/psymodel.c \
	./libmplame/quantize.c \
	./libmplame/quantize_pvt.c \
	./libmplame/reservoir.c \
	./libmplame/set_get.c \
	./libmplame/tables.c \
	./libmplame/takehiro.c \
	./libmplame/util.c \
	./libmplame/vbrquantize.c \
	./libmplame/VbrTag.c \
	./libmplame/version.c \

LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)