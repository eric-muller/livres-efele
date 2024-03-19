for f in *.png; do
 magick.exe $f \
   -separate -evaluate-sequence Mean \
   -brightness-contrast 0x70 \
   -bordercolor white -border 1x1 \
   -fuzz 20% -trim +repage \
   -transparent white \
    ../$f
done
