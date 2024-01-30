# extract the hyphenations from all the books 
# (at page boundaries, since that's the only place we have them)

function xslt () {   
  java -cp "${SAXON_JAR}" net.sf.saxon.Transform -s:$1 -xsl:$2;
}

for f in livres.publies/*/*/*.bml; do \
   xslt $f tools/hyphens.xsl; \
done \
| sort > ~/hyphens-efele.txt

