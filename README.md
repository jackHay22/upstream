![HAY](https://github.com/jackHay22/upstream/blob/master/resources/app/readme_title.png)

##
_Game made by Jack Hay using Clojure. Started in Dublin, Ireland in 2018._

## Getting started
[Getting started and technical documentation](doc/intro.md).

## App build info

### OSX build
- To build upstream.app: ``` ./build.sh ``` (requires ``` lein ```, ``` javapackager ```)
- If build script fails to install lein, install [here](https://leiningen.org/#install).

### Linux server build
- Remove ``` -Xdock:name=Upstream ``` from ``` :jvm-opts ``` in [project file](https://github.com/jackHay22/upstream/blob/38cd4494e082e59086f5ed9636aa0a4d1f11f7cd/project.clj#L8) and make sure [lein](https://leiningen.org/#install) is installed separately from build script. (optional: add ```"-Xmx1g" "-server"``` to ```:jvm-opts```)
- Run ``` ./build.sh -server ``` (requires ``` lein ```, ``` docker ```, ``` aws ``` cli tool (with ECR auth)).
  - This will build and push a new image to AWS ECR
- Optional: start a vagrant vm with ``` vagrant up ``` and then run ``` vagrant provision ``` to prep vm and pull ECR image. (login stage currently broken)
- Or just run the following:
```
eval $(aws ecr get-login --region us-east-2 --no-include-email)
docker pull 190175714341.dkr.ecr.us-west-2.amazonaws.com/upstream_server:latest
docker run -p 4000:4000 -p 4444:4444 upstream_server:latest
```
- Alternatively, run app in server mode: ```java -jar target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar -server```.
- To kill all running containers: ```docker kill $(docker ps -q)```.
- Web interface: ```localhost:4444```.

### Store build artifact
- Run ``` ./build -saveartifact ``` (requires ``` aws ``` cli and bucket permissions for ``` s3://upstream-build-archive ```) to upload standalone jar to a versioned s3 bucket.
- Note: will not build native app

### Windows build
- _Not tested_

## Server Operation
- Upstream (in server mode) will send logs to a Sumologic endpoint (in config.clj).

## TODO:
- [ ] Start-delay not updating correctly
- [ ] Fix bad load performance (may be an underlying clojure speed problem)
- [ ] Non-fading static screen images not rendering
- [ ] Fix tilemap not drawing superblocks as they scroll away (render optimization)
- [ ] General tilemap refactor (clean up hardcoded stuff)
- [ ] AWS lambda function for automated ecr cleanup
- [ ] Clear unused resources as system overhead optimization (i.e. paralax)
- [ ] Fix menu options
- [ ] Art, art, art
- [ ] Multicast server config (and setting up game to respond to server driven state updates)
- [ ] Music from nick
- [ ] Pause menu
- [ ] Layer driven tilemap (terrain, obstacles @ height, layers that render behind player and then in front of player)
- [ ] Dynamic grass, bushes, trees
- [ ] Water (boats)
- [ ] Enemy AI (big time)
- [ ] More art
- [ ] Use terraform for automated ecs deployments

### Completed
- [x] Fix conflict between load state and menu state using static screen
- [x] Rewrite fixbounds
- [x] Better reactive scaling
- [x] Prevent image loads in tilemap for ```-server``` mode.
- [x] Fix problems with gsmanager state changes from key presses
- [x] Async resource loads through calls to state/init
- [x] Add option to declare master images as ordered file set rather than single image for layer 2 (not all the same size, not necessarily 2:1)
- [x] Link tile layer movement
- [x] Better resource loading at boot (optimize game loads)

## Upstream Copyright © 2018 Jack Hay.
