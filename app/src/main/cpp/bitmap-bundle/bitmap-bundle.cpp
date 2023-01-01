#include <jni.h>
#include <android/bitmap.h>

#include "unit.h"
#include "bitmap.h"
#include "copy_pixel.h"
#include "rect.h"

#define BITMAP_BUNDLE_FIELD_POINTER "pointer"
#define BITMAP_BUNDLE_FIELD_POINTER_SIG "J"

jfieldID get_bitmap_bundle_pointer(JNIEnv *, jobject);
bitmap_t *get_bitmap(JNIEnv *, jobject);

extern "C"
JNIEXPORT jboolean JNICALL
Java_projekt_cloud_piece_pic_util_BitmapBundle_parseBitmap(JNIEnv *env, jobject thiz,
                                                           jobject jbitmap) {

    AndroidBitmapInfo android_bitmap_info;
    if (AndroidBitmap_getInfo(env, jbitmap, &android_bitmap_info) != ANDROID_BITMAP_RESULT_SUCCESS) {
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
            return false;
    }

    length_t bitmap_size = android_bitmap_info.width * android_bitmap_info.height;

    buffer_t pixels;
    if (AndroidBitmap_lockPixels(env, jbitmap, &pixels) != 0) {
        return false;
    }
    buffer_t buffer;
    switch (buffer_format) {
        case BUFFER_FORMAT_32: {
            buffer = new pixel32_t[bitmap_size]();
            copy_pixel32_to_buffer((buffer32_t) pixels, (buffer32_t) buffer, bitmap_size);
            break;
        }
        case BUFFER_FORMAT_16: {
            buffer = new pixel16_t[bitmap_size]();
            copy_pixel16_to_buffer((buffer16_t) pixels, (buffer16_t) buffer, bitmap_size);
            break;
        }
#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnreachableCode"
        default: {
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
    env->SetLongField(thiz, bitmap_bundle_pointer, (ptr_t) bitmap);
    return true;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_projekt_cloud_piece_pic_util_BitmapBundle_recoverBitmap(JNIEnv *env, jobject thiz,
                                                             jobject jbitmap) {
    auto bitmap = get_bitmap(env, thiz);
    AndroidBitmapInfo android_bitmap_info;
    if (AndroidBitmap_getInfo(env, jbitmap, &android_bitmap_info) != ANDROID_BITMAP_RESULT_SUCCESS) {
        return false;
    }
    auto size = bitmap->get_height() * bitmap->get_width();
    if (android_bitmap_info.height * android_bitmap_info.width != size) {
        return false;
    }
    if (android_bitmap_info.format != bitmap->get_config()) {
        return false;
    }
    buffer_t pixels;
    if (AndroidBitmap_lockPixels(env, jbitmap, &pixels) != 0) {
        return false;
    }
    switch (bitmap->get_format()) {
        case BUFFER_FORMAT_16:
            copy_pixel16_to_buffer((buffer16_t) bitmap->get_buffer(), (buffer16_t) pixels, size);
            break;
        case BUFFER_FORMAT_32:
            copy_pixel32_to_buffer((buffer32_t) bitmap->get_buffer(), (buffer32_t) pixels, size);
            break;
        default:
            AndroidBitmap_unlockPixels(env, jbitmap);
            return false;
    }
    AndroidBitmap_unlockPixels(env, jbitmap);

    return true;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_projekt_cloud_piece_pic_util_BitmapBundle_getSize(JNIEnv *env, jobject thiz, jobject jrect) {
    auto bitmap = get_bitmap(env, thiz);
    rect_set(env, jrect, 0, 0, bitmap->get_width(), bitmap->get_height());
}

extern "C"
JNIEXPORT jint JNICALL
Java_projekt_cloud_piece_pic_util_BitmapBundle_getConfig(JNIEnv *env, jobject thiz) {
    auto bitmap = get_bitmap(env, thiz);
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