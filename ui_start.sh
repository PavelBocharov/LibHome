mvn -pl LibHomeUI -am clean install
java -jar \
  -Dapp.data.content.file='/home/marolok/IdeaProjects/LibHome/content.json' \
  -Dapp.data.path='/home/marolok/IdeaProjects/_temp/LIbHome/db/' \
  -Dapp.db.file=ib.db \
  -Dserver.port=8080 \
  -Dspring.profiles.active=production \
./LibHomeUI/target/LibHomeUI-*.jar