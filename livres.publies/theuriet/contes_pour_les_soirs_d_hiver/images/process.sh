for f in [0-9]*.png ; do  

 magick.exe $f  \
   -separate -evaluate-sequence Mean \
   -brightness-contrast 0x20 \
   -bordercolor white -border 1x1 \
   -fuzz 20% -trim +repage \
   -transparent white \
    p$f

done
