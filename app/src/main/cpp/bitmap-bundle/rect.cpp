#include "rect.h"

/**
 * public void set(int left, int top, int right, int bottom)
 **/
#define RECT_METHOD_SET "set"
#define RECT_METHOD_SET_SIG "(IIII)V"

jclass get_rect_class(JNIEnv *env, jobject jrect) {
    return env->GetObjectClass(jrect);
}

jmethodID get_rect_set_id(JNIEnv *env, jclass rect) {
    return env->GetMethodID(rect, RECT_METHOD_SET, RECT_METHOD_SET_SIG);
}

void call_rect_set(JNIEnv *env, jobject jrect, jmethodID rect_set, const length_t &top,
                   const length_t &left , const length_t &right, const length_t &bottom) {
    env->CallVoidMethod(jrect, rect_set, top, left, right, bottom);
}

void rect_set(JNIEnv *env, jobject jrect, const length_t &top, const length_t &left,
              const length_t &right, const length_t &bottom) {
    auto rect_class = get_rect_class(env, jrect);
    auto rect_set_id = get_rect_set_id(env, rect_class);
    call_rect_set(env, jrect, rect_set_id, top, left, right, bottom);
}