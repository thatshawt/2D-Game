#echo "Compressing artifacts into one file"
#zip -r artifacts.zip artifacts_folder

#export GITHUB_TOKEN=$$$$$$$$$$$$
#export GITHUB_API=https://git.{your domain}.com/api/v3 # needed only for enterprise
export GITHUB_ORGANIZATION=thatshawt
export GITHUB_REPO=2D-Game
export VERSION_NAME=${BUILD_NUMBER}
#export PROJECT_NAME=GameServer

echo "Deleting release from github before creating new one"
linux-amd64-github-release delete --user ${GITHUB_ORGANIZATION} --repo ${GITHUB_REPO} --tag "${VERSION_NAME}"

echo "Creating a new release in github"
linux-amd64-github-release release --user ${GITHUB_ORGANIZATION} --repo ${GITHUB_REPO} --tag "${VERSION_NAME}" --name "${VERSION_NAME}"
sleep 10 # sleep to make sure that github updates and all...

echo "Uploading the artifacts into github"
#files=( ./GameServer/target/GameServer-1.0-SNAPSHOT-spring-boot.jar ) # get the jar file in target folder
echo "Uploading GameServer..."
linux-amd64-github-release upload --user ${GITHUB_ORGANIZATION} --repo ${GITHUB_REPO} --tag ${VERSION_NAME} \
 --name "GameServer-${VERSION_NAME}.jar" --file "./GameServer/target/GameServer-1.0-SNAPSHOT-spring-boot.jar"
echo "Uploading GameClient..."
linux-amd64-github-release upload --user ${GITHUB_ORGANIZATION} --repo ${GITHUB_REPO} --tag ${VERSION_NAME} \
 --name "GameClient-${VERSION_NAME}.jar" --file "./GameClient/target/GameClient-1.0-SNAPSHOT-spring-boot.jar"

echo "Finished uploading!"
#  --name "${PROJECT_NAME}-${VERSION_NAME}.jar" --file "${files[0]}"


