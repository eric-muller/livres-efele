HERE=`dirname "$(readlink -f "$0")"`
HERE=`cygpath -w "${HERE}"`

java -cp "${HERE}/java/saxon9he.jar;${HERE}/java"  net.efele.epub.SpaceHandler "$@"
