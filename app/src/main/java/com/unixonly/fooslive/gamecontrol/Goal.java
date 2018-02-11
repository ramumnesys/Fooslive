package com.unixonly.fooslive.gamecontrol;

import android.graphics.PointF;
import android.graphics.RectF;

import com.unixonly.fooslive.util.UnitUtils;

import java.util.Iterator;
import java.util.Queue;

/**
 * Created by paulius on 2/4/18.
 */

/**
 * Contains the data related to a particular goal, which
 * occurred during the game
 */
public class Goal {
    // TODO: Set value from app.config
    public static double sRealTableWidth;
    // TODO: Set value from app.config
    public static double sRealTableHeight;

    private PointF[] mPoints;

    private final double[] mSpeeds;
    private final double mMaxSpeed;

    private final long mTimestampStart;
    private final long mTimestampEnd;

    // Which team scored: true if blue, false otherwise
    private boolean mTeamColor;

    private long mDuration;

    public Goal(Queue<PointF> points, RectF tablePoints,
                long start, long end, boolean team) {
        mPoints = new PointF[points.size()];
        mSpeeds = new double[points.size()];
        mTimestampStart = start;
        mTimestampEnd = end;
        mMaxSpeed = 0;

        int i = 0;
        Iterator<PointF> iterator = points.iterator();
        for (;iterator.hasNext();i ++) mPoints[i] = iterator.next();

        double mulX = sRealTableWidth / (tablePoints.right - tablePoints.left);
        double mulY = sRealTableHeight / (tablePoints.bottom - tablePoints.top);

        mulX *= UnitUtils.metersToCentimeters(1);
        mulY *= UnitUtils.metersToCentimeters(1);

        calculateSpeeds(mulX, mulY);
    }

    private void calculateSpeeds(double mulX, double mulY) {
        PointF lastPoint = null;
        int lostFrameCount = 0;
        int i = 0;
        for (PointF point : mPoints) {
            if (lastPoint == null)
            {
                lastPoint = point;
                continue;
            }

            if (point == null)
            {
                lostFrameCount++;
                i++;
                continue;
            }

            mSpeeds[i] = Math.sqrt((point.x * mulX - lastPoint.x * mulX) * (point.x * mulX - lastPoint.x * mulX) +
                    (point.y * mulY - lastPoint.y * mulY) * (point.y * mulY - lastPoint.y * mulY));
            mSpeeds[i] /= lostFrameCount + 1.0f;

            if (mMaxSpeed < mSpeeds[i]) mMaxSpeed = mSpeeds[i];

            i++;

            lastPoint = point;
            lostFrameCount = 0;
        }
    }

    public double[] getSpeeds() {
        return mSpeeds;
    }

    public double getMaxSpeed() {
        return mMaxSpeed;
    }

    public long getDuration() {
        return mDuration;
    }
}
