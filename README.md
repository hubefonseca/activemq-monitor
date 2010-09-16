activemq-monitor
================

activemq-monitor is a simple software that does monitor an [Apache ActiveMQ](http://activemq.apache.org/) queue and if it stop to be consumed, send a notification email.

Configuring ActiveMQ
--------------------

You should enable JMX and JMX via network in ActiveMQ. Please edit your conf/activemq.xml file as below:

	<broker brokerName="localhost" useJmx="true">

	    <managementContext>
                <managementContext connectorPort="2011" jmxDomainName="org.apache.activemq"/>
            </managementContext>

	    ... 

	</broker>


Configuring Notifications
-------------------------

You need to configure the information of your email account, SMTP server and about ActiveMQ in these files:

-  src/main/resources/activemq-monitor.properties
-  src/main/resources/mail.properties


Installation
------------

We use [maven](http://maven.apache.org/) to do the process of compilation and packaging. If you are using a Debian-based GNU/Linux distribution, install with:

    aptitude install maven2

Having maven installed, you will be able to compile and package:

	mvn clean package
	cd target
	tar -xvf target/activemq-monitor-1.0-bin.tar.gz
	chmod +x activemq-monitor-1.0/bin/*

Note: it was tested on [Ubuntu 10.04]() using sun-java6 binaries (packages sun-java6-{bin,jre}).


Running
-------

To start the daemon, just run:

	./activemq-monitor-1.0/bin/watchdog start

To stop:

	./activemq-monitor-1.0/bin/watchdog stop

Enjoy!
