/**
 * Copyright 2013 Nils Assbeck, Guersel Ayaz and Michael Zoech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iphoto.plus.ptp.commands;

import com.iphoto.plus.ptp.Camera;
import com.iphoto.plus.ptp.PtpAction;
import com.iphoto.plus.ptp.PtpCamera;
import com.iphoto.plus.ptp.PtpConstants;

public class RetrieveImageAction implements PtpAction {

    private final PtpCamera camera;
    private final int objectHandle;
    private final Camera.RetrieveImageListener listener;
    private final int sampleSize;

    public RetrieveImageAction(PtpCamera camera, Camera.RetrieveImageListener listener, int objectHandle, int sampleSize) {
        this.camera = camera;
        this.listener = listener;
        this.objectHandle = objectHandle;
        this.sampleSize = sampleSize;
    }

    @Override
    public void exec(PtpCamera.IO io) {
        GetObjectCommand getObject = new GetObjectCommand(camera, objectHandle, sampleSize);
        io.handleCommand(getObject);
        GetObjectInfoCommand getObjectInfo = new GetObjectInfoCommand(camera, objectHandle);
        io.handleCommand(getObjectInfo);

        if (getObject.getResponseCode() != PtpConstants.Response.Ok) {
           // listener.onImageRetrieved(0, null,0,null);
            return;
        }

        listener.onImageRetrieved(objectHandle, getObject.getBuffer(),getObject.getLength(),getObjectInfo.getObjectInfo());
    }

    @Override
    public void reset() {
    }
}
