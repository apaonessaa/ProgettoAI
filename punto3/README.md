docker run -it -v ~/Desktop/ProgettoAI:/workspace/ProgettoAI -v ~/.ssh:/root/.ssh --network host --env ROS_MASTER_URI=http://localhost:11311 --name ros ros:melodic
