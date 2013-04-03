GPSdata
=======
Android app for GPS-based co-location experiments.

#Dev

Currently GPSdata supports Android 2.3.6 - 4.2.2 platforms. 

We try to collect satellite info from NMEA GPGSV sentences,
and location coordinates from NMEA GPRMC & GPGGA sentences.
Raw data are stored in a sqlite database 'gpsexp.sqlite', 
and exported to '/sdcard/'. 

Satellite info is stored in table TEST_SAT, and Location 
coordinates related info is in TEST_LOC. Entries are recorded
and triggered when listener found changes. The first entry is
reserved for StartTime, while the last entry is for EndTime.
Experiment data can be translated to a list in timeslots,
using aggregate SQL query.


#Usage

1. Install [GPSdata.apk](https://www.dropbox.com/s/snwcmm7tt0iq4pf/GPSdata.apk) to your Android device.
2. Type the experiment ID into the textbox.
2. Press 'Start' button with the help of the clock, and the 
status indicator shows 'WORKING'. 
3. Location and satellite info displays in two TextViews.
3. After 5 miniutes, the data collection task terminates, 
and the indicator shows 'DONE'.
4. Access 'gpsdata.sqlite' in '/sdcard/', and open it with Sqliteman
(Ubuntu), Sqlite Database Browser (Windows), or similar Apps.
