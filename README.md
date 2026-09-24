# Joystick demo

## Overview
This is a simple game to make use of the input from a Joystick connect to the 
computer via Bluetooth. But ant device can be used as long as the data from the 
Bluetooth device is what is expected. 

## Input
The game is controlled by getting this codes from a Bluetooth device:

|code|Action|
|----|------|
|1|Stick up, i.e will move the spaceship up|
|2|Stick down, i.e will move the spaceship down|
|4|Stick left, i.e will move the spaceship left|
|8|Stick Right, i.e will move the spaceship right|
|16|Fire button pressed, is used to start the game|
|42| A first message to tell it the game the device is connected and ready|
 
## The Game
It's a version of a game I played as kid, called s-mission or space mission. 
The player controls a spaceship located at the middle of bottom of the screen when 
it start. Then a bar appears with a hole in it and the player needs to get the spaceship 
through that bar, after a while it adds som monsters that is also to be avoided. The 
game ends when the spaceship hits eather the bar or one of the monsters. 