# Intro
*Loop rider* is `libgdx` top-down racing game **prototype** which I was asked to code to show my programming skills while applying for gamedev position. Basically it is just a clone of popular [*Turn Right*](https://play.google.com/store/apps/details?id=air.tv.avix.turnright&hl=en) Android game. I spend ~20h, created basic gameplay prototype but unfortunately it was still not enough to get the job. 

This prototype requires physics fine-tuning and polishing to make a real game out of it, but I'm not going to do that, I'll just publish sources as-is here.

# Downloads
*"Loop rider"* `v0.4.0` binaries:
 * Desktop JAR - https://github.com/afomins/looprider/releases/download/v0.4.0/looprider.v0.4.0.jar
 * Android APK - https://github.com/afomins/looprider/releases/download/v0.4.0/looprider.v0.4.0.apk

# GIFs
Following animated GIFs show my how my **prototype** looks comparing to original *"Turn right"* game whose gameplay I was supposed to clone:

|  My *"Loop rider"* prototype gameplay | Original *"Turn right"* gameplay |
| --|--|
| <img src="https://github.com/afomins/looprider/blob/master/assets-raw/loop-rider-002.gif" width="300"> | <img src="https://github.com/afomins/looprider/blob/master/assets-raw/turn-right-000.gif" width="300"> |

# Implementation details
 * `libgx` was used as platform independent framework
 * `box2d` was used to simulate car physics and detect collisions. I used following `box2d` tutorial to implement top-down car physics -> http://www.iforce2d.net/b2dtut/top-down-car
 * Car and track are procedurally generated `box2d` bodies
 
# Build instructions (Desktop JAR)
 * Clone `git@github.com:afomins/looprider.git` and import *Gradle* project it into your favorite IDE
 * Allow *Gradle* to download dependent packages and update project files
 * Run `com.matalok.looprider.desktop.DesktopLauncher` class
 
