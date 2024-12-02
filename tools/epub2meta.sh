HERE=`dirname "$(readlink -f "$0")"`

export CLASSPATH="${HERE}/java/saxon9he.jar:${HERE}/java"

java  net.efele.epub.Epub2Meta "$@"
