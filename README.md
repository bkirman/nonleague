Non-League Football Supporter
=========
The game is currently at alpha state, and works well on Android (4.0+)

Outstanding Tasks
------------------
- Iconography needs doing nicely
- Tutorial on first match for new players

Bugs
----
- See Issues system on Github

Compiling
---------
Set up your environment according to the LibGDX setup: https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29

Check out NLFS-GDX and import as Gradle project (there is one project per platform, at least do -desktop and -core (core is where the source lives)). Gradle *should* download all the appropriate dependencies. You should then be able to right click on the project "-desktop" and "Run As" Java Application. It will then ask which class to start with, select DesktopLauncher.

Android is a little more complex. You need to have the dependencies (e.g. Android SDK) set up according to the LibGDX guide. Then, make sure to set the path to the Android SDK in local.properties (sdk.dir=/home/blah) to compile the Android version (or disable it in the settings). We've been using Eclipse with ADT for Android, but your own gradle-supported IDE should work.


Licences:
==========

Typefaces:
----------
- Babas Neue; http://dharmatype.com/ ; SIL Open Font License: http://scripts.sil.org/cms/scripts/page.php?site_id=nrsi&id=OFL_web
- TGL 0-1451 Engschrift; Peter Wiegel; CC-BY; http://www.peter-wiegel.de/TGL_0-1451.html- 
- MPH 2B Damase; Mark Williamson; OFL

Images:
-------
- Football field; See-ming Lee, CC-BY; https://www.flickr.com/photos/seeminglee/8688505741
- Supporter with Beer; Russell James Smith, CC-BY; https://www.flickr.com/photos/russelljsmith/488207808/
- Icons; All [CC-BY](http://creativecommons.org/licenses/by/3.0/):
	- [Geekchamp](http://www.geekchamp.com/icon-explorer/introduction)
	- [Min Kim](http://www.endlessicons.com/terms-of-use/)
	- <a href="http://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a>
	- <a href="http://www.flaticon.com/authors/picol" title="Picol">Picol</a>
	- <a href="http://www.flaticon.com/authors/tutsplus" title="TutsPlus">TutsPlus</a> 
	- <a href="http://www.flaticon.com/authors/spovv" title="spovv">spovv</a>
	- <a href="http://www.flaticon.com/authors/icons8" title="Icons8">Icons8</a>
Sounds:
-------
- Click: Littlerobotsoundfactory, http://freesound.org/people/littlerobotsoundfactory/sounds/288909/
