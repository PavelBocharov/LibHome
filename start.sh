java -jar \
  -Dapp.data.content.file='C:/Users/Marolok/IdeaProjects/LibHome/content.json' \
  -Dapp.data.path='G:/Cache/4. Temp/DreamcastLib/' \
  -Dapp.db.file=dreamcast_lib.db \
  -Dserver.port=8080 \
  -Dspring.profiles.active=production \
./LibHomeUI/target/LibHomeUI-*.jar