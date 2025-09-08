#!/bin/bash

# Start MySQL in Docker container
docker run -d \
  --name mysql-auth \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=authenticacion_db \
  -e MYSQL_USER=authuser \
  -e MYSQL_PASSWORD=authpassword \
  -p 3306:3306 \
  mysql:8.0

echo "MySQL container started with:"
echo "Database: authenticacion_db"
echo "Username: authuser"
echo "Password: authpassword"
echo "Root password: rootpassword"
echo "Port: 3306"