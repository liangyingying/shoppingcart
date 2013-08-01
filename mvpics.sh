#!/bin/bash
cd $1
##date >> ~/productIds.txt
#遍历图片源目录
for file in `ls $1`
do
#得到商品码的后两位,做为目标目录的directory name
dirindex=${file:(-2)}

formPath=$1"/"$file
#toPath = /var/www/images/Pic/product
toPath=$2
	#遍历图片源目录下的各个size目录，目前有四个，800x,350,160,140,分别代表图片各像素值
	for picsize in `ls $1"/"$file`
	do
	#800px
	if [ "${picsize:0:3}" == "800" ];then 
	echo $picsize;
	scp $1"/"$file"/"$picsize"/"*.jpg  root@202.85.213.112:$toPath"/b"$dirindex/
	#商品的多组图片，记录图片名到文件，用于product_gallery
	for picUrlDetail in `ls $1"/"$file"/"$picsize`
		do
		if [ "${picUrlDetail:(-3)}" == "jpg" ];then
			echo $picUrlDetail >> ./picUrlDetail.txt
		fi
		done
	fi
	
	#350px 
	if [ ${picsize:0:3} == "350" ];then 
	scp $1"/"$file"/"$picsize"/"*.jpg  root@202.85.213.112:$toPath"/m"$dirindex/
		
	fi 

	#160px
	if [ ${picsize:0:3} == "160" ];then 
	scp $1"/"$file"/"$picsize"/"*.jpg   root@202.85.213.112:$toPath"/s"$dirindex/
		
	fi

	#140px
	if [ ${picsize:0:3} == "140" ];then 
	scp $1"/"$file"/"$picsize"/"*.jpg   root@202.85.213.112:$toPath"/i"$dirindex/
		
	fi 
	done
#将对应的商品编号记录到文件
echo $file >> ./productIds.txt
done
