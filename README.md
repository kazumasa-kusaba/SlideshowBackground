# SlideshowBackground
An Android library for running slideshow in the background.  
<br>
![slideshowbackground_screenshot_00](https://user-images.githubusercontent.com/17498982/125302862-4f822d00-e367-11eb-8d3d-0653b23b223e.gif)
<br>
Try running the sample program under the app folder.  

# Features
* can play any pre-added image
* can play left and right at any speed you like
* can pause and stop
* random playback is possible

# Usage

## 1. Write in Gradle
```gradle
implementation 'io.github.kazumasa-kusaba:SlideshowBackground:1.0'
```

## 2. Write in XML Layout
```xml
<xyz.kusaba.slideshowbackground.SlideshowBackground
    android:id="@+id/slideshowBackground"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

## 3. Set up and play slideshow
### How to play slide show
```java
// retrieve the widget
SlideshowBackground slideshowBackground = this.findViewById(R.id.slideshowBackground);

// append images to slide show list
//  · images close to the size of the smartphone screen is recommended.
//  · extremely large image size is not recommended.
slideshowBackground.append(this.getResources(), R.drawable.sample_picture_00);
slideshowBackground.append(this.getResources(), R.drawable.sample_picture_01);
slideshowBackground.append(this.getResources(), R.drawable.sample_picture_02);

// set speed in pixels
//  · set how far the images move at 30fps.
//  · if you choose a negative value, the images will flow to the left.
//  · no speed limit is defined, but speed between -10 and 10 is best.
slideshowBackground.setFlowingSpeed(5);

// set random playback
//  · if you set "true", the images will be played randomly.
//  · if you set "false", the images will be played in the order in which they were added.
//  · default setting is false.
slideshowBackground.setRandomPlayback(true);

// play slide show
slideshowBackground.play();
```
* There is no problem to set the speed and random playback while slide show is playing.

### How to pause slide show
```java
slideshowBackground.pause();
```

### How to stop slide show
```java
slideshowBackground.stop();
```

# License
```
MIT License

Copyright (c) 2021 Kazumasa Kusaba

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

