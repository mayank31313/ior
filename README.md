## IOR Socket Server 

This is the IOT Stuff, This application is currently compactible with Python and Java Client Arduino and C++ Libraries has been deprecated and currently working on it.
The application for bridging devices and control them over internet. This can be used in land rover robots to 
  
#### Required Dependencies

1. Mongo DB
2. Java JDK >= 1.8
3. Maven

This application also uses Apache Zookeeper(but that's optional) to manage in the cluster (that's in alpha version), thus Zookeeper is disabled by default other optional dependencies are kafka and kuzzle iot.


#### Running Application
	
	export mongo.uri=mongodb+srv://full/mongo/uri
	mvn clean install
	#this will create a target folder, with a *.jar file in it
	java -jar target/*.jar 