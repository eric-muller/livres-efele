mkdir -p scans_highres

for i in {1..491}; do \
   if [ ! -f v1_$i.jpg ] ; then
     wget -O scans_highres/v1_$i.jpg 'https://gallica.bnf.fr/iiif/ark:/12148/bpt6k10443224/f'$i'/full/full/0/native.jpg'
    fi
done

for i in {1..495}; do \
   if [ ! -f v2_$i.jpg ] ; then
     wget -O scans_highres/v2_$i.jpg 'https://gallica.bnf.fr/iiif/ark:/12148/bpt6k1044324z/f'$i'/full/full/0/native.jpg'
    fi
done
