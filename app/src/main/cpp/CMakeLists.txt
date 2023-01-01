cmake_minimum_required(VERSION 3.22.1)

project("pic")

add_subdirectory(bitmap-bundle)

find_library(log-lib log)

target_link_libraries(
        bitmap-bundle-lib
        ${log-lib}
)
