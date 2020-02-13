#!/bin/sh

if [ "${BUILD_DB}" = true ]; then
    flask init-db;
fi