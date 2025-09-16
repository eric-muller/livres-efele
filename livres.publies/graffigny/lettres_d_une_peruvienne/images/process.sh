for f in p*.png; do magick $f -separate -evaluate-sequence Mean -brightness-contrast 0x20 -bordercolor white -border 1x1 -fuzz 20% -trim +repage -transparent white v1-$f; done
