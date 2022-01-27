cmake_minimum_required(VERSION 3.10)

# give a name to the project
project(tp03)

# find opencv
find_package(OpenCV REQUIRED)

# compilation flags
set(CMAKE_CXX_FLAGS "-Wall -O2 -std=c++11")

# create a directory called 'output' in the project binary directory
file(MAKE_DIRECTORY ${PROJECT_BINARY_DIR}/output)

# or the automatic version
file(GLOB_RECURSE SRC_FILES *.cpp)

# for each sample file, make an exe
foreach(SRC_FILE ${SRC_FILES})
    get_filename_component(FILE ${SRC_FILE} NAME_WE)
    add_executable(${FILE} ${SRC_FILE})
    include_directories( ${OpenCV_INCLUDE_DIRS} )
    target_link_libraries(${FILE} ${OpenCV_LIBS})
endforeach()

