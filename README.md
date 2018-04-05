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
- (Not necessary) Remove ``` -Xdock:name=Upstream ``` from ``` :jvm-opts ``` in [project file](https://github.com/jackHay22/upstream/blob/38cd4494e082e59086f5ed9636aa0a4d1f11f7cd/project.clj#L8) and make sure [lein](https://leiningen.org/#install) is installed separately from build script. (optional: add ```"-Xmx1g" "-server"``` to ```:jvm-opts```)
- Run ``` ./build.sh -server ``` (requires ``` lein ```, ``` docker ```).
  - (macOS: the build script is able to start the docker daemon on its own)
- Additional build options:
  - ```-push``` This will tag push the new docker image to AWS ECR. (Run: ```./build -server -push```) (Requires: ``` aws ``` cli tool (with ECR auth))
  - ```-run``` This will run the new docker container locally without pushing it to ECR. (Run: ```./build -server -run```)
  - Note: ```./build -server``` will just build the new docker image.
- Running the docker container manually:
```bash
eval $(aws ecr get-login --region us-east-2 --no-include-email) #if image stored in ecr
docker pull 190175714341.dkr.ecr.us-west-2.amazonaws.com/upstream_server:latest #if image stored in ecr
docker run \
        -p 4000:4000 \
        -p 4444:4444 \
        --env-file ./docker/run.list \
        upstream_server:latest #change if pulled from ecr
```
- To kill all running containers: ```docker kill $(docker ps -q)``` (free up ports).
- Alternatively, run app in server mode: ```java -jar target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar -server```.
- Vagrant vm: ``` vagrant up ``` and then run ``` vagrant provision ``` to prep vm and pull ECR image. (login stage currently broken)

### Store build artifact
- Run ``` ./build -saveartifact ``` (requires ``` aws ``` cli and bucket permissions for ``` s3://upstream-build-archive ```) to upload standalone jar to a versioned s3 bucket.
- Note: will not build native app

### Windows build
- _Not tested_

## Server Operation (via docker)
- Upstream (in server mode) will send logs to a Sumologic endpoint (in config.clj).
- Upstream containers also provide a web interface with various metrics ```localhost:4444``` (or whatever port is configured in ```docker/run.list```)
  - Note: this is broken

## TODO:
- [ ] Bundle tilemap config and add function for updating
- [ ] Entity manager code
- [ ] Entity decision formatting
- [ ] Keyboard input control
- [ ] Fix broken load-from-save
- [ ] Load safeguards (infrastructure around save functionality)
- [ ] Calculate base layer tiles down at config startup sequence to clean up tilemap layer init fn
- [ ] Height attribute for l1 layer blocks (and draw player at updated height)
- [ ] Tilemap intersections
- [ ] Refactor temporary fixes to menu and load screens, improve code and reduce atomics
- [ ] Fix menu options resolution and scrolling problem with up arrow
- [ ] Rotoscoping for walk cycle
- [ ] Art, art, art
- [ ] Multicast server config (and setting up game to respond to server driven state updates)
- [ ] Music from nick
- [ ] Pause menu
- [ ] Dynamic grass, bushes, trees @ l2
- [ ] Water (boats?)
- [ ] Enemy AI (big time)
- [ ] Proximity-based sound
- [ ] More art
- [ ] Clear unused resources as system overhead optimization (i.e. paralax)? (Allow garbage collection)
- [ ] Fix web interface server
- [ ] Reduce docker image size
- [ ] AWS lambda function for automated ecr cleanup
- [ ] Use terraform for automated ecs deployments

### Completed
- [x] Player shadow
- [x] Start-delay not updating correctly (related to static screen)
- [x] Explore defrecord for interfaces.
- [x] Non-fading static screen images not rendering
- [x] Refactor tilemap loader to match entity-loader (potentially change format definition)
- [x] Fix entity draw handler
- [x] Draw superblocks based on their relative height (so they match with their relative location)
- [x] Fix tilemap not drawing superblocks as they scroll away (render optimization)
- [x] Fix conflict between load state and menu state using static screen
- [x] Rewrite fixbounds
- [x] Better reactive scaling
- [x] Prevent image loads in tilemap for ```-server``` mode.
- [x] Fix problems with gsmanager state changes from key presses
- [x] Async resource loads through calls to state/init
- [x] Add option to declare master images as ordered file set rather than single image for layer 2 (not all the same size, not necessarily 2:1)
- [x] Link tile layer movement
- [x] Better resource loading at boot (optimize game loads)

## Planned Iteration Schedule
- [ ] _0.2.0_ Minimum playable environment (Late April 2018)
  - Acceptance Standards:
    - [ ] Working tilemap system for 2 layers, collision system
    - [ ] Player idle and movement animations
    - [ ] Initial pass at game art
    - [ ] Viable art development pipeline
- [ ] _0.3.0_ Minimum playable game (Mid July 2018)
  - Acceptance Standards:
    - [ ] Full map design and initial art pass on entire map
    - [ ] Running and fighting player animations
    - [ ] First pass on hostile enemy system
    - [ ] Polished art in key locations
    - [ ] Dynamic undergrowth system @ layer 3
- [ ] _0.4.0_ Testing release (End of Summer 2018)
  - Acceptance Standards:
    - [ ] Distribute game to initial group for local gameplay testing
    - [ ] Working website with download functionality (testing for macOS gatekeeper)
- [ ] _0.5.0_ Minimum viable server environment (End of Summer 2018)
  - Acceptance Standards:
    - [ ] Deployment to raspberry pi cluster through docker
    - [ ] Monitoring orchestration
    - [ ] Redis background server
    - [ ] Multicast server
    - [ ] Basic user authentication mechanism
- [ ] _0.6.0_ Production server environment (Mid Fall 2018)
  - Acceptance Standards:
    - [ ] Terraform deployment automation (or cloudformation)
    - [ ] Full AWS server environment
- [ ] _1.0.0_ Full Release (End 2018)
  - Acceptance Standards:
    - [ ] It's what it sounds like
    - [ ] Full release available on website
    - [ ] BTS videos?
- [ ] _*.0.0_ LTS (Through Summer 2019)

## Upstream Copyright © 2018 Jack Hay.
