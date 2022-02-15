cmake_minimum_required(VERSION 2.8)

# give a name to the project
project(tp03)

# find opencv
find_package(OpenCV REQUIRED)

# check opencv version
if(${OpenCV_VERSION} VERSION_LESS 2.0.0)
	message(FATAL_ERROR “OpenCV version is not compatible : ${OpenCV_VERSION}”)
endif()

# compilation flags
set(CMAKE_CXX_FLAGS "-Wall -g -O2 -std=c++11")

# put the binary files in this directory
set(EXECUTABLE_OUTPUT_PATH ${PROJECT_BINARY_DIR}/bin)

# create a directory called 'output' in the project binary directory
file(MAKE_DIRECTORY ${PROJECT_BINARY_DIR}/output)

# or the automatic version
file(GLOB_RECURSE SRC_FILES *.cpp)

# for each sample file, make an exe
foreach(SRC_FILE ${SRC_FILES})
    get_filename_component(FILE ${SRC_FILE} NAME_WE)
    add_executable(${FILE} ${SRC_FILE})
    target_link_libraries(${FILE} ${OpenCV_LIBS})
endforeach()

