cmake_minimum_required(VERSION 3.16)
project(OneKeyQrScanner LANGUAGES C CXX)

set(BUILD_SHARED_LIBS OFF)
set(ZXING_READERS ON)
set(ZXING_WRITERS ON)
set(ZXING_EXPERIMENTAL_API OFF)
set(ZXING_C_API ON)
set(ZXING_USE_BUNDLED_ZINT ON)
add_subdirectory("${ZXING_CORE_DIR}" zxing)

target_sources(ZXing PRIVATE src/zxing_text.cpp)
