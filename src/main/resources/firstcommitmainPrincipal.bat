cd ..
cd ..
cd ..
cd Github
cd %1
git log origin/main%2 --pretty=oneline > C:\resources\salida-%1-branch-main-data.txt
For /F "UseBackQ Delims==" %%A In (C:\resources\salida-%1-branch-main-data.txt) Do Set "lastline=%%A"
Echo %lastline% > C:\resources\salida-%1-branch-main.txt
EXIT