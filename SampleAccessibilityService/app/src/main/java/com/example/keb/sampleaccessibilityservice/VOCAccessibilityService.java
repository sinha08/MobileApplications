package com.example.keb.sampleaccessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * Created by ankur sinha on 15-07-2017.
 */

public class VOCAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("VOC","onAccessibilityEvent");
        final int eventType = event.getEventType();
        String eventText = "";
        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "Focused: ";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "Focused: ";
                break;
        }

        eventText = eventText + eventType;

        // Do something nifty with this text, like speak the composed string
        // back to the user.
        Toast.makeText(this,eventText,Toast.LENGTH_LONG).show();
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }
        if (source.getWindow() != null)
            Log.d("VOC",""+source.getWindow().getRoot().getText());
        Log.d("VOC",""+source.getText()+"\n"+source.getPackageName()+"\n"+source.getChildCount());
        int count = source.getChildCount();
        if(count>0){
            while (count > 0) {
                if (source.getChild(count-1)!=null)
                    Log.v("VOC",source.getChild(count-1).getContentDescription()+"");
                else
                    Log.v("VOC","child null");
                count--;
            }
        }
    }


    @Override
    public void onInterrupt() {
        Log.d("VOC","service interrupted");
    }

    @Override
    public void onServiceConnected() {
        Log.d("VOC","service connected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        // we are interested in all types of accessibility events
        info.describeContents();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        // we want to receive events in a certain interval
        info.notificationTimeout = 100;

        setServiceInfo(info);
    }
}

