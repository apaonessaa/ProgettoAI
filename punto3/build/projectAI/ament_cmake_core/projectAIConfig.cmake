# generated from ament/cmake/core/templates/nameConfig.cmake.in

# prevent multiple inclusion
if(_projectAI_CONFIG_INCLUDED)
  # ensure to keep the found flag the same
  if(NOT DEFINED projectAI_FOUND)
    # explicitly set it to FALSE, otherwise CMake will set it to TRUE
    set(projectAI_FOUND FALSE)
  elseif(NOT projectAI_FOUND)
    # use separate condition to avoid uninitialized variable warning
    set(projectAI_FOUND FALSE)
  endif()
  return()
endif()
set(_projectAI_CONFIG_INCLUDED TRUE)

# output package information
if(NOT projectAI_FIND_QUIETLY)
  message(STATUS "Found projectAI: 0.0.0 (${projectAI_DIR})")
endif()

# warn when using a deprecated package
if(NOT "" STREQUAL "")
  set(_msg "Package 'projectAI' is deprecated")
  # append custom deprecation text if available
  if(NOT "" STREQUAL "TRUE")
    set(_msg "${_msg} ()")
  endif()
  # optionally quiet the deprecation message
  if(NOT ${projectAI_DEPRECATED_QUIET})
    message(DEPRECATION "${_msg}")
  endif()
endif()

# flag package as ament-based to distinguish it after being find_package()-ed
set(projectAI_FOUND_AMENT_PACKAGE TRUE)

# include all config extra files
set(_extras "ament_cmake_export_dependencies-extras.cmake")
foreach(_extra ${_extras})
  include("${projectAI_DIR}/${_extra}")
endforeach()
