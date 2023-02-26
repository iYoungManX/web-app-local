#!/bin/bash

sudo groupadd -r appmgr
sudo useradd -r -s /bin/false -g appmgr jvmapps
sudo tee /etc/systemd/system/myapp.service > /dev/null <<EOT
[Unit]
Description=Manage Java service

[Service]
Environment=MY_VAR=$EXISTING_VAR
WorkingDirectory=/opt/deployment
ExecStart=/bin/java -Xms128m -Xmx256m -jar app.jar
User=jvmapps
Type=simple
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOT

sudo chown -R jvmapps:appmgr /opt/deployment

sudo systemctl daemon-reload
sudo systemctl start myapp
sudo systemctl enable myapp