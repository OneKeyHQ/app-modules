cmake_minimum_required(VERSION 3.13)
project(background_thread)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_VERBOSE_MAKEFILE ON)

find_package(fbjni REQUIRED CONFIG)
find_package(ReactAndroid REQUIRED CONFIG)

add_library(${PROJECT_NAME} SHARED
    src/main/cpp/cpp-adapter.cpp
    ../cpp/SharedStore.cpp
    ../cpp/SharedRPC.cpp
)

target_include_directories(${PROJECT_NAME} PRIVATE
    src/main/cpp
    ../cpp
)

find_library(LOG_LIB log)

target_link_libraries(${PROJECT_NAME}
    ${LOG_LIB}
    fbjni::fbjni
    ReactAndroid::jsi
    ReactAndroid::reactnative
    android
)
