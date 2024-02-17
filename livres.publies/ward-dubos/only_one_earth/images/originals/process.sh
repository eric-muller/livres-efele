for f in p*  ; do

  echo -- $f

 magick.exe $f \
   -separate -evaluate-sequence Mean \
   -brightness-contrast 0x30 \
   -bordercolor white -border 1x1 \
   -fuzz 20% -trim +repage \
   -transparent white \
   ../$f

done

for f in c*  ; do
  cp $f ../$f
done
