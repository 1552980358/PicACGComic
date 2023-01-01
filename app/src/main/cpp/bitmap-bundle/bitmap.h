#ifndef PICACGCOMIC_BITMAP_H
#define PICACGCOMIC_BITMAP_H

#include "unit.h"

#define BUFFER_FORMAT_16 16
#define BUFFER_FORMAT_32 32

class bitmap {

private:
    buffer_t _pixel_buffer = nullptr;
    format_t _buffer_format;

    length_t _width;
    length_t _height;

    config_t _config;

public:
    ~bitmap();

    void set_pixel_buffer(buffer_t, const format_t &, const config_t &);

    void set_length(const length_t &, const length_t &);

    length_t get_width() const;
    length_t get_height() const;

    buffer_t get_buffer();

    format_t get_format() const;

    config_t get_config() const;

};

typedef bitmap bitmap_t;

#endif //PICACGCOMIC_BITMAP_H
