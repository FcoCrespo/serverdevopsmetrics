cd ..
cd ..
cd ..
cd Github
cd eSalud
git log origin/main --pretty=oneline > C:\resources\salida-repo-branch-master-data.txt
For /F "UseBackQ Delims==" %%A In (C:\resources\salida-repo-branch-master-data.txt) Do Set "lastline=%%A"
Echo %lastline% > C:\resources\salida-repo-branch-master.txt
EXIT