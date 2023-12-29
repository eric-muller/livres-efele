for f in *.png ; do  

   if [[ "$f" == "h*" ]]; then
     contrast=0x30
   else
     contrast=0x80
   fi   

  echo -- $f   

 magick.exe $f \
   -separate -evaluate-sequence Mean \
   -brightness-contrast ${contrast} \
   -bordercolor white -border 1x1 \
   -fuzz 20% -trim +repage \
   -transparent white \
   ../$f

done
