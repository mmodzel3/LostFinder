# LostFinder
## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Used external services](#used-external-services)
* [Languages](#languages)
* [Setup](#setup)

# General info
This project is a mobile application for Android that can be used to help searching of missing people.
Participants of searching can see location of each other on map.
Map is powered by OpenStreetMap (osmdroid - https://github.com/osmdroid/osmdroid).
Current participant location is sent to managing server that stores all data. 
Alerts about important situations can be sent from application. 
It generates notifications to logged users using Firebase Cloud Messaging.
Marker with location is also visible on map.
During searching chat is available for communication.
What is more, actual weather data can be downloaded from OpenWeatherMap: https://openweathermap.org/ and can be seen on mobile device.
It is fetched based on current user location.
Application distinguishes three roles: User, Manager and Owner.
With appropriate permissions users' accounts can be managed from application.

Server code for managing searching of missing people can be found on:
https://github.com/mmodzel3/LostFinderServer

# Technologies
* Android
* Kotlin
* Gradle
* JUnit
* PowerMock
* Espresso

# Used external services
* OpenStreetMap
* OpenWeatherMap
* Firebase Cloud Messaging

# Languages
Application supports languages:
* English
* Polish

# Setup
Before compiling project Weather API key has to be added to project.
It can be generated on page: https://openweathermap.org/appid
File "secrets.xml" inside "app/src/main/res/values" directory has to be modified with appropriate Weather API key.

Additionally application server address has to be set in "app/src/main/java/com/github/mmodzel3/lostfinder/server/ServerEndpointFactory.kt". 
"SERVER_URL" has to be modified with correct address and port.

For notifications to work properly file "google-services.json" with FCM (Firebase Cloud Messaging) 
public authorization keys has to be added to "app" directory.
It can be generated on page: firebase.google.com/cloud/messaging
