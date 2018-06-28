# What is *"Loop rider"*
*Loop rider* is `libgdx` top-down racing **game prototype** which I was asked to code to prove my gamedev skills while applying for gamedev position. 
I spend ~20h, created basic gameplay prototype but unfortunately it was still not enough to get the job. 

This prototype requires driving physics fine-tuning and polishing, but I'm not going to do that, I'll just publish sources as-is here.

Basically *"Loop rider"* is just a clone of popular [*Turn Right*](https://play.google.com/store/apps/details?id=air.tv.avix.turnright&hl=en) Android game 

# How it looks
Following animated GIFs illustrate my how my **prototype** looks comparing to original *"Turn right"* game whose gameplay I was supposed to clone:

|  My *"Loop rider"* prototype gameplay | Original *"Turn right"* gameplay |
| --|--|
| <img src="https://github.com/afomins/looprider/blob/master/assets-raw/loop-rider-002.gif" width="300"> | <img src="https://github.com/afomins/looprider/blob/master/assets-raw/turn-right-000.gif" width="300"> |

# Implementation details

 * `libgx` was used as platform independent framework
 * `box2d` was used to simulate car physics and detect collisions. I used following `box2d` tutorial to implement top-down car physics -> http://www.iforce2d.net/b2dtut/top-down-car
 * Car and track are procedurally generated `box2d` bodies
 
# How to run Desktop
 
 * Clone `git@github.com:afomins/looprider.git` and import *Gradle* project it into your favorite IDE
 * Allow *Gradle* to download dependent packages and update project files
 * Run `com.matalok.looprider.desktop.DesktopLauncher` class
 
