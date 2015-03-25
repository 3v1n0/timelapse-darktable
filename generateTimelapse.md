# Main principle #

## Check dependancies ##

timelapse-darktable calls external executables.

Be sure that you have these dependencies installed

```
darktable-cli
octave
convert
mencoder
```

(type "which darktable-cli" in a terminal for instance to check if darktable-cli is installed) :


If it is not the case, install these packages.

For Ubuntu user, type in a terminal:
```
sudo apt-get install darktable octave imagemagick mencoder
```


## Download timelapse-darktable ##

### Option1 : Use executable JAR (easy way) ###

**Download the JAR file**

Download last executable JAR in the project trunk with the following svn command

```
svn checkout http://timelapse-darktable.googlecode.com/svn/trunk/jar timelapse-darktable-read-only
```

NB: if you don't have svn, install subversion package

```
sudo apt-get install subversion
```

**Execute timelapse-darkatble**

Right click on the JAR file > open with OpenSDK
and follow instructions below (Create your timelapse)


### Option2 : Use code in eclipse (developer) ###

skip this Option2 section if you are normal user ! Go to "Create your timelapse"

set up eclipse/java timelapse-darktable environment following wiki steps:

http://code.google.com/p/timelapse-darktable/wiki/configureEnvironment



## Create your timelapse ##

This is the interesting part and why you are looking at this project !

The main principle is to define some key frames, used as reference frames to generate the darktable configuration files (XMP).
Then timelapse-darktable interpolate the tuning parameters and generate the video while deflickering your serie.

**The first time: try with a short series by copying only for instance the first and the second shots of the series to test your global configuration. Remember: this project is still under development...**

### Step1: recommanded folder architecture ###

The architecture below is recommanded to ease the use of timelapse-darktable in your image serie folder:

**Create these folders**
  * ./imgSrc/ <- containing RAW or JPG images of the timelapse series
> and XMP files for keyframes at least (keyframes are starred pictures)
  * ./out <- output of timelapse-darktable are **all** put in this folder (folder "out" is automatically created if not existing)

Note: you can define it freely, and then enter the right path in the graphical interface, but in this page we suppose that we respect this folder architecture.

Starred pictures are keyframes of the timelapse (xmpRef)


### Step2: define key-frames ###

  * Mark keyframes with one star (at least) in darktable
  * in darktable modify keyframe pictures in ./imgSrc
  * respecting darktable supported filters & version: https://code.google.com/p/timelapse-darktable/wiki/SupportedFilters?ts=1389653790&updated=SupportedFilters
  * turn "on" **exposure filter** for all keyframes (at least with 0 tuning value) if you want use deflickering option
  * check that all keyframe pictures share the same filters history in darktable: (use "copy all"/"paste all" options in light table and then tune filters value if necessary)

You should have the same filters, but the interest is that you can adjust the tuning of these filters differently on each key frame.


### Step3: generate your timelapse video ###

**With a graphical user interface (normal user)**
  * Right click on the JAR file > open with OpenSDK
  * Fill inputs following instruction on the window
  * Click on "load keyframes"
  * Click on "generate timelapse"
  * go outside or take a cofee !


**CLI (if desired):**
Display help:
```
java -jar timelapse-darktable.jar --help
```

Typical command to generate your timelapse
  * linear interpolated timelapse (-t linear),
  * with deflickering (-d),
  * generating each JPG frame (-j),
  * and the final movie (-m)
  * source/raw image in imgSrc folder (-i imgSrc),
  * and generated outputs in outFolder (-o outFolder):

```
java -jar timelapse-darktable.jar -i imgSrc -o outFolder -t linear -d -j -m
```

**Notes for troubleshooting:**
  * Clean-up output folder if you don't know what you are doing...
You should clean up your output folder before executing the script (or define a name of a non-existing folder, which will be automatically created during execution).
if this step is not done, some file will be duplicated and you will get some unexpected results.


In eclipse (developper):
launch by configuring arguments tab in Run > Run configurations...


## Finally: enjoy your video ##

go to the output folder you have defined and watch your timelapse : ./out/video.avi

Note:
  * ./out/video.avi : your timelapse video
  * ./out/`*`.XMP : generated XMP (reusable in darktable)
  * ./out/`*`.JPG : darktable-cli exported frames
  * ./out/deflick is the intermediate folder used for deflickering
  * ./out/deflick/calib is a intermediate folder used for calibration for deflickering

**see some examples:**
  * http://www.youtube.com/watch?v=C-0bCAIJR0c&hd=1
  * http://timelapse-darktable.blogspot.fr/