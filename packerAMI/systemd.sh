#!/bin/bash
sudo chmod 777 /opt/deployment/cloudwatch-config.json
sudo groupadd -r appmgr
sudo useradd -r -s /bin/false -g appmgr jvmapps
sudo tee /etc/systemd/system/myapp.service > /dev/null <<EOT
[Unit]
Description=Manage Java service
[Service]
WorkingDirectory=/opt/deployment
ExecStart=/bin/bash -c 'source /etc/environment && /usr/bin/java -jar app.jar'
User=jvmapps
Type=simple
Restart=on-failure
RestartSec=10
[Install]
WantedBy=multi-user.target
EOT


#/usr/bin/sudo
sudo chown -R jvmapps:appmgr /opt/deployment
sudo systemctl daemon-reload
sudo systemctl start myapp.service
sudo systemctl enable myapp.service

