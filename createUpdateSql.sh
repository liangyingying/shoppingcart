#!/bin/bash
for line in `cat ./productIds.txt`
do
	tmp=${line:(-2)}
	echo update product set homepage_image=\'/product/b$tmp/$line.jpg\',list_image=\'/product/m$tmp/$line.jpg\',small_image=\'/product/s$tmp/$line.jpg\',mini_image=\'/product/i$tmp/$line.jpg\' where id=$line >> ./updateProduct.sql
done
