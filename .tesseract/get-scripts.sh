#!/bin/bash
readarray -t scripts < <(sed 's/\r$//' scripts.txt)
for i in "${scripts[@]}"
do
  echo "Downloading ${i}.traineddata"
  wget -qO ${i}.traineddata https://github.com/tesseract-ocr/tessdata_best/blob/main/script/${i}.traineddata?raw=true
done
echo "Done"