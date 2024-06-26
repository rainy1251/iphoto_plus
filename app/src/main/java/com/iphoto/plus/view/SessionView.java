/**
 * Copyright 2013 Nils Assbeck, Guersel Ayaz and Michael Zoech
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iphoto.plus.view;

import android.graphics.Bitmap;

import com.iphoto.plus.ptp.Camera;
import com.iphoto.plus.ptp.model.LiveViewData;

public interface SessionView {

    public abstract void enableUi(boolean enabled);

    public abstract void cameraStarted(Camera camera);

    public abstract void cameraStopped(Camera camera);

    public abstract void cameraNoFound();//new

    public abstract void propertyChanged(int property, int value);

    public abstract void propertyDescChanged(int property, int[] values);

    public abstract void setCaptureBtnText(String text);

    public abstract void focusStarted();

    public abstract void focusEnded(boolean hasFocused);

    public abstract void liveViewStarted();

    public abstract void liveViewStopped();

    public abstract void liveViewData(LiveViewData data);

    public abstract void capturedPictureReceived(int objectHandle, String filename, Bitmap thumbnail, Bitmap bitmap);

    public abstract void objectAdded(int handle, int format);
    public abstract void rateAdded(int handle);
}