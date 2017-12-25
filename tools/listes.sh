

echo "<html xmlns='http://www.w3.org/1999/xhtml'>" > liste.$1.html
echo "<body>"                                      >> liste.$1.html

echo "<p>Liste des réimpressions ÉFÉLÉ, "$1"</p>" >> liste.$1.html
echo "<ul>"                                      >> liste.$1.html

find . -name '*.'$2 | sed -e 's_.*results/\(.*\)_<li><a href="livres/\1">\1</a></li>_' >> liste.$1.html

echo "</ul>"                                       >> liste.$1.html
echo "</body>"                                     >> liste.$1.html
echo "</html>"                                     >> liste.$1.html
