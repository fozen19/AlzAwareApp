#!/bin/bash

# PostgreSQL credentials
PG_USER="postgres"
PG_PASSWORD="1Catal:Kasik"
DB_NAME="alzaware_db"

echo "Creating AlzAware database..."

# Check if database exists
if psql -U $PG_USER -lqt | cut -d \| -f 1 | grep -qw $DB_NAME; then
    echo "Database $DB_NAME already exists."
else
    # Create database
    PGPASSWORD=$PG_PASSWORD createdb -U $PG_USER $DB_NAME
    echo "Database $DB_NAME created."
fi

echo "Database setup completed." 