## steup.h
## make a directory for copy
mkdir /tmp/target

sudo yum update -y

sudo amazon-linux-extras install docker
sudo service docker start
#sudo usermod -a -G docker ec2-user
sudo chmod 666 /var/run/docker.sock

docker info
echo "I come here"
sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

docker-compose version




# shellcheck disable=SC2164
#cd /tmp && docker-compose up -d

sudo chmod +x /tmp/launch.sh
sudo cp /tmp/launch.sh /var/lib/cloud/scripts/per-boot/launch.sh
#sudo cp /tmp/launch.sh /var/lib/cloud/scripts/per-instance/launch.sh
