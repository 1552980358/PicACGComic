list(
        APPEND
        BITMAP-BUNDLE-SRC-H
        bitmap.h
        unit.h
        copy_pixel.h
        rect.h
)

list(
        APPEND
        BITMAP-BUNDLE-SRC-CPP
        bitmap-bundle.cpp
        bitmap.cpp
        copy_pixel.cpp
        rect.cpp
)

add_library(
        bitmap-bundle-lib
        SHARED
        ${BITMAP-BUNDLE-SRC-H}
        ${BITMAP-BUNDLE-SRC-CPP}
)