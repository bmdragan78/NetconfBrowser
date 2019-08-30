#!/bin/sh
kill -9 `ps ax | grep com.yangui.gui.App | grep java | awk '{print $1}'`