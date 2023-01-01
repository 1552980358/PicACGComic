#include "bitmap.h"

bitmap::~bitmap() {
    if (_buffer_format == BUFFER_FORMAT_16) {
        delete[] (buffer16_t) _pixel_buffer;
    } else {
        delete[] (buffer32_t) _pixel_buffer;
    }
}

void bitmap::set_pixel_buffer(buffer_t buffer, const format_t &format, const config_t &config) {
    _pixel_buffer = buffer;
    _buffer_format = format;
    _config = config;
}

void bitmap::set_length(const length_t &width, const length_t &height) {
    _width = width;
    _height = height;
}

length_t bitmap::get_width() const {
    return _width;
}

length_t bitmap::get_height() const {
    return _height;
}

format_t bitmap::get_format() const {
    return _buffer_format;
}

config_t bitmap::get_config() const {
    return _config;
}

buffer_t bitmap::get_buffer() {
    return _pixel_buffer;
}
