####Background####
- Erik: *"We'll expand our home automation system with Eneco Toon for heating control, including a cool touchscreen on the living room wall."*
- Erik's wife: *"Very nice, so can we operate the whole home from there as well?"*
- Erik: *"That would be super, but I guess you have to hack into the device to add your own custom interface and it seems no one has successfully done that yet."*
- Toon announcement: *"We'll soon be adding control of your Philips Hue lights to Toon."*
- Erik: *"Hmm, that will use local network communication with a known and documented protocol. This is my way in! :-)"*

#Hue Bridge Simulator#

##Goal of the project##
This project aims to **simulate** a **Philips Hue bridge** on a local network. The setup of the project is to support a custom, programmatic implementation of the lights data and a hook for acting on the event of a light being turned on or off. This way you can 'fool' an already existing Philips Hue graphical user interface into thinking it is turning lights on and off, while actually you have **full programmatic control** over what lights 'exist' and how you would like **to act on light switching**. So instead of just controlling your lights, you can hook it up to your entire home automation system!

This is the general principle of this project, but the implemented behavior subset is specifically designed to work with the **[Dutch Eneco Toon](http://www.eneco.nl/toon)** system and has not been tested with other existing Philips Hue interfacing apps or devices. Though it should be quite straightforward to extend this project to include support for other interfaces.

This project is designed to work together with **[Program Your Home](https://github.com/ewjmulder/program-your-home)** (work in progress), but you can also program your own desired functionality and customize the behavior to your needs.

*NB: This project is explicitly not meant to be a fully functioning bridge simulation that you can use to test the working of your Philips Hue app against. It might be used as a basis for that though.*

##Usage##

###Getting started###

Prerequisites:
* Java 8 JDK installed and on the path
* Maven 3 installed and on the path (Maven 2 might also work)

Basic installation steps:
1. Clone the project in the folder of your choice and go into the created `hue-bridge-simulator` folder
2. Copy the file `server/src/test/resources/simulator.properties.example` to a location of your choicSe, eg `/home/user/simulator.properties` or `C:/simulator.properties`
3. Edit the copy of the properties file and choose the right property values for your situation (see section Properties)
4. In the folder 'hue-bridge-simulator' run `mvn clean install`
5. Go into the 'server' subfolder and run `mvn java:exec -Dsimulator.properties.location="/home/user/simulator.properties"` (of course use the path applicable for your situation)

###Eneco Toon###

This project was specifically designed and tested to work with the [Eneco Toon wall display - Philips Hue interface](https://www.eneco.nl/includes/eneco3/img/toonstatic/toon-hue/toon-device.png). Toon can connect to a Philips Hue bridge and let you control your lights on the touchscreen interface. This is a cool feature, but it would be even cooler to not just control your lights, but your whole home automation system from the Toon display!

To be able to 'hook into' the interaction of Toon with a Philips Hue bridge we must simulate being a bridge ourselves, hence the project name hue-bridge-simulator. When you installed and started the software with the getting started steps outlined above (and are running on the same local network as the Toon is connected to), the simulator will announce itself on the local network (using UPnP). Toon will pick this up and allow you to connect to it. If you already connected Toon to a 'real' bridge, you have to disconnect first. Now, when you navigate in Toon to 'connect to bridge', you should be able to connect to the simulated bridge. When it's the only one on the network, it will connect automatically. When there is more then one bridge, you can select which bridge to connect to. You can recognize the simulated bridge by the name 'Philips Hue simulation' and the MAC address you configured. Note: it might take up to a few minutes for Toon to detect the simulated bridge. There is no need to press any physical button on the simulated bridge, let alone where you could even find a physical button on a piece of software. :)

If you selected the 'backend.mode.test=true', you will now see the 4 simulated 'lights' on the display of Toon. You can turn these lights on and off and by turning the 'Reverse' light on, the lights will be shown in reversed order. It may take up to 5 seconds for Toon to show the new state, since it will refresh it's light data every 5 seconds.

If you selected the 'backend.mode.rest=true' and successfully set up a running Program Your Home server (work in progress), you will be able to turn activities on and off with Toon. What the underlying action of an activity is, depends on your configuration. It might involve lights, media devices, locks or actually anything you have programmatic control over.

If you don't want to use the Program Your Home server, you can create your own REST backend that will be called to get the lights data and will be informed when a light has been turned on or off (see section REST server). Or you can go for an even more custom control by implementing the `com.programyourhome.huebridgesimulator.model.menu.Menu` interface with your won desired logic. Please also remember to extend the Spring wiring and configuration in such a way that the server will pick up your newly created menu implementation.

Enjoy controlling your smart home with the Toon tablet on the wall!

##Technical information##

This project is programmed in Java 8 and uses Maven as a build tool. It makes heavy use of the [Spring framework](http://spring.io/). [Spring Boot](http://projects.spring.io/spring-boot/) is used to start a web container with minimal custom configuration. Spring MVC, and more specifically the @RestController annotation is used to easily set up some URL's that will be serving data according to the Philips Hue bridge API. For the UPnP part of the simulation, the classes in the java.net package are used to broadcast messages over the local network. See the source code and Javadoc for more details.

###Properties###

The simulator.properties file needs to be edited to provide the right values for your situation. The available properties are listed in the example file, including a detailed description of how to choose the right value. This section will provide a general idea of why these properties exist and how they take effect.

Section 1 is about how the simulated bridge should be available on the network. The host (which can be either an IP address or a hostname) should match the machine where the software will be running on. It could be auto detected, but is provided as a property, to be able to differ between different available IP addresses, for example from a wired and wireless connection on the same machine. The port should be available on the machine. The MAC address is how the (simulated) bridge can be uniquely identified on your local network. It can be any value as long as it is different from the MAC address of any other existing Philips Hue bridge. For easier recognition and also for fun, you might like to use a [HEX speak](http://en.wikipedia.org/wiki/Hexspeak) value. See also this [HEX speak word list](https://reminiscential.wordpress.com/2008/09/03/hexspeak-word-list/).

Section 2 is about how the simulated bridge will get the lights data and who it should inform when a light has been switched. This functionality combined is called a 'Menu' and is represented by a class implementing the `com.programyourhome.huebridgesimulator.model.menu.Menu` interface. Currently, 2 Menu implementations exist: Test and REST. The Test menu is purely for testing if the simulated bridge is working correctly. The REST menu is meant to connect the simulated bridge to a REST backend. This could be the Program Your Home server, or any other server that supports the REST api for the lights menu. For the REST server, 3 more properties are available: host, port and base path.

Section 3 does not need any manual changes. It merely defines some derived properties that supply Spring boot with configuration for the host and port to run on. This way you only have to configure that once, and the same values will be used both by Spring Boot and the simulated bridge software.
