
INSTALAÇÃO
============
mvn clean package
cd target
tar -xvf target/activemq-monitor-1.0-bin.tar.gz
chmod +x target/activemq-monitor-1.0/bin/*

START
============
target/activemq-monitor-1.0/bin/watchdog start

STOP
============
target/activemq-monitor-1.0/bin/watchdog stop
