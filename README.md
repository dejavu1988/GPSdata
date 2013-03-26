GPSdata
=======
Android app for GPS-based co-location experiments.

#Dev

Currently GPSdata supports Android 2.3.6 - 4.0.3 platforms. 

We try to collect satellite info from NMEA GPGSV sentences,
and location coordinates from NMEA GPRMC & GPGGA sentences.
Raw data are stored in a sqlite database 'gpsexp.sqlite', 
and exported to '/sdcard/'.

#Usage

1. Install gpsdata.apk to your Android device (2.3.6 - 4.0.3);
2. Once you start the app, location and satellite info presents
on the screen.
3. Press 'Start' button to start database deamon, and press 'End'
to terminate data storage.
4. Access 'gpsdata.sqlite' in '/sdcard/', and open it with Sqliteman
(Ubuntu) or Sqlite Database Browser (Windows).
