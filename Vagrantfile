# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|

  config.vm.box = "ubuntu/trusty64"

  config.vm.provision "shell", inline: <<-SHELL
    eval $(aws ecr get-login --region us-east-2 --no-include-email)
  SHELL
  config.vm.provision "docker" do |d|
    d.pull_images "190175714341.dkr.ecr.us-east-2.amazonaws.com/upstream_server"
    d.run 'upstream_server:latest', args: ''
  end
end
