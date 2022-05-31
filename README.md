# Smishdet
Smishdet is a mobile app aimed at detecting malicious links in text mesages. The user selects the text messages he/she wants to be inspected for malicious links and sends to smishhdet backend which then sends a response containing the results for each link. 

In the backend, smishdet uses the Lookup API of google safe to inspect each url. 

To setup your own copy of the app and customize, use the following

## Packages required on backend server

•	Python

•	Hypercorn or Uvicorn


## Configuring the smishdet app
You need Android Studio in order to configure the front end of the app. The app has been created in Java. You can clone and download the repository and then open the app using android studio.


## Setting up backend server

On the server of your choice install Python and Hypercorn and copy the file main.py from the smishdet repository. 

Run the main app (in main.py) file using hypercorn.

`hypercorn main:app`

The above command runs the on the loopback IP. To globally access it, run as below

`hypercorn main:app --bind 0.0.0.0:80`

to access the API, use the following

1) Run on browser using the serverIP

`<Server IP:80/docs`

Your backend server is now setup.

Extract the APK of the app and install on any android mobile device.


