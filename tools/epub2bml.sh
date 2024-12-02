HERE=`dirname "$(readlink -f "$0")"`


java -cp "${HERE}/java/saxon9he.jar:${HERE}/java"  net.efele.epub.Epub2Bml "$1"
