cmake_minimum_required(VERSION 3.13)
set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_CXX_STANDARD 20)

set(NITROGEN_PATH "../node_modules/react-native-nitro-modules/nitrogen/generated/android/")

add_library(
  {{cxxNamespace}}
  SHARED
  "src/main/cpp/cpp-adapter.cpp"
  "${NITROGEN_PATH}c++/NitroModules.cpp"
)

# Header Search Paths (includes)
target_include_directories(
  {{cxxNamespace}}
  PRIVATE
  "../nitrogen/generated/shared/c++"
  "${NITROGEN_PATH}c++"
)

# Link everything together
target_link_libraries(
  {{cxxNamespace}}
  android
  log
  react_nativemodule_core
  react_render_core
  reactnative
  turbomodulejsijni
)
