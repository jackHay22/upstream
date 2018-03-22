# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|

  config.vm.box = "ubuntu/trusty64"
  config.ssh.forward_agent = true
  config.ssh.forward_x11 = true

  config.vm.provision "shell", inline: <<-SHELL
     apt-get update
     sudo apt-get install xauth
     sudo apt-get install x11-apps
     # TODO: run: `aws ecr get-login --region us-east-2 --no-include-email` (and eval what is returned to login to pull)
  SHELL
  config.vm.provision "docker" do |d|
    d.pull_images "190175714341.dkr.ecr.us-east-2.amazonaws.com/upstream_server"
    #TODO: problem with docker run
    d.run 'upstream_server:latest', args: ' --rm -e DISPLAY=unix$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix'
  end
end
