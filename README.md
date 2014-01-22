Spyfi: Mobile Surveillance Application
==============================

Mobile surveillance application for use with Foscam and Yanmix brand IP cameras.  May 
also work with other cameras as long as they use the same API as Foscam and Yanmix.


Building with Ant (Android)
-------------

There are two files that must edited.  The `local.properties` file found in both the root 
of the project as well as in the ActionBarSherlock library must be edited so the `sdk.dir` points 
to the actual install location of the Android SDK.

Once the two files have been edited you can build two different ways.

If you are on Linux or Mac and would like to do a debug build and install to simulator or device, you 
can use the included script and run from the Terminal:

    $ ./generate.sh

If you would just like to use Ant with the shipped Android tools you can always do:

    $ ant debug
    $ adb install bin/Spyfi-debug.apk
   

Version History
-------------

2.1.1 - July 10, 2013

* Passwords are no longer required
* Added Google Analytics


Have a question or found a bug (compliments work too)?
-------------

Email me via my website - [http://www.nraboy.com](http://www.nraboy.com/index/contact)

Tweet me on Twitter - [@nraboy](https://www.twitter.com/nraboy)


Resources
-------------

Google Play - [https://play.google.com/store/apps/details?id=com.nraboy.spyfi](https://play.google.com/store/apps/details?id=com.nraboy.spyfi)

Amazon App Store - [http://www.amazon.com/Nic-Raboy-Spyfi/dp/B0097UHJDA](http://www.amazon.com/Nic-Raboy-Spyfi/dp/B0097UHJDA)