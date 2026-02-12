#!/bin/bash

BASE_PATH="/home/claude/service-booking-platform/src/main/java/com/servicebooking"

# Create directory structure
mkdir -p $BASE_PATH/entity
mkdir -p $BASE_PATH/dto/request
mkdir -p $BASE_PATH/dto/response
mkdir -p $BASE_PATH/repository
mkdir -p $BASE_PATH/service
mkdir -p $BASE_PATH/controller
mkdir -p $BASE_PATH/config
mkdir -p $BASE_PATH/security
mkdir -p $BASE_PATH/exception
mkdir -p $BASE_PATH/util
mkdir -p $BASE_PATH/enums

echo "Directory structure created successfully"
