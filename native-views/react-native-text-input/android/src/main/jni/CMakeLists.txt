# Copyright (c) Meta Platforms, Inc. and affiliates.
#
# This source code is licensed under the MIT license found in the
# LICENSE file in the root directory of this source tree.

cmake_minimum_required(VERSION 3.13)
set(CMAKE_VERBOSE_MAKEFILE ON)

set(LIB_TARGET_NAME react_codegen_RNTextInputViewSpec)
set(LIB_ANDROID_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../..)
set(LIB_GENERATED_JNI_DIR ${LIB_ANDROID_DIR}/build/generated/source/codegen/jni)

file(GLOB LIB_CODEGEN_SRCS CONFIGURE_DEPENDS ${LIB_GENERATED_JNI_DIR}/*.cpp)

add_library(
  ${LIB_TARGET_NAME}
  SHARED
  ${LIB_CODEGEN_SRCS}
)

target_include_directories(
  ${LIB_TARGET_NAME}
  PUBLIC
  .
  ${LIB_GENERATED_JNI_DIR}
)

target_link_libraries(
  ${LIB_TARGET_NAME}
  fbjni
  jsi
  reactnative
)

target_compile_reactnative_options(${LIB_TARGET_NAME} PRIVATE)
