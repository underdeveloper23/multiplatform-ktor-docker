#!/bin/bash

# Define default values for optional settings
max_connections=100
shared_buffers=128MB
work_mem=4MB
maintenance_work_mem=64MB
enable_ssl="n"
include_postgis="n"
include_pgcrypto="n"
include_timescaledb="n"
custom_settings=""

# Define usage function
usage() {
  echo "Usage: $0 -v <PostgreSQL version> [-p] [-c] [-t] [-s] [-n <max_connections>] [-b <shared_buffers>] [-w <work_mem>] [-m <maintenance_work_mem>] [-S] [-C <custom_settings>]"
  echo "Options:"
  echo "  -v <version>          PostgreSQL version (e.g., 13.4)"
  echo "  -p                    Include PostGIS extension (y/n)"
  echo "  -c                    Include pgcrypto extension (y/n)"
  echo "  -t                    Include TimescaleDB extension (y/n)"
  echo "  -s                    Enable SSL (y/n)"
  echo "  -n <max_connections>   Maximum connections (default: 100)"
  echo "  -b <shared_buffers>    Shared buffers size (default: 128MB)"
  echo "  -w <work_mem>         Work mem size (default: 4MB)"
  echo "  -m <maintenance_work_mem> Maintenance work mem size (default: 64MB)"
  echo "  -S                    Include custom settings (y/n)"
  echo "  -C <custom_settings>   Custom settings as a string (e.g., '-c custom_setting=value')"
  exit 1
}

# Parse command line arguments
while getopts "v:p:c:t:s:n:b:w:m:SC:" opt; do
  case $opt in
    v) postgres_version="$OPTARG";;
    p) include_postgis="y";;
    c) include_pgcrypto="y";;
    t) include_timescaledb="y";;
    s) enable_ssl="y";;
    n) max_connections="$OPTARG";;
    b) shared_buffers="$OPTARG";;
    w) work_mem="$OPTARG";;
    m) maintenance_work_mem="$OPTARG";;
    S) include_custom_settings="y";;
    C) custom_settings="$OPTARG";;
    \?) echo "Invalid option: -$OPTARG" >&2; usage;;
  esac
done

# Check for required arguments
if [ -z "$postgres_version" ]; then
  echo "Error: PostgreSQL version is required."
  usage
fi

# Start generating Docker Compose file
echo "version: '3.8'" > docker-compose.yml
echo "services:" >> docker-compose.yml
echo "  postgres:" >> docker-compose.yml
echo "    image: postgres:${postgres_version}" >> docker-compose.yml
echo "    environment:" >> docker-compose.yml
echo "      POSTGRES_DB: mydb" >> docker-compose.yml
echo "      POSTGRES_USER: myuser" >> docker-compose.yml
echo "      POSTGRES_PASSWORD: mypassword" >> docker-compose.yml

# Add extensions based on user input
if [ "$include_postgis" == "y" ]; then
  echo "    volumes:" >> docker-compose.yml
  echo "      - ./initdb-postgis:/docker-entrypoint-initdb.d" >> docker-compose.yml
fi

if [ "$include_pgcrypto" == "y" ]; then
  echo "    volumes:" >> docker-compose.yml
  echo "      - ./initdb-pgcrypto:/docker-entrypoint-initdb.d" >> docker-compose.yml
fi

if [ "$include_timescaledb" == "y" ]; then
  echo "    volumes:" >> docker-compose.yml
  echo "      - ./initdb-timescaledb:/docker-entrypoint-initdb.d" >> docker-compose.yml
fi

# Configure additional options
echo "    command: postgres -c max_connections=${max_connections} -c shared_buffers=${shared_buffers} -c work_mem=${work_mem} -c maintenance_work_mem=${maintenance_work_mem} ${custom_settings}" >> docker-compose.yml

if [ "$enable_ssl" == "y" ]; then
  echo "    volumes:" >> docker-compose.yml
  echo "      - ./ssl:/var/lib/postgresql/ssl" >> docker-compose.yml
  echo "    environment:" >> docker-compose.yml
  echo "      POSTGRES_SSL: on" >> docker-compose.yml
fi

echo "    ports:" >> docker-compose.yml
echo "      - 5432:5432" >> docker-compose.yml
echo "    networks:" >> docker-compose.yml
echo "      - mynetwork" >> docker-compose.yml
echo "    healthcheck:" >> docker-compose.yml
echo "      test: ["CMD", "pg_isready", "-h", "localhost", "-U", "myuser"]" >> docker-compose.yml
echo "networks:" >> docker-compose.yml
echo "  mynetwork:" >> docker-compose.yml

# Provide instructions
echo ""
echo "Docker Compose configuration generated in 'docker-compose.yml'."
echo "You can customize the configuration or run 'docker-compose up'."
