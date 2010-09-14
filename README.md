
Premissas
============

ActiveMQ deve estar instalado e com JMX remoto habilitado:

	<broker brokerName="localhost" useJmx="true">

		<managementContext>
           	<managementContext connectorPort="2011" jmxDomainName="org.apache.activemq"/>
        	</managementContext>

		... 

	</broker>



Instalação
============
	
	mvn clean package
	cd target
	tar -xvf target/activemq-monitor-1.0-bin.tar.gz
	chmod +x target/activemq-monitor-1.0/bin/*

Start
============

	target/activemq-monitor-1.0/bin/watchdog start

Stop
============

	target/activemq-monitor-1.0/bin/watchdog stop
