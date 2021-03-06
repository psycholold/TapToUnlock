/*
 * Copyright 2015 Hannes Bibel, Valentin Sawadski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abominableshrine.taptounlock;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Describing a tap pattern
 * <p/>
 * A tap pattern will be described by the side that has been tapped and the interval between two
 * taps.
 * <p/>
 * Facts like the force of the tap or the position of the tap on the side will not be
 * considered by this model.
 * <p/>
 * This class can be easily sent via Messenger to remote services as it provides convenient methods
 * to be bundled and extracted from a bundle. See {@link #toBundle()} and
 * {@link #TapPattern(android.os.Bundle)}
 */
public class TapPattern {

    final private static String KEY_SIDES = "sides";
    final private static String KEY_PAUSES = "pauses";
    /**
     * The percentage the comparison duration may differ from the this duration
     * <p/>
     * The target duration must be within (1 - MAX_DURATION_TOLERANCE) * this.duration() and
     * (1 + MAX_DURATION_TOLERANCE) * this.duration
     */
    private static final float MAX_DURATION_TOLERANCE = 0.3f;
    /**
     * Same as {@link #MAX_DURATION_TOLERANCE} but for the position of individual taps in the
     * pattern
     */
    private static final float MAX_TAP_POSITION_TOLERANCE = 0.2f;

    private ArrayList<DeviceSide> sides;
    private ArrayList<Integer> pauses;

    /**
     * Create an empty tap pattern
     */
    public TapPattern() {
        this.sides = new ArrayList<>();
        this.pauses = new ArrayList<>();
    }

    /**
     * Create a tap pattern from a Bundle
     *
     * @param b The bundle to create the pattern from
     */
    public TapPattern(Bundle b) {
        this();

        this.pauses = b.getIntegerArrayList(TapPattern.KEY_PAUSES);
        int sides[] = b.getIntArray(TapPattern.KEY_SIDES);
        for (int side : sides) {
            this.sides.add(DeviceSide.values()[side]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TapPattern that = (TapPattern) o;

        if (!pauses.equals(that.pauses)) return false;
        if (!sides.equals(that.sides)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sides.hashCode();
        result = 31 * result + pauses.hashCode();
        return result;
    }

    /**
     * Add a new tap to the end of the pattern
     * <p/>
     * The pause will be ignored for the first tap in the pattern
     *
     * @param where          Where the device has been tapped
     * @param pauseBeforeTap Pause before this tap in milliseconds
     * @return The same pattern for call chaining
     */
    public TapPattern appendTap(DeviceSide where, int pauseBeforeTap) {
        if (this.size() != 0) {
            if (pauseBeforeTap <= 0) {
                return null;
            }
            this.pauses.add(pauseBeforeTap);
        }
        this.sides.add(where);
        return this;
    }

    /**
     * The number of taps in the pattern
     *
     * @return The number of taps in the pattern
     */
    public int size() {
        return this.sides.size();
    }

    /**
     * The duration in nanoseconds of the pattern
     *
     * @return The duration in nanoseconds
     */
    public long duration() {
        long totalPause = 0;
        for (long pause : this.pauses) {
            totalPause += pause;
        }
        return totalPause;
    }

    /**
     * Convert this tap pattern to a bundle that can be sent via a Message
     *
     * @return The bundle representation of the tap pattern
     */
    public Bundle toBundle() {
        Bundle b = new Bundle();
        int sides[] = new int[this.sides.size()];
        for (int i = 0; i < sides.length; i++) {
            sides[i] = this.sides.get(i).ordinal();
        }
        b.putIntArray(TapPattern.KEY_SIDES, sides);
        b.putIntegerArrayList(TapPattern.KEY_PAUSES, this.pauses);
        return b;
    }

    /**
     * Compares if two tap patterns match each other given certain tolerance
     * <p/>
     * This comparison is more relaxed than {@link #equals(Object)} in the ways that timings may lie
     * in a certain tolerance and {@link DeviceSide#ANY}
     * is being handled correctly.
     *
     * @param p The pattern to compare this against
     * @return True if they are similar to each other; false otherwise
     */
    public boolean matches(TapPattern p) {
        if (null == p) {
            return false;
        }
        if (this.equals(p)) {
            return true;
        }
        if (this.size() != p.size()) {
            return false;
        }

        for (int i = 0; i < this.size(); i++) {
            boolean isSameSide = this.sides.get(i) == p.sides.get(i);
            if (!isSameSide) {
                boolean isAnySide = this.sides.get(i) == DeviceSide.ANY || p.sides.get(i) == DeviceSide.ANY;
                if (!isAnySide) {
                    return false;
                }
            }
        }

        float timeScale = ((float) p.duration()) / this.duration();
        if (Math.abs(timeScale - 1f) > MAX_DURATION_TOLERANCE) {
            return false;
        }
        for (int i = 0; i < this.pauses.size(); i++) {
            float scaledTime = (this.pauses.get(i) * timeScale);
            float tapScale = ((float) p.pauses.get(i) / scaledTime);
            if (Math.abs(tapScale - 1f) > MAX_TAP_POSITION_TOLERANCE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the device side of a tap in the pattern
     *
     * @param i The index of the tap
     * @return The device side
     */
    public DeviceSide getSide(int i) {
        return this.sides.get(i);
    }

    @Override
    public String toString() {
        String ret = "TapPattern{";
        for(int i = 0; i < this.size(); i++) {
            ret += " " + this.getPause(i) + ":" + this.getSide(i);
        }
        return ret + " }";
    }

    /**
     * Return the pause between the tap and the previous tap
     *
     * @param i The index of the tap
     * @return The pause or 0 if it is the first tap
     */
    public long getPause(int i) {
        // Input validation through getSide()
        @SuppressWarnings("UnusedDeclaration") DeviceSide s = this.getSide(i);

        int index = i - 1;
        if (index < 0) {
            return 0;
        }
        return this.pauses.get(index);
    }
}
