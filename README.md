![HAY](https://github.com/jackHay22/upstream/blob/master/resources/app/readme_title.png)

##
_Game made by Jack Hay using Clojure. Started in Dublin, Ireland in 2018._

## Getting started
[Getting started and technical documentation](doc/intro.md).

## App build info

### OSX build
- To build upstream.app: ```./build.sh``` (requires ```lein```, ```javapackager```)
- If build script fails to install lein, install [here](https://leiningen.org/#install).

### Linux server build
- (Not necessary) Remove ```-Xdock:name=Upstream``` from ```:jvm-opts``` in [project file](https://github.com/jackHay22/upstream/blob/38cd4494e082e59086f5ed9636aa0a4d1f11f7cd/project.clj#L8) and make sure [lein](https://leiningen.org/#install) is installed separately from build script. (optional: add ```"-Xmx1g" "-server"``` to ```:jvm-opts```)
- Run ``` ./build.sh -server ``` (requires ```lein```, ```docker```).
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
        --mount source=server_trace_volume,target=/gp_volume \
        upstream_server:latest #change if pulled from ecr
```
- Make sure to change the server mode in ```/docker/run.list```.  This can either be ```-gp``` or ```-server```.
- To kill all running containers: ```docker kill $(docker ps -q)``` (free up ports).
- Alternatively, run app in server mode: ```java -jar target/uberjar/upstream-*.*.*-SNAPSHOT-standalone.jar -server```.
- Vagrant vm: ```vagrant up``` and then run ```vagrant provision``` to prep vm and pull ECR image. (login stage currently broken)

### Store build artifact
- Run ```./build -saveartifact``` (requires ```aws``` cli and bucket permissions for ```s3://upstream-build-archive```) to upload standalone jar to a versioned s3 bucket.
- Note: will not build native app
- Run  ```./build -backup``` to upload entire repo to deep Glacier cold storage (will archive entire contents of project)

### Windows build
- _Not tested_

## Server Operation (via docker)
- Upstream (in server mode) will send logs to a Sumologic endpoint (in config.clj).
- Upstream containers also provide a web interface with various metrics ```localhost:4444``` (or whatever port is configured in ```docker/run.list```)
  - Note: this is broken

## UpstreamGP server Operation
- Run UpstreamGP with the argument ```-gp```.
- Follow input formatting convention found in [documentation](doc/intro.md).

## Map Editor Operation
- Run ```./build.sh -editor``` to build and run the editing tool.
- Note ```./buildEditor``` should only be run as a subroutine of ```build.sh``` to verify that resources are correctly copied to the editor's working directory.
- This tool uses ```ant``` to orchestrate the java build and will attempt to use brew to download this tool if it can't find it in ```$PATH```.

## TODO:
- [ ] Try additional light/depth techniques
- [ ] Fix object-blocks-visible? fn
- [ ] Redesign gp evaluation reduce to be list based
- [ ] Redesign level 1 init file problem for docker image
- [ ] Something wrong with opacity function
- [ ] Tilemap blocked intersections
- [ ] Don't need two sound layers: make layer two a sound layer and update layer 1 accordingly
- [ ] Don't always recompute spacial range (chunk size is constant)
- [ ] Make entity draw handler efficient
- [ ] Edge testing
- [ ] Bundle tilemap config and add function for updating
- [ ] Add server support to all updates made
- [ ] Height attribute for l1 layer blocks (and draw player at updated height)
- [ ] Pause menu in level1
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
_See [documentation](doc/intro.md) for completed list_

## Planned Iteration Schedule
- [ ] _0.2.0_ Minimum playable environment (Mid May 2018)
  - Acceptance Standards:
    - [x] Working tilemap system for 2 layers
    - [ ] Collision system
    - [x] Stand-in player movement images
    - [ ] Initial pass at game art
    - [x] Viable art development pipeline
- [ ] _0.3.0_ Stable production environment (Mid June 2018)
  - Acceptance Standards:
    - [ ] _Useful_ map editing toolset and build orchestration
    - [ ] Better game environment production systems
- [ ] _0.4.0_ Minimum playable game (Mid July 2018)
  - Acceptance Standards:
    - [ ] Player idle and movement animations
    - [ ] Full map design and initial art pass on entire map
    - [ ] Running and fighting player animations
    - [ ] First pass on hostile enemy system
    - [ ] Polished art in key locations
    - [ ] Dynamic undergrowth system @ layer 3
- [ ] _0.5.0_ Testing release (Early August 2018)
  - Acceptance Standards:
    - [ ] Distribute game to initial group for local gameplay testing
    - [ ] Working website with download functionality (testing for macOS gatekeeper)
- [ ] _0.6.0_ Genetic Programming Release (End of Summer 2018)
  - Acceptance Standards:
    - [ ] Working distributed evaluation system
    - [ ] Running UpstreamGP image on cluster
    - [ ] Basic decision instruction generation
    - [ ] Optimized UpstreamGP individual evaluation
    - [ ] Testing GP individuals with human-coded individuals
- [ ] _0.7.0_ Testing release 2 (Early Fall 2018)
  - Acceptance Standards:
    - [ ] Resolve inevitable bugs
    - [ ] Iterate on feedback
    - [ ] Re-release with changes made
- [ ] _0.8.0_ Minimum viable server environment (End of Summer 2018)
  - Acceptance Standards:
    - [ ] Deployment to raspberry pi cluster through docker
    - [ ] Monitoring orchestration
    - [ ] Redis background server
    - [ ] Multicast server
    - [ ] Basic user authentication mechanism
- [ ] _0.9.0_ Production server environment (Mid Fall 2018)
  - Acceptance Standards:
    - [ ] Terraform deployment automation (or cloudformation)
    - [ ] Full AWS server environment
- [ ] _1.0.0_ Full Release (End 2018)
  - Acceptance Standards:
    - [ ] It's what it sounds like
    - [ ] Website
    - [ ] Full release available on website
    - [ ] BTS videos?
- [ ] _1.0.1_ System optimization of full release (End 2018)
  - Acceptance Standards:
    - [ ] Live website distribution testing
    - [ ] System architecture testing
    - [ ] Resource and load optimizations
- [ ] _*.0.0_ LTS (Through Summer 2019)
  - Acceptance Standards:
    - [ ] Iterate on feedback with well-tested, stable releases
    - [ ] Release Deployment pipeline

### Upstream Copyright © 2018 Jack Hay.
