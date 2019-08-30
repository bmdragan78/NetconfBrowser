APP_HOME=`pwd`

cd ${APP_HOME}
START_CLASS=com.yangui.gui.App
CLASSPATH=".:${APP_HOME}/../lib/*:$APP_HOME/../config:$APP_HOME/../yangrepo/yang:$APP_HOME/../yangrepo/template"

echo "===================================================================="
echo "java -cp ${CLASSPATH} ${START_CLASS} production"
echo "===================================================================="
java -cp "${CLASSPATH}" ${START_CLASS} production
