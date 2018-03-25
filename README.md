![HAY](https://github.com/jackHay22/upstream/blob/master/resources/app/readme_title.png)

## Info
_Game made by Jack Hay using Clojure. Started in Dublin, Ireland in 2018._

## Getting started
[Getting started and technical documentation](doc/intro.md).

## App build info

### OSX build
- To build upstream.app: ``` ./build.sh ``` (requires ``` lein ```, ``` javapackager ```)
- If build script fails to install lein, install [here](https://leiningen.org/#install).

### Linux server build
- Remove ``` -Xdock:name=Upstream ``` from ``` :jvm-opts ``` in project file and make sure [lein](https://leiningen.org/#install) is installed separately from build script. (optional: specify -server as jvm optimization arg)
- Run ``` ./build.sh -linuxserver ``` (requires ``` lein ```, ``` docker ```, ``` aws ``` cli tool (with ECR auth)).
  - This will build and push a new image to AWS ECR
- Then start the vagrant vm with ``` vagrant up ``` and then run ``` vagrant provision ``` to prep vm and pull ECR image. (Testing)
- Or just run the following (still may need x11 server running):

```
docker pull 190175714341.dkr.ecr.us-west-2.amazonaws.com/upstream_server:latest
docker run --rm \
           -e DISPLAY=unix$DISPLAY \
           -v /tmp/.X11-unix:/tmp/.X11-unix \
           upstream_server:latest
```
- Note: there are potentially other problems.

### Store build artifact
- Run ``` ./build -saveartifact ``` (requires ``` aws ``` cli and bucket permissions for ``` s3://upstream-build-archive ```) to upload standalone jar to a versioned s3 bucket.
- Note: will not build native app

### Windows build
- _Not tested_

## Docker
- Definitely broken
- Raspberry pi use: ``` FROM hypriot/rpi-java ```
- Note: there is currently a problem running app in container.  I am currently trying to use an X11 server for graphics.

## TODO:
- [x] Add option to declare master images as ordered file set rather than single image for layer 2 (not all the same size, not necessarily 2:1)
- [ ] Rework tiles/set-position
- [x] Link tile layer movement
- [ ] General tilemap refactor (clean up hardcoded stuff)
- [ ] Better resource loading at boot (optimize game loads)
- [ ] Fix resolution of menu options
- [x] Better reactive scaling
- [ ] Art, art, art
- [ ] Multicast server config (and setting up game to respond to server driven state updates)
- [ ] Figure out docker X11 server
- [ ] Music from nick
- [ ] Pause menu
- [ ] Layer driven tilemap (terrain, obstacles @ height, layers that render behind player and then in front of player)
- [ ] Dynamic grass, bushes, trees
- [ ] Water (boats)
- [ ] Enemy AI (big time)
- [ ] More art

## Upstream Copyright © 2018 Jack Hay.
