#!/bin/bash
######################################################
if [ ! -f "todo.txt" ]; then 
	echo "can't find todo.txt, fail"
	exit 0
fi 
clear
sum=0
num=1
echo " * Starting download..."
echo; echo "------------------------------------------------------"; echo
######################################################
cat todo.txt | while read BOOK_ID
do
BASE_URL="http://archive.org/download/${BOOK_ID}"
sum=$(($sum + $num))

echo -n "$sum" > current.status.txt "of "
TOTAL=`cat todo.txt | wc -l` 
echo ${TOTAL} >> current.status.txt
echo "title: ${BOOK_ID}" >> current.status.txt

echo -n " [ `head -n1 current.status.txt` ]	Title: ${BOOK_ID}"; echo

if [ -d "${BOOK_ID}" ]; then
	echo "	- Existing data found, continuing previous download..."
	if [ -f "${BOOK_ID}/index.html" ]; then 
		rm ${BOOK_ID}/index.html 
	fi 
fi

wget -p -c -nc -nH -nd -erobots=off -P${BOOK_ID} ${BASE_URL}

#grep "${BOOK_ID}." ${BOOK_ID}/index.html | cut -d"<" -f2 | cut -d">" -f2 | grep "\." >> ${BOOK_ID}/xml_files_tmp
grep "${BOOK_ID}." ${BOOK_ID}/index.html | cut -d"<" -f4 | cut -d">" -f2 >> ${BOOK_ID}/xml_files_tmp

sed "s/  *$//;/^$/d" ${BOOK_ID}/xml_files_tmp > ${BOOK_ID}/xml_files_tmp2

cat ${BOOK_ID}/xml_files_tmp2 | sed s/^/http:\\/\\/archive.org\\/download\\/$BOOK_ID\\// > ${BOOK_ID}/download.urls

rm ${BOOK_ID}/index.html

rm ${BOOK_ID}/xml_files_tmp*

#---wget -p -c -A '.xml' -i ${BOOK_ID}/download.urls -nc -nH -nd -erobots=off -P${BOOK_ID} ${BASE_URL}
wget -p -c -i ${BOOK_ID}/download.urls -nc -nH -nd -erobots=off -P${BOOK_ID} ${BASE_URL}

rm ${BOOK_ID}/download.urls

echo " > Download of ${BOOK_ID} complete."
echo; echo "------------------------------------------------------"; echo
done
######################################################
rm current.status.txt
exit 0
