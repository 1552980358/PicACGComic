#include "copy_pixel.h"

void copy_pixel32_to_buffer(buffer32_t from, buffer32_t to, const length_t &size) {
    for (int i = 0; i < size; ++i) {
        *(to + i) = *(from + i);
    }
}

void copy_pixel16_to_buffer(buffer16_t from, buffer16_t to, const length_t &size) {
    for (int i = 0; i < size; ++i) {
        *(to + i) = *(from + i);
    }
}