#Hue Bridge Simulator#

##Goal of the project##
This project aims to **simulate** a **Philips Hue bridge** on a local network. The setup of the project is to support a
custom, programmatic implementation of the lights data and a hook for acting on the event of a light being turned on or off.
This way you can 'fool' an already existing Philips Hue graphical user interface into thinking it is turning lights
on and off, while actually you have **full programmatic control** over what lights 'exist' and how you would like
to act on light switching. This is the general principle of this project, but the implemented behavior subset
is specifically designed to work with the **[Dutch Eneco Toon](http://www.eneco.nl/toon)** system and has not been tested with other existing
Philips Hue interfacing apps or devices. Though it should be quite straightforward to extend this project to include
support for other interfaces.

This project is designed to work together with [Program Your Home](https://github.com/ewjmulder/program-your-home) (work in progress),
but you can also program your own desired functionality and customize the behavior to your needs.

*NB: This project is explicitly not meant to be a fully functioning bridge simulation that you can use to test
the working of your Philips Hue app against. It might be used as a basis for that though.*

##Usage##

###Getting started###

TODO

###Eneco Toon###

TODO
