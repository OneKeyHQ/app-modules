project(qrscanner)
cmake_minimum_required(VERSION 3.9.0)

set(PACKAGE_NAME qrscanner)
set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_CXX_STANDARD 20)

# The template pointed at ../node_modules/react-native-nitro-modules sources,
# which do not exist once nitro-modules is hoisted. Use Nitrogen's autolinking
# CMake like the other Nitro views (it finds NitroModules via find_package and
# compiles this package's generated specs).
add_library(${PACKAGE_NAME} SHARED src/main/cpp/cpp-adapter.cpp)
include(${CMAKE_SOURCE_DIR}/../nitrogen/generated/android/qrscanner+autolinking.cmake)

include_directories("src/main/cpp")

find_library(LOG_LIB log)

target_link_libraries(
        ${PACKAGE_NAME}
        ${LOG_LIB}
        android
)
