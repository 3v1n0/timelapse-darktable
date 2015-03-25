# Introduction #

This page details how to setup your environment to use timelapse-darktable

To follow the steps in video:
http://www.youtube.com/watch?v=cDSNwWh_vCg&feature=youtu.be&hd=1

NB: old video, the pinciple remains the same, but all 'lib' jars should be added in the building path.

# Details #

1. Open a terminal

2. Checkout last release of project :
```
svn checkout http://timelapse-darktable.googlecode.com/svn/trunk/ timelapse-darktable-read-only
```

3. Launch eclipse

if not installed :

```
sudo apt-get install eclipse
```

4. Create a new project

File > New > Java project

Customize options if you want

Name : timelapse-darktable for example

5. Import source code into created project

Right click on projects frame > Import

Then choose "File System"

Then "browse" to source repository "timelapse-darktable-read-only"

And "check" the folder box

Click "Finish"

6. Build path

Right click on projet > Build path > Configure build path

Click "Add JARs" in "Libraries" tab

Pick in your project folder
  * "./lib/commons-math3-3.2/commons-math3-3.2.jar"
  * "./lib/JSAP-2.1/lib/JSAP-2.1.jar"
  * "./lib/apache-pivot-2.0.3/lib/`*`.jar"
  * "./lib/svg/svgSalamander-tiny.jar"

To ignore svn files:
  * in "sources" tab, add the "`*`.svn`*`" pattern to the "excluded" list.


7. Use your project !

Run configuration (little arrow beside "play" big green arrow)

Choose main (if needed click on "+" icon on upper-left corner)

Arguments : fill with arguments

List of arguments provided by
```
--help.
```

Typical syntax:
  * for CLI
```
-x xmpRef -i imgSrc -o outFolder -t linear -d -j -m
```
  * for GUI
```
# no arguments needed
```