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

import com.iphoto.plus.ptp.PtpCamera;
import com.iphoto.plus.ptp.PtpConstants;

import java.nio.ByteBuffer;

public class InitiateCaptureCommand extends Command {

    public InitiateCaptureCommand(PtpCamera camera) {
        super(camera);
    }

    @Override
    public void exec(PtpCamera.IO io) {
        io.handleCommand(this);
        if (responseCode == PtpConstants.Response.DeviceBusy) {
            camera.onDeviceBusy(this, true); // TODO when nikon live view is enabled this stalls
            return;
        }
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.InitiateCapture, 0, 0);
    }
}
