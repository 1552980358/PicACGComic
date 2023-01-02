#ifndef PICACGCOMIC_LOG_H
#define PICACGCOMIC_LOG_H

#include "android/log.h"

#define log_i(tag, str) __android_log_print(ANDROID_LOG_INFO, tag, str)

#endif //PICACGCOMIC_LOG_H
