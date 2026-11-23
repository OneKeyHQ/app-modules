cmake_minimum_required(VERSION 3.13)

set(PAGER_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/../../../..)
set(PAGER_GENERATED ${PAGER_ROOT}/android/build/generated/source/codegen/jni)
set(PAGER_COMMON ${PAGER_ROOT}/common/cpp)
file(GLOB PAGER_GENERATED_SOURCES CONFIGURE_DEPENDS
  ${PAGER_GENERATED}/*.cpp
  ${PAGER_GENERATED}/react/renderer/components/pagerview/*.cpp)

add_library(react_codegen_pagerview OBJECT
  ${PAGER_GENERATED_SOURCES}
  ${PAGER_COMMON}/react/renderer/components/pagerview/RNCNativeScrollerShadowNode.cpp)
target_include_directories(react_codegen_pagerview PUBLIC
  ${CMAKE_CURRENT_SOURCE_DIR}
  ${PAGER_COMMON}
  ${PAGER_GENERATED}
  ${PAGER_GENERATED}/react/renderer/components/pagerview)
target_link_libraries(react_codegen_pagerview fbjni jsi reactnative)
target_compile_reactnative_options(react_codegen_pagerview PRIVATE)
