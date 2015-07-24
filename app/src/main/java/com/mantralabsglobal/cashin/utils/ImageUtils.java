package com.mantralabsglobal.cashin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.ejml.data.DenseMatrix64F;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import boofcv.abst.feature.detect.line.DetectLineSegmentsGridRansac;
import boofcv.abst.geo.Estimate1ofEpipolar;
import boofcv.alg.distort.DistortImageOps;
import boofcv.alg.distort.PointToPixelTransform_F32;
import boofcv.alg.distort.PointTransformHomography_F32;
import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.feature.detect.edge.EdgeSegment;
import boofcv.alg.feature.detect.quadblob.FindQuadCorners;
import boofcv.alg.feature.shapes.ShapeFittingOps;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.interpolate.TypeInterpolate;
import boofcv.android.ConvertBitmap;
import boofcv.android.VisualizeImageData;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.factory.geo.FactoryMultiView;
import boofcv.struct.ConnectRule;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.distort.PixelTransform_F32;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import georegression.struct.line.LineSegment2D_F32;
import georegression.struct.point.Point2D_F32;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;

/**
 * Created by pk on 7/15/2015.
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    public ImageUtils()
    {

    }

    public Bitmap binarize(Bitmap source)
    {
        ImageUInt8 gray = ConvertBitmap.bitmapToGray(source, (ImageUInt8) null, null);

        ImageUInt8 binary = new ImageUInt8(gray.width,gray.height);

        // Select a global threshold using Otsu's method.
       // double threshold = GThresholdImageOps.computeOtsu(gray, 0, 256);

        // Apply the threshold to create a binary image
        //ThresholdImageOps.threshold(gray, binary, (int) threshold, false);



        GThresholdImageOps.adaptiveSauvola(gray, binary, 20, 0.30f, false);

        ImageUInt8 adjusted = new ImageUInt8(gray.width, gray.height);

        EnhanceImageOps.sharpen8(binary, adjusted);

        // remove small blobs through erosion and dilation
        // The null in the input indicates that it should internally declare the work image it needs
        // this is less efficient, but easier to code.
        /*ImageUInt8 filtered = BinaryImageOps.erode8(binary, 1, null);
        filtered = BinaryImageOps.dilate8(filtered, 1, null);*/
        Bitmap  result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        VisualizeImageData.binaryToBitmap(adjusted, result, null );
        return result;
    }

    public Bitmap processImage(final Bitmap source, Context context) {
        AsyncTask<Bitmap, Void, Bitmap> asyncTask = new AsyncTask<Bitmap, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Bitmap... params) {
                return detectLineSegments(params[0]);
            }
        }.execute(source);

        try {
            return asyncTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return source;
    }

    private Bitmap canny(Bitmap source)
    {
        ImageUInt8 gray = ConvertBitmap.bitmapToGray(source, (ImageUInt8) null, null);
        ImageUInt8 edgeImage = new ImageUInt8(gray.width,gray.height);

        // Create a canny edge detector which will dynamically compute the threshold based on maximum edge intensity
        // It has also been configured to save the trace as a graph.  This is the graph created while performing
        // hysteresis thresholding.
        CannyEdge<ImageUInt8,ImageSInt16> canny = FactoryEdgeDetectors.canny(2, true, true, ImageUInt8.class, ImageSInt16.class);

        // The edge image is actually an optional parameter.  If you don't need it just pass in null
        canny.process(gray,0.1f,0.3f,edgeImage);

        // First get the contour created by canny
        List<EdgeContour> edgeContours = canny.getContours();
        // The 'edgeContours' is a tree graph that can be difficult to process.  An alternative is to extract
        // the contours from the binary image, which will produce a single loop for each connected cluster of pixels.
        // Note that you are only interested in external contours.
        List<Contour> contours = BinaryImageOps.contour(edgeImage, ConnectRule.EIGHT, null);
        Log.i(TAG, "Contour size" + contours.size());

        List<PointIndex_I32> match= null;
        EdgeContour edgeContour = null;

        for( EdgeContour e : edgeContours ) {

            for(EdgeSegment s : e.segments ) {
                // fit line segments to the point sequence.  Note that loop is false
                List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(s.points, true,
                        5, 0.1, 5);

                if(vertexes.size()== 4)
                {
                    match = vertexes;
                    edgeContour = e;
                    break;
                }

            }
        }
    if(match != null) {
        VisualizeImageData.drawEdgeContours(edgeContours, 0xFF0000, source, (byte[]) null);
        return source;//removePerspectiveDistortion(source, match);
    }
    else {

        //VisualizeImageData.drawEdgeContours( edgeContours, 0xFF0000, source, (byte[]) null);
        return source;
    }
        //VisualizeImageData.drawEdgeContours(edgeContours, 0xFF0000, output, (byte[]) null);
    }

    public Bitmap detectLineSegments( Bitmap image)
    {
        // convert the line into a single band image
        //T input = ConvertBufferedImage.convertFromSingle(image, null, imageType);
        ImageFloat32 input = ConvertBitmap.bitmapToGray(image, null, ImageFloat32.class, null);

        // Comment/uncomment to try a different type of line detector
        DetectLineSegmentsGridRansac<ImageFloat32, ImageFloat32> detector = FactoryDetectLineAlgs.lineRansac(40, 30, 2.36, true, ImageFloat32.class, ImageFloat32.class);

        List<LineSegment2D_F32> found = detector.detect(input);

        Log.d(TAG, "Lines found count " + found.size());
        LinkedList<Point2D_F32> rectangle = null;

        for(LineSegment2D_F32 line: found)
        {
            LinkedList<Point2D_F32> chain = getJointLineSegment(line, null, found);
            if(chain.getFirst() == chain.getLast() && chain.size()==5
                    && chain.getFirst().getX() == chain.getLast().getX()
                    && chain.getFirst().getY() == chain.getLast().getY())
            {
                rectangle = chain;
                Log.d(TAG, "Chain found for line" + line);
            }
        }
        if(rectangle != null)
           return removePerspectiveDistortion(image, rectangle);
        else
            return image;
    }

    private LinkedList<Point2D_F32> getJointLineSegment(LineSegment2D_F32 seed, LinkedList<Point2D_F32> chain, List<LineSegment2D_F32> found )
    {
        if(chain == null || chain.size()==0)
        {
            chain = new LinkedList<>();
            chain.add(seed.getA());
            return getJointLineSegment(null, chain, found);
        }

        Point2D_F32 current = chain.getLast();

        for(LineSegment2D_F32 loopLine : found)
        {
            if(chain.contains(loopLine))
                continue;
            Point2D_F32 nextPoint = checkIfLinesAreJoined(current, loopLine);
            if(nextPoint != null)
            {
                chain.add(nextPoint);
                getJointLineSegment(null, chain, found);
            }
        }
        return chain;
    }

    private Point2D_F32 checkIfLinesAreJoined(Point2D_F32 point, LineSegment2D_F32 line2)
    {
        if( point.getX() == line2.getA().getX() && point.getY()==line2.getA().getY()
                )
        {
            return line2.getB();
        }
        else if(point.getX() == line2.getB().getX() && point.getY()==line2.getB().getY())
            return line2.getA();

        return null;
    }

    private Bitmap removePerspectiveDistortion(Bitmap source, List<Point2D_F32> corners)
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
        associatedPairs.add(new AssociatedPair(0,output.height-1,corners.get(3).getX(), corners.get(3).getY()));

        // Compute the homography
        DenseMatrix64F H = new DenseMatrix64F(3,3);
        computeHomography.process(associatedPairs, H);

        // Create the transform for distorting the image
        PointTransformHomography_F32 homography = new PointTransformHomography_F32(H);
        PixelTransform_F32 pixelTransform = new PointToPixelTransform_F32(homography);

        // Apply distortion and show the results
        DistortImageOps.distortMS(input, output, pixelTransform, true, TypeInterpolate.BILINEAR);

        Bitmap bmp = Bitmap.createBitmap(400,500, Bitmap.Config.ARGB_8888);

        ConvertBitmap.multiToBitmap(output, bmp, null);

        return bmp;
    }

    /*public Bitmap processImage2(Bitmap srcBitmap, Context context)
    {
        Mat imgSource = new Mat();
        Utils.bitmapToMat(srcBitmap, imgSource);
        //convert the image to black and white does (8 bit)
        Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BGR2GRAY);

        //apply gaussian blur to smoothen lines of dots
        Imgproc.GaussianBlur(imgSource, imgSource, new org.opencv.core.Size(5, 5), 5);

        double highThreshold = Imgproc.threshold(imgSource, new Mat(), 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);

        Imgproc.Canny(imgSource, imgSource, 30, 30);



        *//*Mat houghLines = new Mat();
        Imgproc.HoughLinesP(imgSource, imgSource, 1, 3.14/ 180, 70, 30, 10);
*//*
        Bitmap bitmap = Bitmap.createBitmap(imgSource.cols(), imgSource.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgSource, bitmap);
        return bitmap;*//*
        //find the contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = -1;
        int maxAreaIdx = -1;
        Log.d("size", Integer.toString(contours.size()));
        MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint largest_contour = contours.get(0);
        //largest_contour.ge
        List<MatOfPoint> largest_contours = new ArrayList<MatOfPoint>();
        //Imgproc.drawContours(imgSource,contours, -1, new Scalar(0, 255, 0), 1);

        for (int idx = 0; idx < contours.size(); idx++) {
            temp_contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(temp_contour);

            if(contourarea>300)
            {
                MatOfPoint2f new_mat = new MatOfPoint2f( temp_contour.toArray() );
                double epsilon = 0.05*temp_contour.total(); //Imgproc.arcLength(new_mat, temp_contour.isContinuous());

                //int contourSize = (int)temp_contour.total();
                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();

                Imgproc.approxPolyDP(new_mat, approxCurve_temp, epsilon, true);
                if(contourarea > maxArea &&  approxCurve_temp.total()==4)
                {
                    maxArea = contourarea;
                    maxAreaIdx = idx;
                    approxCurve=approxCurve_temp;
                    largest_contour = temp_contour;
                }

            }

           *//* //compare this contour to the previous largest contour found
            if (contourarea > maxArea) {
                //check if this contour is a square
                MatOfPoint2f new_mat = new MatOfPoint2f( temp_contour.toArray() );
                int contourSize = (int)temp_contour.total();
                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
                Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize*0.01, true);
                if (approxCurve_temp.total() == 4 ) {
                    maxArea = contourarea;
                    maxAreaIdx = idx;
                    approxCurve=approxCurve_temp;
                    largest_contour = temp_contour;
                }
            }*//*
        }

        Mat tempSource = imgSource.clone();
        Scalar color = new Scalar(255, 255, 255);
        Imgproc.drawContours(tempSource, contours, -1, color, 5);
        Imgcodecs.imwrite(context.getExternalFilesDir(null).getAbsolutePath() + "/contours.jpeg", tempSource);

        tempSource = imgSource.clone();
        Imgproc.drawContours(tempSource, contours, maxAreaIdx, color, 5);
        //color = new Scalar(0, 255, 0);
        Imgcodecs.imwrite(context.getExternalFilesDir(null).getAbsolutePath() + "/contours1.jpeg", tempSource);

        Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BayerBG2RGB);
        Mat sourceImage = new Mat() ;//Imgcodecs.imread(Environment.getExternalStorageDirectory().
                //getAbsolutePath() + "/scan/p/1.jpg");
        Utils.bitmapToMat(srcBitmap, sourceImage);
        double[] temp_double;
            temp_double = approxCurve.get(0,0);
        if(temp_double != null && temp_double.length>1) {
            Point p1 = new Point(temp_double[0], temp_double[1]);
            //Core.circle(imgSource,p1,55,new Scalar(0,0,255));
            //Imgproc.warpAffine(sourceImage, dummy, rotImage,sourceImage.size());
            temp_double = approxCurve.get(1, 0);
            Point p2 = new Point(temp_double[0], temp_double[1]);
            // Core.circle(imgSource,p2,150,new Scalar(255,255,255));
            temp_double = approxCurve.get(2, 0);
            Point p3 = new Point(temp_double[0], temp_double[1]);
            //Core.circle(imgSource,p3,200,new Scalar(255,0,0));
            temp_double = approxCurve.get(3, 0);
            Point p4 = new Point(temp_double[0], temp_double[1]);
            // Core.circle(imgSource,p4,100,new Scalar(0,0,255));
            List<Point> source = new ArrayList<Point>();
            source.add(p1);
            source.add(p2);
            source.add(p3);
            source.add(p4);
            Mat startM = Converters.vector_Point2f_to_Mat(source);
            Mat result = warp(sourceImage, startM);
            Bitmap bitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(result, bitmap);
            return bitmap;
        }
        else
        {
            return srcBitmap;
        }
    }

    public Mat warp(Mat inputMat,Mat startM) {
        int resultWidth = inputMat.width()/2;
        int resultHeight = inputMat.height()/2;

        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);



        Point ocvPOut1 = new Point(0, 0);
        Point ocvPOut2 = new Point(0, resultHeight);
        Point ocvPOut3 = new Point(resultWidth, resultHeight);
        Point ocvPOut4 = new Point(resultWidth, 0);
        List<Point> dest = new ArrayList<Point>();
        dest.add(ocvPOut1);
        dest.add(ocvPOut2);
        dest.add(ocvPOut3);
        dest.add(ocvPOut4);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat,
                outputMat,
                perspectiveTransform,
                new Size(resultWidth, resultHeight),
                Imgproc.INTER_CUBIC);

        return outputMat;
    }*/
}
