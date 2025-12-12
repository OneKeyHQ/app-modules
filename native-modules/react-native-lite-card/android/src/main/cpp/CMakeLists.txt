cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -Os -Oz")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -Os -Oz")
set(CMAKE_C_VISIBILITY_PRESET hidden)
set(CMAKE_CXX_VISIBILITY_PRESET hidden)

# Support for Android 15 16KB page size
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wl,-z,max-page-size=16384")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wl,-z,max-page-size=16384")

add_library(keys SHARED
        ../../../../keys/keys.c
        validation.c)

# Include libraries needed for native-lib lib
target_link_libraries(keys
                      android
                      log)