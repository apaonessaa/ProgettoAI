import os

from ament_index_python.packages import get_package_share_directory

from launch import LaunchDescription
from launch.actions import DeclareLaunchArgument, IncludeLaunchDescription
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch.substitutions import LaunchConfiguration
from launch_ros.actions import Node


def generate_launch_description():
    # Get the launch directory
    example_dir = get_package_share_directory('project_ai')
    namespace = LaunchConfiguration('namespace')

    declare_namespace_cmd = DeclareLaunchArgument(
        'namespace',
        default_value='',
        description='Namespace')

    plansys2_cmd = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(os.path.join(
            get_package_share_directory('plansys2_bringup'),
            'launch',
            'plansys2_bringup_launch_monolithic.py')),
        launch_arguments={
          'model_file': example_dir + '/pddl/domain_durative.pddl',
          'namespace': namespace
          }.items())

    # Specify the actions
    pickup_cmd = Node(
        package='project_ai',
        executable='pickup_action',
        name='pickup_action',
        namespace=namespace,
        output='screen',
        parameters=[])

    fill_cmd = Node(
        package='project_ai',
        executable='fill_action',
        name='fill_action',
        namespace=namespace,
        output='screen',
        parameters=[])

    move_cmd = Node(
        package='project_ai',
        executable='move_action',
        name='move_action',
        namespace=namespace,
        output='screen',
        parameters=[])

    deliver_cmd = Node(
        package='project_ai',
        executable='deliver_action',
        name='deliver_action',
        namespace=namespace,
        output='screen',
        parameters=[])
        
    empty_cmd = Node(
        package='project_ai',
        executable='empty_action',
        name='empty_action',
        namespace=namespace,
        output='screen',
        parameters=[])
    ld = LaunchDescription()

    ld.add_action(declare_namespace_cmd)

    # Declare the launch options
    ld.add_action(plansys2_cmd)

    ld.add_action(pickup_cmd)
    ld.add_action(fill_cmd)
    ld.add_action(move_cmd)
    ld.add_action(deliver_cmd)
    ld.add_action(empty_cmd)

    return ld
