# Main steps #

timelapse-darktable allows to treat a series of photo easily.
The main steps of the global processing are listed by order:

  1. load XMP from keyframe reference folder (e.g. shots {10 50 100}).
  1. interpolate keyframes XMP filters values for the whole series. (e.g. 10 11 12 ... 98 99 100})
  1. export JPG from interpolated XMP file and source image of the source image folder for the whole series
  1. compute the luminance curve of the series (external call: convert)
  1. filter the luminance curve for deflickering target (external call: octave)
  1. calibrate the luminance sensitivity of the scene (external call: convert)
  1. apply deflickering target through darktable exposure module and save it in XMP
  1. export the series in JPG with new XMP for the whole series
  1. produce timelapse movie from the JPG series (external call: ffmpeg)


# Details #