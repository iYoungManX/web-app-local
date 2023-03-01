## steup.h

## install docker and add docker to the user group
sudo yum update -y
sudo amazon-linux-extras install docker
sudo service docker start
sudo usermod -a -G docker ec2-user
sudo chmod 666 /var/run/docker.sock
docker info

## install docker compose
sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose version

# shellcheck disable=SC2164

<<<<<<< HEAD
cd /opt && docker-compose up -d

## configure auto start
sudo chmod +x /opt/launch.sh
sudo cp /opt/launch.sh /var/lib/cloud/scripts/per-boot/launch.sh


