cd ..
cd ..
cd ..
cd Github
cd %1
git log origin/master%2 --pretty=oneline > C:\resources\salida-%1-branch-master-data.txt
For /F "UseBackQ Delims==" %%A In (C:\resources\salida-%1-branch-master-data.txt) Do Set "lastline=%%A"
Echo %lastline% > C:\resources\salida-%1-branch-master.txt
EXIT