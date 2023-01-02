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
        log.h
)

add_library(
        bitmap-bundle-lib
        SHARED
        ${BITMAP-BUNDLE-SRC-H}
        ${BITMAP-BUNDLE-SRC-CPP}
)

target_link_libraries(
        bitmap-bundle-lib
        jnigraphics
        ${log-lib}
)
