cmake_minimum_required(VERSION 3.22.1)

project("tpbreakout")

add_library(
        tpbreakout
        SHARED
        native-lib.cpp
        qrcodegen.cpp)

include_directories(.)

find_library(
        log-lib
        log)

target_link_libraries(
        tpbreakout
        ${log-lib})