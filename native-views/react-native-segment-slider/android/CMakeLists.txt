project(segmentslider)
cmake_minimum_required(VERSION 3.9.0)

set(PACKAGE_NAME segmentslider)
set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_CXX_STANDARD 20)

# Define the C++ library and add the local adapter source.
add_library(${PACKAGE_NAME} SHARED src/main/cpp/cpp-adapter.cpp)

# Add the Nitrogen-generated specs + Nitro core linking. This resolves
# react-native-nitro-modules via find_package (monorepo-safe), instead of a
# hardcoded ../node_modules path that breaks once nitro-modules is hoisted.
include(${CMAKE_SOURCE_DIR}/../nitrogen/generated/android/segmentslider+autolinking.cmake)

# Local includes.
include_directories("src/main/cpp")

find_library(LOG_LIB log)

# Link everything together.
target_link_libraries(
        ${PACKAGE_NAME}
        ${LOG_LIB}
        android
)
