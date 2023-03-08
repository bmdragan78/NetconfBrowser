#!/bin/sh
kill -9 `ps ax | grep com.yang.ui.App | grep java | awk '{print $1}'`