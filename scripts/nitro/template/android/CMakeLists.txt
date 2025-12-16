cmake_minimum_required(VERSION 3.9.0)

set(PACKAGE_NAME "{{cxxNamespace}}")
set(LIB_NAME "{{cxxNamespace}}")
set(LIB_LITERAL {{cxxNamespace}})

# Resolve react-native-nitro-modules package location
execute_process(
  COMMAND node --print "require('react-native-nitro-modules/package.json').main"
  WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
  OUTPUT_VARIABLE RNNITROMODULES_MAIN_FILE
  OUTPUT_STRIP_TRAILING_WHITESPACE
)
get_filename_component(RNNITROMODULES_ROOT_DIR "${RNNITROMODULES_MAIN_FILE}" DIRECTORY)
set(RNNITROMODULES_ANDROID_DIR "${RNNITROMODULES_ROOT_DIR}/cpp/android")

# Include Nitro Modules
include("${RNNITROMODULES_ANDROID_DIR}/CMakeLists.txt")

# Set up library
add_library(
  ${LIB_NAME}
  SHARED
  ${GENERATED_CXX_FILES}
  ${RNNITROMODULES_GENERATED_CXX_FILES}
  "../cpp/cpp-adapter.cpp"
)

target_include_directories(
  ${LIB_NAME}
  PRIVATE
  "../cpp"
  ${RNNITROMODULES_INCLUDES}
)

# Link libraries
find_package(fbjni REQUIRED CONFIG)
find_package(ReactAndroid REQUIRED CONFIG)

target_link_libraries(
  ${LIB_NAME}
  ${RNNITROMODULES_LIBS}
  ReactAndroid::jsi
  ReactAndroid::reactnativejni
  fbjni::fbjni
  android
  log
)
