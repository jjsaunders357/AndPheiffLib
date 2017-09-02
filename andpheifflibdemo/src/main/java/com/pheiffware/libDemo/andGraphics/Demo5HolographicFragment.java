package com.pheiffware.libDemo.andGraphics;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.opengl.GLES20;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.and.graphics.AndGraphicsUtils;
import com.pheiffware.lib.and.gui.graphics.openGL.BaseGameFragment;
import com.pheiffware.lib.and.gui.graphics.openGL.GameView;
import com.pheiffware.lib.and.gui.graphics.openGL.SystemInfo;
import com.pheiffware.lib.and.input.OrientationTracker;
import com.pheiffware.lib.and.input.TouchAnalyzer;
import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.geometry.collada.ColladaFactory;
import com.pheiffware.lib.geometry.collada.ColladaObject3D;
import com.pheiffware.lib.graphics.EuclideanCamera;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.Projection;
import com.pheiffware.lib.graphics.Vec4F;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.MeshDataManager;
import com.pheiffware.lib.graphics.managed.engine.MeshHandle;
import com.pheiffware.lib.graphics.managed.engine.ObjectHandle;
import com.pheiffware.lib.graphics.managed.engine.renderers.CubeDepthRenderer;
import com.pheiffware.lib.graphics.managed.engine.renderers.SimpleRenderer;
import com.pheiffware.lib.graphics.managed.frameBuffer.FrameBuffer;
import com.pheiffware.lib.graphics.managed.light.HoloLighting;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.GraphicsConfig;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.graphics.managed.techniques.Std3DTechnique;
import com.pheiffware.lib.graphics.managed.texture.TextureCubeMap;
import com.pheiffware.lib.graphics.utils.MeshGenUtils;
import com.pheiffware.lib.graphics.utils.PheiffGLUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;
import com.pheiffware.libDemo.Demo3DRenderer;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Loads a mesh using the Collada library and displays it on the screen.  Allows the camera to be adjusted using TouchTransform events. Created by Steve on 3/27/2016.
 */

public class Demo5HolographicFragment extends BaseGameFragment
{
    private static final int CAMERA_WIDTH = 240;
    private static final int CAMERA_HEIGHT = 320;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 30465;
    FaceDetector faceDetector;
    CameraSource cameraSource;
    private boolean alreadyAskedForCamera;
    private Renderer renderer;

    @Override
    public GameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        alreadyAskedForCamera = false;

        faceDetector = new FaceDetector.Builder(getContext()).
                setProminentFaceOnly(true)
                .setLandmarkType(FaceDetector.NO_LANDMARKS)
                .setMinFaceSize(0.01f)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setTrackingEnabled(true)
//                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
//                .setMode(FaceDetector.FAST_MODE)
                .build();
        faceDetector.setProcessor(
                new LargestFaceFocusingProcessor.Builder(faceDetector, new FaceTracker())
                        .build());


        if (!faceDetector.isOperational())
        {
            //TODO: Proper error
            Log.e("FACE", "Face detector dependencies are not yet available.");
            getActivity().finish();
        }
        cameraSource = new CameraSource.Builder(getContext(), faceDetector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedPreviewSize(CAMERA_WIDTH, CAMERA_HEIGHT)
                .setAutoFocusEnabled(true)
                .setRequestedFps(30)
                .build();
        this.renderer = new Renderer();
        return new GameView(getContext(), renderer, FilterQuality.MEDIUM, true, true);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        cameraSource.release();
        faceDetector.release();
        renderer = null;
    }

    @Override
    public void onPause()
    {
        Log.i("Permissions", "Pause");
        super.onPause();
        cameraSource.stop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i("Permissions", "Resume");
        startCamera();

    }

