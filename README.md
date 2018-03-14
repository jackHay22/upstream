# Upstream

## Info
- Game made using clojure started in Dublin, Ireland in 2018.

## App build info
- Install [lein](https://leiningen.org/#install).
- Clone git repo
- To build upstream.app: ``` ./build.sh ```.
- For container server mode: ``` ./build.sh -server ``` (broken)

## Docker
- Raspberry pi use: ``` FROM hypriot/rpi-java ```
- Note: there is currently a problem running app in container.  I am currently trying to use an X11 server for graphics.

## Resource Specifications
- Fullscreen resource resolution is 350x200 (all resources are scaled based on java max window calculation)

## TODO:

![HAY](jackHay22.github.com/upstream/blob/master/resources/app/company_logo.png)
Upstream Copyright © 2018 Jack Hay.
