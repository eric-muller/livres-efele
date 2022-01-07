for f in orig/*.png; do 
  magick.exe ${f} -separate -evaluate-sequence Mean \
      -brightness-contrast 0x5 -fuzz 20% -transparent white \
      `basename $f`
done

