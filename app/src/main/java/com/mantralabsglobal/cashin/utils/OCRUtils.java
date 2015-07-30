package com.mantralabsglobal.cashin.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import org.ejml.data.DenseMatrix64F;

import java.util.ArrayList;
import java.util.List;

import boofcv.abst.feature.detect.line.DetectLineSegmentsGridRansac;
import boofcv.abst.geo.Estimate1ofEpipolar;
import boofcv.alg.distort.DistortImageOps;
import boofcv.alg.distort.PointToPixelTransform_F32;
import boofcv.alg.distort.PointTransformHomography_F32;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.feature.detect.quadblob.DetectQuadBlobsBinary;
import boofcv.alg.feature.detect.quadblob.FindQuadCorners;
import boofcv.alg.feature.detect.quadblob.QuadBlob;
import boofcv.alg.feature.shapes.ShapeFittingOps;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.interpolate.TypeInterpolate;
import boofcv.alg.misc.ImageStatistics;
import boofcv.android.ConvertBitmap;
import boofcv.android.VisualizeImageData;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.factory.geo.FactoryMultiView;
import boofcv.struct.ConnectRule;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.distort.PixelTransform_F32;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageInt16;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import georegression.struct.line.LineSegment2D_F32;
import georegression.struct.point.Point2D_I32;

/**
 * Created by hello on 7/27/2015.
 */
public class OCRUtils extends AsyncTask<Bitmap,Bitmap,Bitmap> {

    private static final String TAG = OCRUtils.class.getSimpleName();
    static double toleranceDist = 20;
    static double toleranceAngle= Math.PI/10;

    public Bitmap automatedPerspectiveTransform(Bitmap source)
    {
        ImageUInt8 gray = ConvertBitmap.bitmapToGray(source, (ImageUInt8) null, null);

        publishProgress(ConvertBitmap.grayToBitmap(gray, Bitmap.Config.RGB_565));

        // the mean pixel value is often a reasonable threshold when creating a binary image
        double mean = ImageStatistics.mean(gray);

        ImageUInt8 binary = new ImageUInt8(gray.width,gray.height);
        // create a binary image by thresholding
        ThresholdImageOps.threshold(gray, binary, (int) mean, true);

        // reduce noise with some filtering
        ImageUInt8 filtered = BinaryImageOps.erode8(binary, 1, null);
        filtered = BinaryImageOps.dilate8(filtered, 1, null);

        // Find the contour around the shapes
        List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT,null);

        Bitmap bmp = ConvertBitmap.grayToBitmap(filtered, Bitmap.Config.RGB_565);
        publishProgress(bmp);
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.WHITE);

        double largestArea = Double.MIN_VALUE;
        Contour largestContour = null;
        for( Contour c : contours ) {
            // Fit the polygon to the found external contour.  Note loop = true
            List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external, true,
                    toleranceDist, toleranceAngle, 10);

            for( int i = 1; i < vertexes.size(); i++ ) {
                PointIndex_I32 a = vertexes.get(i-1);
                PointIndex_I32 b = vertexes.get(i);

                canvas.drawLine(a.x,a.y,b.x,b.y,paint);
            }

            PointIndex_I32 a = vertexes.get(vertexes.size()-1);
            PointIndex_I32 b = vertexes.get(0);

            canvas.drawLine(a.x, a.y, b.x, b.y, paint);

            if(vertexes != null && vertexes.size()==4)
            {
                double l = vertexes.get(0).distance(vertexes.get(1));
                double w = vertexes.get(0).distance(vertexes.get(3));
                double area = l*w;
                if(area>largestArea)
                {
                    largestArea = area;
                    Log.i(TAG,"found bigger Polygon length " + l + " , width : " + w);
                    largestContour = c;
                }
            }

        }

        publishProgress(bmp);


        DetectLineSegmentsGridRansac<ImageUInt8,ImageSInt16> detector = FactoryDetectLineAlgs.lineRansac(40, 30, 2.36, true, ImageUInt8.class, ImageSInt16.class);
        List<LineSegment2D_F32> lineSegment2D_f32s = detector.detect(ConvertBitmap.bitmapToGray(bmp, (ImageUInt8)null, null));

        bmp = ConvertBitmap.grayToBitmap(filtered, Bitmap.Config.RGB_565);

        for(LineSegment2D_F32 lineSegment2D_f32: lineSegment2D_f32s)
        {
            canvas.drawLine(lineSegment2D_f32.a.x, lineSegment2D_f32.a.y, lineSegment2D_f32.b.x, lineSegment2D_f32.b.y, paint);
        }
        publishProgress(bmp);

        /*
            DetectQuadBlobsBinary detectQuadBlobsBinary = new DetectQuadBlobsBinary(10, 0.25, 1);
        detectQuadBlobsBinary.process(ConvertBitmap.bitmapToGray(output,(ImageUInt8) null, null));
        List<QuadBlob> quadBlobList = detectQuadBlobsBinary.getDetected();

        double largestArea = Double.MIN_VALUE;
        QuadBlob largestBlob = null;
        if(quadBlobList != null && quadBlobList.size()>0) {
            for (QuadBlob quadBlob : quadBlobList) {
                double area = quadBlob.largestSide * quadBlob.smallestSide;
                if (area > largestArea && quadBlob.corners.size() == 4) {
                    largestArea = area;
                    largestBlob = quadBlob;
                }
            }
        }*/

        if(largestContour != null)
            return removePerspectiveDistortion(source, largestContour.external);
        else
            return source;

    }

    private Bitmap removePerspectiveDistortion(Bitmap source, List<Point2D_I32> corners)
    {
        MultiSpectral<ImageFloat32> input = ConvertBitmap.bitmapToMS(source, null, ImageFloat32.class, null);

        // Create a smaller output image for processing later on
        MultiSpectral<ImageFloat32> output = input._createNew(400, 500);

        // Homography estimation algorithm.  Requires a minimum of 4 points
        Estimate1ofEpipolar computeHomography = FactoryMultiView.computeHomography(true);

        FindQuadCorners findQuadCorners = new FindQuadCorners();
        //List<Point2D_I32> corners = findQuadCorners.process(contour.external);

        // Specify the pixel coordinates from destination to target
        ArrayList<AssociatedPair> associatedPairs = new ArrayList<AssociatedPair>();
        associatedPairs.add(new AssociatedPair(0,0, corners.get(0).getX(), corners.get(0).getY()));
        associatedPairs.add(new AssociatedPair(output.width-1,0,corners.get(1).getX(), corners.get(1).getY()));
        associatedPairs.add(new AssociatedPair(output.width-1,output.height-1,corners.get(2).getX(), corners.get(2).getY()));
        associatedPairs.add(new AssociatedPair(0, output.height - 1, corners.get(3).getX(), corners.get(3).getY()));

        // Compute the homography
        DenseMatrix64F H = new DenseMatrix64F(3,3);
        computeHomography.process(associatedPairs, H);

        // Create the transform for distorting the image
        PointTransformHomography_F32 homography = new PointTransformHomography_F32(H);
        PixelTransform_F32 pixelTransform = new PointToPixelTransform_F32(homography);

        // Apply distortion and show the results
        DistortImageOps.distortMS(input, output, pixelTransform, true, TypeInterpolate.BILINEAR);

        Bitmap bmp = Bitmap.createBitmap(400,500, Bitmap.Config.RGB_565);

        ConvertBitmap.multiToBitmap(output, bmp, null);

        return bmp;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        return automatedPerspectiveTransform(params[0]);
    }
}
