/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dask.pent;


/**
 * Calculates and displays the distance walked.  
 * @author Levente Bagi
 */
public class DistanceNotifier implements StepListener {

    public interface Listener {
        public void valueChanged(double value);
    }
    private Listener mListener;
    
    double mDistance = 0;
    
    boolean mIsMetric;
    float mStepLength;

    public DistanceNotifier(Listener listener) {
        mListener = listener;
        mStepLength = 20;
    }
    
    public void setDistance(double distance) {
        mDistance = distance;
        notifyListener();
    }
    
    public void onStep() {
    	mDistance += (double)(	// kilometers
    			mStepLength 	// centimeters
    			/ 100.0); 		// centimeters/kilometer
        notifyListener();
    }
    
    private void notifyListener() {
        mListener.valueChanged(mDistance);
    }
    
    public void passValue() {
        // Callback of StepListener - Not implemented
    }
    

}

