#!/bin/sh

~/bin/ImageMagick-6.7.2-8/convert.exe originals/$1.png -resize 1080x\> -resize x1440\>  $1.png
