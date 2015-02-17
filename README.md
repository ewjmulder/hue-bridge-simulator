####Background####
- Erik: *"We'll expand our home automation system with Eneco Toon for heating control, including a cool touchscreen on the living room wall."*
- Erik's wife: *"Very nice, so can we operate the whole home from there as well?"*
- Erik: *"That would be super, but I guess you have to hack into the device to add your own custom interface and it seems no one has successfully done that yet."*
- Toon announcement: *"We'll soon be adding control of your Philips Hue lights to Toon."*
- Erik: *"Hmm, that will use local network communication with a known and documented protocol. This is my way in! :-)"*

#Hue Bridge Simulator#

##Goal of the project##
This project aims to **simulate** a **Philips Hue bridge** on a local network. The setup of the project is to support a
custom, programmatic implementation of the lights data and a hook for acting on the event of a light being turned on or off.
This way you can 'fool' an already existing Philips Hue graphical user interface into thinking it is turning lights
on and off, while actually you have **full programmatic control** over what lights 'exist' and how you would like
**to act on light switching**. So instead of just controlling your lights, you can hook up your entire home automation system!

This is the general principle of this project, but the implemented behavior subset
is specifically designed to work with the **[Dutch Eneco Toon](http://www.eneco.nl/toon)** system and has not been tested with other existing
Philips Hue interfacing apps or devices. Though it should be quite straightforward to extend this project to include
support for other interfaces.

This project is designed to work together with **[Program Your Home](https://github.com/ewjmulder/program-your-home)** (work in progress),
but you can also program your own desired functionality and customize the behavior to your needs.

*NB: This project is explicitly not meant to be a fully functioning bridge simulation that you can use to test
the working of your Philips Hue app against. It might be used as a basis for that though.*

##Usage##

###Getting started###

Prerequisites:
* Java 8 JDK installed and on path
* Maven 3 installed and on path (Maven 2 might also work)

1. Clone the project in the folder of your choice and go into the created `hue-bridge-simulator` folder
2. Copy the file `server/src/test/resources/simulator.properties.example` to a location of your choice, eg `/home/user/simulator.properties` or `C:/simulator.properties`
3. Edit the copy of the properties file and choose the right property values for your situation
4. In the folder 'hue-bridge-simulator' run `mvn clean install`
5. Go into the 'server' subfolder and run `mvn java:exec -Dsimulator.properties.location="/home/user/simulator.properties"` (of course use the path applicable for your situation)

###Eneco Toon###

This project was specifically designed and tested to work with the [Eneco Toon wall display - Philips Hue interface](https://www.eneco.nl/includes/eneco3/img/toonstatic/toon-hue/toon-device.png). Toon can connect to a Philips Hue bridge and let you control your lights on the touchscreen interface. This is a cool feature, but it would be even cooler to not just control your lights, but your whole home automation system from the Toon display!

##Technical information##

This project is programmed in Java 8 and uses Maven as a build tool.