    private void startCamera()
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (alreadyAskedForCamera)
            {
                //TODO: Proper error reporting
                Log.e("Permissions", "App cannot function without the camera!");
                getActivity().finish();
                return;
            }
            else
            {
                Log.i("Permissions", "Request permissions");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST_CODE);
                alreadyAskedForCamera = true;
                return;
            }
        }
        try
        {
            cameraSource.start();
        }
        catch (SecurityException e)
        {
            throw new RuntimeException("Permission was granted and immediately rejected", e);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not access camera", e);
        }

    }


    private class FaceTracker extends Tracker<Face>
    {
        float averageWidth = 0;
        int numSamples = 0;

        private void sendFaceUpdate(final boolean newFace, Face face)
        {
            if (face != null)
            {
                final PointF leftEyePosition;
                Landmark leftEye = getLandmark(face, Landmark.LEFT_EYE);
                if (leftEye != null)
                {
                    leftEyePosition = leftEye.getPosition();
                }
                else
                {
                    leftEyePosition = null;
                }

                final float width = face.getWidth();
                final float height = face.getHeight();
                final float eulerY = face.getEulerY();
                final float eulerZ = face.getEulerZ();
                final PointF position = face.getPosition();
                getView().queueEvent(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        renderer.updateFace(width, height, eulerY, eulerZ, position, leftEyePosition);
                    }
                });
            }

        }

        @Override
        public void onNewItem(int faceId, final Face face)
        {
            //Log.i("Face", "New face: " + faceId);
            sendFaceUpdate(true, face);
        }

        private Landmark getLandmark(Face face, int type)
        {
            List<Landmark> landmarks = face.getLandmarks();
            for (Landmark landmark : landmarks)
            {
                if (landmark.getType() == type)
                {
                    return landmark;
                }
            }

            return null;
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults,
                             Face face)
        {
            sendFaceUpdate(false, face);


//            Log.i("Face", "Left Eye: " + leftEyePosition + " Right Eye: " + rightEyePosition);

//            Log.i("Face", "Tracking face: RY = " + face.getEulerY() + " + RZ = " + face.getEulerZ());
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults)
        {
            //Log.i("Face", "Face missing");
            getView().queueEvent(new Runnable()
            {
                @Override
                public void run()
                {
                    renderer.faceMissing();
                }
            });
        }

        @Override
        public void onDone()
        {
            //Log.i("Face", "Done");
        }
    }

    private static class EyeBuffer
    {
        private static final float MaxEyeTime = 0.2f;
        LinkedList<Vec4F> eyes = new LinkedList<>();
        LinkedList<Long> eyeTimes = new LinkedList<>();

        void addEye(Vec4F eye)
        {
            eyeTimes.add(System.nanoTime());
            eyes.add(eye.copy());
        }

        float getTimeDiff(long now, long previous)
        {
            return (float) ((now - previous) / 1000000000.0);
        }

        Vec4F getEye(Matrix4 orientationMatrix)
        {
            if (eyes.size() == 0)
            {
                return null;
            }
            long now = System.nanoTime();
            Vec4F totalEye = new Vec4F(1);
            float totalWeight = 0;
            Iterator<Vec4F> eyeIter = eyes.iterator();
            Iterator<Long> eyeTimeIter = eyeTimes.iterator();
            while (eyeIter.hasNext())
            {
                Vec4F eye = eyeIter.next();
                long eyeTime = eyeTimeIter.next();

                float timeDiff = getTimeDiff(now, eyeTime);
                if (timeDiff > MaxEyeTime && eyes.size() > 1)
                {
                    eyeIter.remove();
                    eyeTimeIter.remove();
                }
                else
                {
                    float weight = 1.0f - Math.min(1.0f, timeDiff / MaxEyeTime);
                    totalWeight += weight;
                    Vec4F eyeCopy = eye.copy();
                    eyeCopy.transformBy(orientationMatrix);
                    totalEye.setX(totalEye.x() + eyeCopy.x() * weight);
                    totalEye.setY(totalEye.y() + eyeCopy.y() * weight);
                    totalEye.setZ(totalEye.z() + eyeCopy.z() * weight);
                }
            }
            if (totalWeight == 0f)
            {
                totalEye = eyes.getFirst().copy();
                totalEye.transformBy(orientationMatrix);
            }
            else
            {
                totalEye.scaleBy(1.0f / totalWeight);
            }
            return totalEye;

        }

        void updateEyes(Matrix4 orientationMatrix)
        {
            long now = System.nanoTime();
            Iterator<Vec4F> eyeIter = eyes.iterator();
            Iterator<Long> eyeTimeIter = eyeTimes.iterator();
            while (eyeIter.hasNext())
            {
                Vec4F eye = eyeIter.next();
                long eyeTime = eyeTimeIter.next();

                float timeDiff = getTimeDiff(now, eyeTime);

                if (timeDiff > MaxEyeTime && eyes.size() > 1)
                {
                    eyeIter.remove();
                    eyeTimeIter.remove();
                }
                else
                {
                    eye.transformBy(orientationMatrix);
                }
            }
        }
    }

    private static class Renderer extends Demo3DRenderer
    {
        private static final float SCREEN_ALPHA = 0.3f;
        private static final int numMonkeys = 30;
        private OrientationTracker orientationTracker;
        private HoloLighting lighting;
        private TextureCubeMap[] cubeDepthTextures;
        private Std3DTechnique colorTechnique;
        private MeshDataManager manager;

        //Represents the position of the eye relative to surface of the direct center of the screen if screen is flat.
        private final Vec4F absEyePosition = new Vec4F(0, 0, 2.7f, 1);
        //private final Vec4F cameraEyePosition = new Vec4F(0, 0, 4, 1);
        private EyeBuffer eyeBuffer;

        private MeshHandle[] monkeyHandles;
        private Mesh screenQuad;
        ObjectHandle monkeyGroupHandle;
        private MeshHandle screenHandle;
        private CubeDepthRenderer cubeDepthRenderer;
        private SimpleRenderer simpleRenderer;

        public Renderer()
        {
            super(AndGraphicsUtils.GL_VERSION_30, AndGraphicsUtils.GL_VERSION_30, "shaders", 90f, 1.0f, 100.0f, 0.01f);
        }

        @Override
        public void onSurfaceCreated(AssetLoader al, GLCache glCache, SystemInfo systemInfo) throws GraphicsException
        {
            super.onSurfaceCreated(al, glCache, systemInfo);
            eyeBuffer = new EyeBuffer();
            PheiffGLUtils.enableAlphaTransparency();
            orientationTracker = new OrientationTracker(true);
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

//            lighting = new HoloLighting(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, new float[]{1, 0.5f, 2, 1}, new float[]{0.7f, 0.7f, 0.7f, 1.0f}, new boolean[]{true});
            lighting = new HoloLighting(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, new float[]{0, 0, 2.7f, 1}, new float[]{0.7f, 0.7f, 0.7f, 1.0f}, new boolean[]{true});
            //lighting.setCastsCubeShadow(0, 1);
            cubeDepthTextures = new TextureCubeMap[Lighting.numLightsSupported];
            cubeDepthTextures[0] = glCache.buildCubeDepthTex(1024, 1024).build();
            cubeDepthRenderer = new CubeDepthRenderer(glCache, 0.1f, 100.0f);
            PheiffGLUtils.enableAlphaTransparency();

            colorTechnique = glCache.buildTechnique(Std3DTechnique.class, GraphicsConfig.TEXTURED_MATERIAL, false);
            simpleRenderer = new SimpleRenderer(colorTechnique);

            ColladaFactory colladaFactory = new ColladaFactory();
            try
            {
                Collada collada = colladaFactory.loadCollada(al, "meshes/test_render.dae");

                //Lookup object from loaded file by "name" (what user named it in editing tool)
                ColladaObject3D monkey = collada.objects.get("Monkey");

                //From a given object get all meshes which should be rendered with the given material (in this case there is only one mesh which uses the single material defined in the file).
                Mesh mesh = monkey.getMesh(0);

                manager = new MeshDataManager();
                monkeyHandles = new MeshHandle[numMonkeys];
                monkeyHandles[0] = manager.addStaticMesh(
                        mesh,
                        colorTechnique,
                        new RenderPropertyValue[]
                                {
                                        new RenderPropertyValue(RenderProperty.MAT_COLOR, new float[]{0f, 1f, 1f, 1f}),
                                        new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, new float[]{1f, 1f, 1f, 1f}),
                                        new RenderPropertyValue(RenderProperty.SHININESS, 100f)
                                });
                for (int i = 1; i < numMonkeys; i++)
                {
                    monkeyHandles[i] = monkeyHandles[0].copy();
                }
                monkeyGroupHandle = new ObjectHandle();
                monkeyGroupHandle.setMeshHandles(monkeyHandles);
                cubeDepthRenderer.add(monkeyGroupHandle);
                EnumMap<VertexAttribute, float[]> data = new EnumMap<>(VertexAttribute.class);
                data.put(VertexAttribute.POSITION4, MeshGenUtils.genSingleQuadPositionData(0, 0, 0, 1f, VertexAttribute.POSITION4));
                data.put(VertexAttribute.NORMAL3, new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1});
                screenQuad = new Mesh(6, data, MeshGenUtils.genSingleQuadIndexData());
                screenHandle = manager.addStaticMesh(screenQuad,
                        colorTechnique,
                        new RenderPropertyValue[]
                                {
                                        new RenderPropertyValue(RenderProperty.MAT_COLOR, new float[]{0.5f, 0.5f, 0.5f, 0.155f}),
                                        new RenderPropertyValue(RenderProperty.SPEC_MAT_COLOR, new float[]{1f, 1f, 1f, 0.5f}),
                                        new RenderPropertyValue(RenderProperty.SHININESS, 5f)
                                });

                screenHandle.setProperty(RenderProperty.MODEL_MATRIX, Matrix4.newIdentity());
                manager.packAndTransfer();
                for (int i = 0; i < numMonkeys; i++)
                {
                    Matrix4 transform = Matrix4.newTranslation(0.0f, 0.0f, 0.5f - i * 0.25f);
                    transform.scaleBy(0.1f, 0.1f, 0.1f);
                    monkeyHandles[i].setProperty(RenderProperty.MODEL_MATRIX, transform);
                }
            }
            catch (IOException | XMLParseException e)
            {
                throw new GraphicsException(e);
            }
        }


        public void faceMissing()
        {
            Log.i("Face", "Missing");
        }

        public void updateFace(float width, float height, float eulerY, float eulerZ, PointF position, PointF leftEyePosition)
        {
            //Screen Width 4.75" Height 6.5"
            //ScreenCenter - CameraCenter: (-13/16",-3.75") , (-1/4.75,-3.75/6.5)
            //Face size diagonal at 9": 1.0905
            //9"/3.25" = 2.77 (1/2 screen height's) away (z=2.77)

//            if (leftEyePosition != null)
//            {
//                float screenX = leftEyePosition.x;
//                float screenY = leftEyePosition.y;
//                float offsetX = -0.10865198f;
//                float offsetY = -0.25f;
            Matrix4 currentOrientation = orientationTracker.getCurrentOrientation();
            if (eyeBuffer != null && currentOrientation != null)
            {
                eyeBuffer.updateEyes(currentOrientation);
                //orientationTracker.zeroOrientationMatrix();
                float screenX = position.x;
                float screenY = position.y;
                float offsetX = 0.47f;
                float offsetY = 0.52f;
                float eyeX = (screenX - CAMERA_WIDTH / 2) / CAMERA_HEIGHT;
                float eyeY = (screenY - CAMERA_HEIGHT / 2) / CAMERA_HEIGHT;
                float faceDiagonal = (float) (Math.sqrt((width / CAMERA_WIDTH) * (width / CAMERA_WIDTH) + (height / CAMERA_HEIGHT) * (height / CAMERA_HEIGHT)));
                float eyeZ = 2.77f * 1.0905f / faceDiagonal;
                eyeX = eyeX * eyeZ + offsetX;
                eyeY = eyeY * eyeZ + offsetY;
                Vec4F eye = new Vec4F(1);
                eye.set(-eyeX, -eyeY, eyeZ, 1);
                eyeBuffer.addEye(eye);
            }
//            }
//            offsetX += eyeX;
//            offsetY += eyeY;
//            numSamples++;
//            Log.i("Face", "(" + offsetX / numSamples + ", " + offsetY / numSamples + ") " + numSamples);


//            Log.i("Face", "(" + eyeX + ", " + eyeY + ", " + eyeZ + ")");
            //Log.i("Face", "" + leftEyePosition.y);
        }

        private static class HoloCamera
        {
            private static final float screenHeight = 1;
            Matrix4 viewMatrix;
            Matrix4 projectionMatrix;

            /**
             * @param eyeX
             * @param eyeY
             * @param eyeZ
             * @param aspect ratio of width to height
             */
            HoloCamera(float eyeX, float eyeY, float eyeZ, float aspect)
            {
                float near = 1.0f;
                float far = 100.0f;
                Vec4F screenCenter = new Vec4F(1);
                screenCenter.setX(-eyeX);
                screenCenter.setY(-eyeY);
                screenCenter.setZ(-eyeZ);
                screenCenter.setW(1);
                viewMatrix = Matrix4.newTranslation(screenCenter.x(), screenCenter.y(), screenCenter.z());

                float screenWidth = aspect * screenHeight;

                //Where the center of the screen will be projected.  All projected (x,y) screen coordinates should be offset by this
                float projectedCenterX = screenCenter.x() / -screenCenter.z();
                float projectedCenterY = screenCenter.y() / -screenCenter.z();

                //Once screen coordinates are offset, they should be scaled based on projected screen dimensions
                //We want to scale coordinates to ranges y: [-1,1], x: [-aspect, aspect]
                float halfProjectedWidth = screenWidth / (2 * -screenCenter.z());
                float halfProjectedHeight = screenHeight / (2 * -screenCenter.z());

                //xProjected = (x/z - projectedCenterX) / projectedWidth
                //xProjected = (x/z) / projectedWidth - projectedCenterX / projectedWidth
                //xProjected = (x/z) * xScale + xOffset

                //Scale by inverse of 1/2 width
                float xScale = 1 / halfProjectedWidth;

                //Offset is positive, because it will be multiplied by z, but then divided by -z
                float xOffset = projectedCenterX / halfProjectedWidth;

                //Scale by inverse of 1/2 height
                float yScale = 1 / halfProjectedHeight;

                //Offset is positive, because it will be multiplied by z, but then divided by -z
                float yOffset = projectedCenterY / halfProjectedHeight;

                float[] m = new float[16];
                //@formatter:off
                m[0] = xScale;  m[4] = 0;       m[8] =  xOffset;                        m[12] = 0;
                m[1] = 0;       m[5] = yScale;  m[9] =  yOffset;                        m[13] = 0;
                m[2] = 0;       m[6] = 0;       m[10] = -(far + near) / (far - near);   m[14] = -2 * far * near / (far - near);
                m[3] = 0;       m[7] = 0;       m[11] = -1;                             m[15] = 0;
                //@formatter:on
                projectionMatrix = Matrix4.newFromFloats(m);
            }
        }

        @Override
        protected void onDrawFrame(Projection projection, EuclideanCamera camera) throws GraphicsException
        {

            //Bind main frame buffer
            FrameBuffer.main.bind(0, 0, getSurfaceWidth(), getSurfaceHeight());
            GLES20.glViewport(0, 0, getSurfaceWidth(), getSurfaceHeight());

            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glClearDepthf(1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            Matrix4 orientationMatrix = orientationTracker.getCurrentOrientation();
            if (orientationMatrix == null)
            {
                return;
            }

            Vec4F eye = absEyePosition.copy();
            eye.transformBy(orientationMatrix);
            HoloCamera holoCamera = new HoloCamera(eye.x(), eye.y(), eye.z(), ((float) getSurfaceWidth() / getSurfaceHeight()));

//            Vec4F eye = eyeBuffer.getEye(orientationMatrix);
//            if (eye == null)
//            {
//                return;
//            }
//            Log.i("Face", "(" + eye.x() + ", " + eye.y() + ", " + eye.z() + ")");

            Vec4F positions = lighting.getPositions().copy();
            positions.transformByAll(orientationMatrix);
            positions.setIndex(0);
            //cubeDepthRenderer.render(positions.x(), positions.y(), positions.z(), cubeDepthTextures[0]);

            //Bind main frame buffer
            FrameBuffer.main.bind(0, 0, getSurfaceWidth(), getSurfaceHeight());
            GLES20.glViewport(0, 0, getSurfaceWidth(), getSurfaceHeight());

            colorTechnique.setProperty(RenderProperty.PROJECTION_MATRIX, holoCamera.projectionMatrix);
            colorTechnique.setProperty(RenderProperty.VIEW_MATRIX, holoCamera.viewMatrix);
            colorTechnique.setProperty(RenderProperty.LIGHTING, lighting);
            colorTechnique.setProperty(RenderProperty.DEPTH_Z_CONST, cubeDepthRenderer.getDepthZConst());
            colorTechnique.setProperty(RenderProperty.DEPTH_Z_FACTOR, cubeDepthRenderer.getDepthZFactor());
            colorTechnique.setProperty(RenderProperty.CUBE_DEPTH_TEXTURES, cubeDepthTextures);

            colorTechnique.applyConstantProperties();
            for (int i = 0; i < numMonkeys; i++)
            {
                monkeyHandles[i].drawTriangles();
            }
            screenHandle.drawTriangles();
//            monkeyHandle.drawTriangles();
//            monkeyHandle2.drawTriangles();
//
//            transform3.rotateBy(1, 0, 0, 1);
//            monkeyHandle3.setProperty(RenderProperty.MODEL_MATRIX, transform3);
//            monkeyHandle3.drawTriangles();
        }

        @Override
        public void onSurfaceResize(int width, int height)
        {
            super.onSurfaceResize(width, height);
        }

        @Override
        public void onTouchTransformEvent(TouchAnalyzer.TouchTransformEvent event)
        {
            if (event.numPointers == 1)
            {
                //Scale distance of eye from the screen
                absEyePosition.setZ((float) (absEyePosition.z() + event.transform.translation.x / 100.0f));
            }
        }

        @Override
        protected void onSensorChanged(int type, float[] values, long timestamp)
        {
            switch (type)
            {
                case Sensor.TYPE_ROTATION_VECTOR:
                    orientationTracker.onSensorChanged(values);
                    break;
            }
        }
    }

}