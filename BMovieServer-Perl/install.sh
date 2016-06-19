#!/bin/sh
if [ $# -lt 1 ] ; then 
    echo "Please give the directory to install to"
    exit 
fi

base_dir=$1
mkdir $base_dir
chown www-data $base_dir
chmod 700 $base_dir

for dat in `ls *`; do
	if [ -f $dat ] ; then
	    echo $dat " wird erstellt"
	    cp $dat $base_dir/$dat
	    chown www-data $base_dir/$dat
	    chmod 400 $base_dir/$dat
	fi
done

chmod 500 $base_dir/*.pl

for dir in art data movies styles; do
    mkdir $base_dir/$dir
    chown www-data $base_dir/$dir
    chmod 700 $base_dir/$dir
    for dat in `ls $dir/*`; do
	if [ -f $dat ] ; then
	    echo $dat "wird angelegt" 
	    cp $dat $base_dir/$dat
	    chown www-data $base_dir/$dat
	    chmod 400 $base_dir/$dat
	fi
    done
done



