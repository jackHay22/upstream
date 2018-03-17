# Upstream

## Info
- Game made by Jack Hay using Clojure. Started in Dublin, Ireland in 2018.
- For more information, read the docs

## App build info
- Install [lein](https://leiningen.org/#install)  (Note: build script will attempt to download with [homebrew](https://brew.sh/) if not installed).
- To build upstream.app: ``` ./build.sh ``` (requires ``` lein ```, ``` javapackager ```)
- For container server mode: ``` ./build.sh -server ``` (broken) (requires ``` lein ```, ``` docker ```)

## Docker
- Raspberry pi use: ``` FROM hypriot/rpi-java ```
- Note: there is currently a problem running app in container.  I am currently trying to use an X11 server for graphics.

## Resource Specifications
- Fullscreen resource resolution is 350x200 (all resources are scaled based on java max window calculation)

## TODO:
[] Better resource loading at boot
[] Tile engine
[] Better reactive scaling
[] Art
[] Multicast server config
[] Figure out docker X11 server

## Upstream Copyright © 2018 Jack Hay.
![HAY](https://github.com/jackHay22/upstream/blob/master/resources/app/company_logo.png)
