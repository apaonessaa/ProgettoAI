cmake_minimum_required(VERSION 3.5)
project(project_ai)

# find dependencies
find_package(ament_cmake REQUIRED)
find_package(rclcpp REQUIRED)
find_package(rclcpp_action REQUIRED)
find_package(plansys2_executor REQUIRED)
find_package(plansys2_msgs REQUIRED)

set(CMAKE_CXX_STANDARD 17)

set(dependencies
    rclcpp
    rclcpp_action
    plansys2_msgs
    plansys2_executor
)

add_executable(pickup_action src/pickup_action.cpp)
ament_target_dependencies(pickup_action ${dependencies})

add_executable(fill_action src/fill_action.cpp)
ament_target_dependencies(fill_action ${dependencies})

add_executable(move_action src/move_action.cpp)
ament_target_dependencies(move_action ${dependencies})

add_executable(deliver_action src/deliver_action.cpp)
ament_target_dependencies(deliver_action ${dependencies})

add_executable(empty_action src/empty_action.cpp)
ament_target_dependencies(empty_action ${dependencies})

install(DIRECTORY launch pddl DESTINATION share/${PROJECT_NAME})

install(TARGETS
  pickup_action
  fill_action
  move_action
  deliver_action
  empty_action
  ARCHIVE DESTINATION lib
  LIBRARY DESTINATION lib
  RUNTIME DESTINATION lib/${PROJECT_NAME}
)

if(BUILD_TESTING)
  find_package(ament_lint_auto REQUIRED)
  ament_lint_auto_find_test_dependencies()

  find_package(ament_cmake_gtest REQUIRED)
endif()


ament_export_dependencies(${dependencies})
ament_package()
