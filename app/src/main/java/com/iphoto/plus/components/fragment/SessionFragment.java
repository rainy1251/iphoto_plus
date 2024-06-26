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
package com.iphoto.plus.components.fragment;

import com.iphoto.plus.ptp.Camera;
import com.iphoto.plus.util.AppSettings;
import com.iphoto.plus.view.SessionActivity;
import com.iphoto.plus.view.SessionView;


public abstract class SessionFragment extends BFragment implements SessionView {

    protected boolean inStart;

    protected Camera camera() {
        if (getActivity() == null) {
            return null;
        }
        return ((SessionActivity) getActivity()).getCamera();
    }

    protected AppSettings getSettings() {
        return ((SessionActivity) getActivity()).getSettings();
    }

    @Override
    public void onStart() {
        super.onStart();
        inStart = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        inStart = false;
    }
}
