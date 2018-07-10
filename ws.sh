#!/bin/bash
# @author: heweiwu
# @desc: 查看指定日期commit log描述

#作者
AUTHOR=$1 
#开始日期 
S_DATE=$2	
#结束日期
E_DATE=$3 

if [ ! -n "$1" ] 
then
	AUTHOR='wuhewei'
fi

if [ ! -n "$2" ] 
then
	S_DATE='date -d last-thursday'
fi

if [ ! -n "$3" ] 
then
	E_DATE='date +%Y-%m-%d'
fi

	
git log --author="$AUTHOR" --no-merges --pretty=format:"%cd - %s" --date=format:%Y-%m-%d --since="$S_DATE" --before="$E_DATE"