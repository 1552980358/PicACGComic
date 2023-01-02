#include <jni.h>
#include <android/bitmap.h>

#include "unit.h"
#include "bitmap.h"
#include "copy_pixel.h"
#include "rect.h"
#include "log.h"

#define BITMAP_BUNDLE_FIELD_POINTER "pointer"
#define BITMAP_BUNDLE_FIELD_POINTER_SIG "J"

jfieldID get_bitmap_bundle_pointer(JNIEnv *, jobject);
bitmap_t *get_bitmap(JNIEnv *, jobject);
void cleanup(JNIEnv *, jobject, bitmap_t *);

extern "C"
JNIEXPORT jboolean JNICALL
Java_projekt_cloud_piece_pic_util_BitmapBundle_parseBitmap(JNIEnv *env, jobject thiz,
                                                           jobject jbitmap) {
    log_i("bitmap-bundle", "parseBitmap");

    AndroidBitmapInfo android_bitmap_info;
    if (AndroidBitmap_getInfo(env, jbitmap, &android_bitmap_info) != ANDROID_BITMAP_RESULT_SUCCESS) {
        log_i("bitmap-bundle", "parseBitmap: cannot get info");
        return false;
    }

    format_t buffer_format;
    switch (android_bitmap_info.format) {
        case ANDROID_BITMAP_FORMAT_RGBA_8888:
            buffer_format = BUFFER_FORMAT_32;
            break;
        case ANDROID_BITMAP_FORMAT_RGB_565:
        case ANDROID_BITMAP_FORMAT_RGBA_4444:
            buffer_format = BUFFER_FORMAT_16;
            break;
        default:
            log_i("bitmap-bundle", "parseBitmap: incorrect config");
            return false;
    }

    length_t bitmap_size = android_bitmap_info.width * android_bitmap_info.height;

    buffer_t pixels;
    if (AndroidBitmap_lockPixels(env, jbitmap, &pixels) != ANDROID_BITMAP_RESULT_SUCCESS) {
        log_i("bitmap-bundle", "parseBitmap: lock pixel failed");
        return false;
    }
    buffer_t buffer;
    switch (buffer_format) {
        case BUFFER_FORMAT_32: {
            log_i("bitmap-bundle", "parseBitmap: BUFFER_FORMAT_32");
            buffer = new pixel32_t[bitmap_size]();
            copy_pixel32_to_buffer((buffer32_t) pixels, (buffer32_t) buffer, bitmap_size);
            break;
        }
        case BUFFER_FORMAT_16: {
            log_i("bitmap-bundle", "parseBitmap: BUFFER_FORMAT_16");
            buffer = new pixel16_t[bitmap_size]();
            copy_pixel16_to_buffer((buffer16_t) pixels, (buffer16_t) buffer, bitmap_size);
            break;
        }
#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnreachableCode"
        default: {
            log_i("bitmap-bundle", "parseBitmap: incorrect format");
            AndroidBitmap_unlockPixels(env, jbitmap);
            return false;
        }
#pragma clang diagnostic pop
    }
    AndroidBitmap_unlockPixels(env, jbitmap);

    auto bitmap = new bitmap_t;
    bitmap->set_pixel_buffer(buffer, buffer_format, android_bitmap_info.format);
    bitmap->set_length(android_bitmap_info.width, android_bitmap_info.height);

    auto bitmap_bundle_pointer = get_bitmap_bundle_pointer(env, thiz);
    if (!bitmap_bundle_pointer) {
        log_i("bitmap-bundle", "parseBitmap: cannot find pointer field");
        return false;
    }
    env->SetLongField(thiz, bitmap_bundle_pointer, (ptr_t) bitmap);
    return true;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_projekt_cloud_piece_pic_util_BitmapBundle_recoverBitmap(JNIEnv *env, jobject thiz,
                                                             jobject jbitmap) {
    log_i("bitmap-bundle", "recoverBitmap");

    auto bitmap = get_bitmap(env, thiz);
    AndroidBitmapInfo android_bitmap_info;
    if (AndroidBitmap_getInfo(env, jbitmap, &android_bitmap_info) != ANDROID_BITMAP_RESULT_SUCCESS) {
        log_i("bitmap-bundle", "recoverBitmap: cannot get info");
        return false;
    }
    auto size = bitmap->get_height() * bitmap->get_width();
    if (android_bitmap_info.height * android_bitmap_info.width != size) {
        log_i("bitmap-bundle", "recoverBitmap: incorrect size");
        return false;
    }
    if (android_bitmap_info.format != bitmap->get_config()) {
        log_i("bitmap-bundle", "recoverBitmap: incorrect config");
        return false;
    }
    buffer_t pixels;
    if (AndroidBitmap_lockPixels(env, jbitmap, &pixels) != 0) {
        log_i("bitmap-bundle", "recoverBitmap: lock pixel failed");
        return false;
    }
    switch (bitmap->get_format()) {
        case BUFFER_FORMAT_32: {
            log_i("bitmap-bundle", "recoverBitmap: BUFFER_FORMAT_32");
            copy_pixel32_to_buffer((buffer32_t) bitmap->get_buffer(), (buffer32_t) pixels, size);
            break;
        }
        case BUFFER_FORMAT_16: {
            log_i("bitmap-bundle", "recoverBitmap: BUFFER_FORMAT_16");
            copy_pixel16_to_buffer((buffer16_t) bitmap->get_buffer(), (buffer16_t) pixels, size);
            break;
        }
        default: {
            log_i("bitmap-bundle", "recoverBitmap: incorrect format");
            AndroidBitmap_unlockPixels(env, jbitmap);
            cleanup(env, thiz, bitmap);
            return false;
        }
    }
    AndroidBitmap_unlockPixels(env, jbitmap);
    cleanup(env, thiz, bitmap);
    return true;
}

void cleanup(JNIEnv *env, jobject thiz, bitmap_t *bitmap) {
    // Clean up
    delete bitmap;
    auto pointer = get_bitmap_bundle_pointer(env, thiz);
    env->SetLongField(thiz, pointer, 0);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_projekt_cloud_piece_pic_util_BitmapBundle_getSize(JNIEnv *env, jobject thiz, jobject jrect) {
    log_i("BitmapBundle", "getSize");

    auto bitmap = get_bitmap(env, thiz);
    if (!bitmap) {
        log_i("bitmap-bundle", "getSize: null pointer");
        return false;
    }
    rect_set(env, jrect, 0, 0, bitmap->get_width(), bitmap->get_height());
    return true;
}

extern "C"
JNIEXPORT jint JNICALL
Java_projekt_cloud_piece_pic_util_BitmapBundle_getConfig(JNIEnv *env, jobject thiz) {
    log_i("BitmapBundle", "getConfig");

    auto bitmap = get_bitmap(env, thiz);
    if (!bitmap) {
        log_i("bitmap-bundle", "parseBitmap: null bitmap");
        return 0;
    }
    return bitmap->get_config();
}

jfieldID get_bitmap_bundle_pointer(JNIEnv *env, jobject thiz) {
    auto bitmap_bundle = env->GetObjectClass(thiz);
    return env->GetFieldID(bitmap_bundle,
                           BITMAP_BUNDLE_FIELD_POINTER,
                           BITMAP_BUNDLE_FIELD_POINTER_SIG
                           );
}

bitmap_t *get_bitmap(JNIEnv *env, jobject thiz) {
    auto pointer = env->GetLongField(thiz, get_bitmap_bundle_pointer(env, thiz));
    return (bitmap_t *) pointer;
}