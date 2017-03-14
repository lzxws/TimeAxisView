# TimeAxisView
A simple view for regular time running include taking off and landing show.

### build with gradle:
    com.zxlee.station:TimeAxisView:1.0.1
### or with maven
    <dependency> 
      <groupId>com.zxlee.station</groupId> 
      <artifactId>TimeAxisView</artifactId> 
      <version>1.0.1</version> 
    </dependency>
![](https://github.com/lzxws/TimeAxisView/blob/master/app/doc/movie.gif)
### Usage
```
<HorizontalScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="@dimen/activity_horizontal_margin"
        android:scrollbars="none">

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@color/green3BAF24"
            android:orientation="horizontal">

            <com.zxlee.timeaxisview.TimeAxisView
                android:id="@+id/timeAxisView2"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:background="@color/white"
                app:color_valid_period="@color/blue1D81FC"
                app:color_invalid_period="@color/yellowF6BC1F"
                app:circle_radius="3dp"/>
        </LinearLayout>
    </HorizontalScrollView>
```
  you can use more attributes to make your view colorful:
  ```
  color_valid_period
  color_invalid_period
  color_circle
  color_time
  circle_radius
  text_time_size
  img_ready
  img_finish
  img_up
  img_down
  img_forward
  img_i
  ```
  As the name suggests,all of these are understandable.
