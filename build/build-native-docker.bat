echo After docker starts type "./mvnw clean integration-test -Pnative -s build/settings.xml"
docker run -v %cd%:/project -it --entrypoint /bin/bash marksailes/al2-graalvm:11-22.0.0.2