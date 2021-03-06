package org.dttimelapse.gui;

/**
Copyright 2014 Rudolf Martin
 
This file is part of DTTimelapse.

DTTimelapse is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

DTTimelapse is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with DTTimelapse.  If not, see <http://www.gnu.org/licenses/>.
*/

import javax.swing.JComponent;

// display slideshow of previews
// use new thread


public class SlideShow extends JComponent {
       
        private static final long serialVersionUID = 1L;

        private Thread internalThread;

        private volatile boolean noStopRequested;

       
        private MainGui mg;

       
       
        public SlideShow(MainGui gui) {

                mg = gui;
               

        }  // end constructor

       
       
       
        public void start() {
                noStopRequested = true;
               
                //System.out.println("start routine");
               
                Runnable r = new Runnable() {
                        public void run() {
                                try {
                                        runWork();
                                } catch (Exception x) {
                                        x.printStackTrace();
                                }
                        }
                };
               
                internalThread = new Thread(r, "SlideShow");
                internalThread.start();
        }


        private void runWork() {
               
                double framerate = mg.prefVideoFramerate;
                int time = (int) (1000/framerate);
               
                System.out.println("framerate = " + framerate);
               
                int startindex = mg.activeIndex;
                               
                while (noStopRequested) {
                       
                        for (int i = startindex; i <= mg.activeNumber; i++) {      
                                               
                                try {
                                        Thread.sleep(time); //
                                       
                                        //MainGui.activeIndex = i;
                                       
                                        // changing slider invokes new picture display
                                        mg.picSlider.setValue(i);
                                       
                                } catch (InterruptedException x) {
                                        Thread.currentThread().interrupt();
                                }
                               
                        }  // end for-loop
               
                        // start at begin until startindex
                        for (int i = 0; i <= startindex; i++) {
                               
                                try {
                                        Thread.sleep(time); //
                                       
                                        //MainGui.activeIndex = i;
                                       
                                        // changing slider invokes new picture display
                                        mg.picSlider.setValue(i);
                                       
                                } catch (InterruptedException x) {
                                        Thread.currentThread().interrupt();
                                }
                               
                        }  // end for-loop
                       
                       
                       
                       
                        // stop slideshow when last image reached
                        stopRequest();
                       
                }
        }

        public void stopRequest() {
                noStopRequested = false;
                internalThread.interrupt();
                mg.picSlider.setValue(mg.activeIndex);
               
                mg.playButton.setSelected(false);
               
        }

        public boolean isAlive() {
               
                if (internalThread == null) return false;
               
                return internalThread.isAlive();
        }


}

