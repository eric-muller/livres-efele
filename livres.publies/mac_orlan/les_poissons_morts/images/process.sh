for f in *.jp2 ; do  

   if [ "$f" == "021.jp2" ]; then
     exclude=(-fill white -draw "rectangle 0,0 429,543")
   elif [ "$f" == "025.jp2" ]; then
     exclude=(-fill white -draw "rectangle 0,0 387,730")
   else
     exclude=()
   fi   

   resize=()
   case "$f" in
         011.* | \
         013.* | \
         014.* | \
         018.* | \
         020.* | \
         024.* | \
         026.* | \
         027.* | \
         028.* | \
         034.* | \
         036.* | \
         041.* | \
         042.* | \
         047.* | \
         050.* | \
         059.* | \
         063.* | \
         064.* | \
         066.* | \
         068.* | \
         072.* | \
         077.* | \
         082.* | \
         085.* | \
         087.* | \
         090.* | \
         092.* | \
         095.* | \
         097.* | \
         099.* | \
         103.* | \
         105.* | \
         106.* | \
         110.* | \
         115.* | \
         116.* | \
         122.* | \
         124.* | \
         131.* | \
         135.* | \
         138.* | \
         140.* | \
         147.* | \
         152.* | \
         157.* | \
         159.* | \
         161.* | \
         164.* | \
         173.* | \
         178.* | \
         183.* | \
         188.* | \
         190.* | \
         202.* | \
         210.* | \
         213.* | \
         220.* | \
         224.* | \
         225.* | \
         230.* | \
         235.* | \
         237.* | \
         242.* | \
         244.*)
        resize=(-background none -gravity center -extent 2100x)
        ;;
        esac

  echo -- $f   

 magick.exe $f "${exclude[@]}" \
   -separate -evaluate-sequence Mean \
   -brightness-contrast 0x70 \
   -bordercolor white -border 1x1 \
   -fuzz 20% -trim +repage \
   "${resize[@]}" \
   -transparent white \
    `basename $f .jp2`.png

done
