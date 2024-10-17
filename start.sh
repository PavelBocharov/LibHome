java -jar \
  -Dapp.data.content.file=/home/marolok/IdeaProjects/LibHome/content.json \
  -Dapp.data.path=/home/marolok/IdeaProjects/for_test/LibHomeTest_1/ \
  -Dapp.db.file=lib.db \
  -Dserver.port=8080 \
  -Dspring.profiles.active=production \
./target/lib-home*.jar