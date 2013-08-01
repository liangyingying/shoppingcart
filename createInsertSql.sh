#!/bin/bash
for line in `cat ./picUrlDetail.txt`
do
	tmp=${line:5:2}
	id=${line:0:7}
	is_homepage=0
	if [`expr length $line` -eq 11 ];then
		is_homepage=1
	fi
	echo insert into product_gallery\(product_id,big_url,middle_url,small_url,mini_url,is_homepage,position\) values\($id,\'/product/b$tmp/$line\',\'/product/m$tmp/$line\',\'/product/s$tmp/$line\',\'/product/i$tmp/$line\',$is_homepage,$is_homepage\) >> insertProductGallery.sql
done
